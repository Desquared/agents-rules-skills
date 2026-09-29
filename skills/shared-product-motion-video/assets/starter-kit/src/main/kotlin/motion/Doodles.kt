package motion

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

// Hand-drawn marks. Each is a path revealed from 0 to [progress], like a pen stroke. They are the
// voice of a person annotating the product: use one or two per scene, never as decoration.

/** Draw in dp units (the film is laid out on a 960 x 540 dp page). */
inline fun DrawScope.inDp(block: DrawScope.() -> Unit) = scale(density, density, Offset.Zero) { block() }

/** A path drawn on up to [progress]. Round caps and joins; ink; 2.2 to 2.8 dp wide. */
fun DrawScope.pen(path: Path, progress: Float, color: Color = Ink.ink, width: Float = 2.4f) {
    if (progress <= 0f) return
    val measure = PathMeasure().apply { setPath(path, false) }
    val part = Path()
    measure.getSegment(0f, measure.length * progress.coerceAtMost(1f), part, true)
    drawPath(part, color, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** A loose underline with a little wave, from [x1] to [x2] at [y], ending in the flick back people draw. */
fun underlinePath(x1: Float, x2: Float, y: Float): Path = Path().apply {
    moveTo(x1, y + 1f)
    val w = x2 - x1
    cubicTo(x1 + w * 0.3f, y - 3f, x1 + w * 0.6f, y + 4f, x2, y - 1f)
    cubicTo(x2 - w * 0.25f, y + 5f, x2 - w * 0.55f, y + 3f, x1 + w * 0.35f, y + 7f)
}

/** A loose loop around [r] (10 dp and 8 dp outside it), overshooting where the pen meets itself. */
fun loopPath(r: Rect, wobble: Float = 3f): Path = Path().apply {
    val rx = r.width / 2f + 10f
    val ry = r.height / 2f + 8f
    val steps = 64
    for (i in 0..steps) {
        val a = -PI.toFloat() * 0.6f + (i / steps.toFloat()) * 2.15f * PI.toFloat()
        val k = 1f + sin(a * 3f) * wobble / rx
        val x = r.center.x + cos(a) * rx * k
        val y = r.center.y + sin(a) * ry * k
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
}

/** A single strike through, slightly rising: a quick correction of a word. Draw it 5 dp wide. */
fun strikePath(r: Rect): Path = Path().apply {
    moveTo(r.left - 6f, r.center.y + 4f)
    cubicTo(r.left + r.width * 0.3f, r.center.y - 2f, r.left + r.width * 0.7f, r.center.y + 3f, r.right + 8f, r.center.y - 5f)
}

/** A curved arrow from [from] to [to], bending by [bend]. The body draws in the first 85%, the head last. */
fun DrawScope.arrow(from: Offset, to: Offset, progress: Float, bend: Float = 0.25f, color: Color = Ink.ink, width: Float = 2.2f) {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val length = hypot(dx, dy)
    val control = Offset((from.x + to.x) / 2f - dy * bend, (from.y + to.y) / 2f + dx * bend)
    val body = Path().apply {
        moveTo(from.x, from.y)
        quadraticTo(control.x, control.y, to.x, to.y)
    }
    pen(body, (progress / 0.85f).coerceAtMost(1f), color, width)
    val head = ((progress - 0.85f) / 0.15f).coerceIn(0f, 1f)
    if (head > 0f) {
        val angle = atan2(to.y - control.y, to.x - control.x)
        val size = (length * 0.12f).coerceIn(7f, 12f) * head
        listOf(angle + 2.6f, angle - 2.6f).forEach { a ->
            drawLine(color, to, Offset(to.x + cos(a) * size, to.y + sin(a) * size), strokeWidth = width, cap = StrokeCap.Round)
        }
    }
}

/** A sparkle: six short strokes radiating out, each 0.08 of the progress after the last. */
fun DrawScope.sparkle(center: Offset, radius: Float, progress: Float, color: Color = Ink.ink) {
    listOf(-90f, 0f, 90f, 180f, -45f, 45f).forEachIndexed { i, degrees ->
        val p = ((progress - i * 0.08f) / 0.5f).coerceIn(0f, 1f)
        if (p <= 0f) return@forEachIndexed
        val a = degrees / 180f * PI.toFloat()
        val long = if (i < 4) radius else radius * 0.55f
        val start = Offset(center.x + cos(a) * long * 0.35f, center.y + sin(a) * long * 0.35f)
        val reach = lerp(long * 0.35f, long, easeOutCubic(p))
        drawLine(color, start, Offset(center.x + cos(a) * reach, center.y + sin(a) * reach), strokeWidth = 2.2f, cap = StrokeCap.Round)
    }
}

/** A hand-drawn tick, green by default: the only way "done" is shown. */
fun DrawScope.tick(at: Offset, size: Float, progress: Float, color: Color = Ink.greenInk) {
    val path = Path().apply {
        moveTo(at.x - size * 0.45f, at.y)
        lineTo(at.x - size * 0.1f, at.y + size * 0.35f)
        lineTo(at.x + size * 0.5f, at.y - size * 0.4f)
    }
    pen(path, progress, color, 2.6f)
}

/** A five-point star outline: "this one". */
fun DrawScope.star(center: Offset, radius: Float, progress: Float, color: Color = Ink.ink) {
    val path = Path()
    for (i in 0..10) {
        val r = if (i % 2 == 0) radius else radius * 0.45f
        val a = -PI.toFloat() / 2f + i * PI.toFloat() / 5f
        val p = Offset(center.x + cos(a) * r, center.y + sin(a) * r)
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    pen(path, progress, color, 2f)
}

/** The macOS arrow cursor at [tip]. While [press] runs 0..1 it shrinks 12% and throws a blue ripple. */
fun DrawScope.cursor(tip: Offset, press: Float = 0f, alpha: Float = 1f) {
    if (alpha <= 0f) return
    val s = 1f - press * 0.12f
    val points = listOf(0f to 0f, 0f to 17f, 4.2f to 13.2f, 7f to 19.6f, 9.6f to 18.5f, 6.9f to 12.3f, 12.4f to 12.3f)
    val path = Path().apply {
        points.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(tip.x + x * s, tip.y + y * s) else lineTo(tip.x + x * s, tip.y + y * s) }
        close()
    }
    drawPath(path, Color.White.copy(alpha = alpha), style = Stroke(width = 3f, join = StrokeJoin.Round))
    drawPath(path, Color.Black.copy(alpha = alpha), style = Fill)
    if (press > 0f) drawCircle(Ink.blueInk.copy(alpha = 0.25f * (1f - press) * alpha), radius = 6f + 18f * press, center = tip)
}

/** A highlighter sweep behind the lower 42% of the text, left to right as [progress] runs 0..1. */
fun Modifier.marker(progress: Float, color: Color = Ink.yellow): Modifier = drawBehind {
    val pad = 8.dp.toPx()
    drawRoundRect(
        color,
        topLeft = Offset(-pad, size.height * 0.5f),
        size = Size((size.width + pad * 2) * progress, size.height * 0.42f),
        cornerRadius = CornerRadius(6.dp.toPx()),
    )
}
