package motion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The tutorial toolkit: real product footage under a moving camera, a cursor and doodles pinned to
// the footage, plus the cards a walkthrough needs. All coordinates on footage are FRACTIONS of the
// image (0..1), so re-capturing at another resolution changes nothing.

/** A camera keyframe: centre (fractions of the footage) and zoom. Moves between keys ease in-out. */
data class Cam(val t: Float, val cx: Float, val cy: Float, val s: Float)

/** Which footage image is on screen from [t]. Consecutive shots dissolve over 0.35 s. */
data class Shot(val t: Float, val image: ImageBitmap)

/** A cursor keyframe in footage fractions. With [click] it presses at [t] and then rests for 0.6 s. */
data class Point(val t: Float, val x: Float, val y: Float, val click: Boolean = false)

/** A hand-drawn loop around a footage region (fractions), drawn over 0.5 s from [t]. */
data class Loop(val t: Float, val region: Rect)

fun camAt(keys: List<Cam>, t: Float): Cam {
    if (t <= keys.first().t) return keys.first()
    val next = keys.indexOfFirst { it.t > t }
    if (next < 0) return keys.last()
    val a = keys[next - 1]
    val b = keys[next]
    val p = easeInOutCubic((t - a.t) / (b.t - a.t))
    return Cam(t, lerp(a.cx, b.cx, p), lerp(a.cy, b.cy, p), lerp(a.s, b.s, p))
}

/** The visible part of the footage (fractions) for [cam], kept inside the image's top [usable] fraction. */
private fun region(cam: Cam, usable: Float): Rect {
    val w = 1f / cam.s
    val h = usable / cam.s
    val left = (cam.cx - w / 2).coerceIn(0f, 1f - w)
    val top = (cam.cy - h / 2).coerceIn(0f, usable - h)
    return Rect(left, top, left + w, top + h)
}

/**
 * Real product footage in a macOS window at ([x], [y]) [width] dp wide, with a camera, a cursor and
 * loops that stay pinned to the footage as the camera moves. [usable] hides a bottom strip of the
 * capture (a status bar, a dock) that should never be seen.
 */
@Composable
fun Footage(
    t: Float,
    x: Float,
    y: Float,
    width: Float,
    title: String,
    shots: List<Shot>,
    cams: List<Cam>,
    points: List<Point> = emptyList(),
    loops: List<Loop> = emptyList(),
    enter: Float = 1f,
    usable: Float = 1f,
) {
    val first = shots.first().image
    val contentH = width * first.height / first.width * usable
    val view = region(camAt(cams, t), usable)
    val keys = points.flatMap { if (it.click) listOf(it, it.copy(t = it.t + 0.6f, click = false)) else listOf(it) }
    fun toWindow(fx: Float, fy: Float) = Offset((fx - view.left) / view.width * width, (fy - view.top) / view.height * contentH)
    MacWindow(x, y, width, contentH + 24f, title, Modifier.enter(enter, rise = 50f)) {
        val current = shots.indexOfLast { t >= it.t }.coerceAtLeast(0)
        val fade = if (current > 0) window(t, shots[current].t, 0.35f) else 1f
        fun crop(img: ImageBitmap) = Rect(view.left * img.width, view.top * img.height, view.right * img.width, view.bottom * img.height)
        if (current > 0 && fade < 1f) Picture(shots[current - 1].image, Modifier.fillMaxSize(), crop(shots[current - 1].image))
        Picture(shots[current].image, Modifier.fillMaxSize().graphicsLayer { alpha = fade }, crop(shots[current].image))
        Canvas(Modifier.fillMaxSize()) {
            inDp {
                loops.forEach { loop ->
                    val a = toWindow(loop.region.left, loop.region.top)
                    val b = toWindow(loop.region.right, loop.region.bottom)
                    pen(loopPath(Rect(a, b)), easeInOutCubic(window(t, loop.t, 0.5f)), Ink.ink, 2.6f)
                }
                if (keys.isNotEmpty() && t >= keys.first().t - 0.25f) {
                    val next = keys.indexOfFirst { it.t > t }
                    val tip = when {
                        next < 0 -> keys.last().let { toWindow(it.x, it.y) }
                        next == 0 -> keys.first().let { toWindow(it.x, it.y) }
                        else -> {
                            val a = keys[next - 1]
                            val b = keys[next]
                            val p = easeInOutCubic(((t - a.t) / (b.t - a.t)).coerceIn(0f, 1f))
                            toWindow(lerp(a.x, b.x, p), lerp(a.y, b.y, p))
                        }
                    }
                    val press = keys.filter { it.click }.maxOfOrNull { if (t in it.t..(it.t + 0.3f)) window(t, it.t, 0.3f) else 0f } ?: 0f
                    cursor(tip, press, alpha = window(t, keys.first().t - 0.25f, 0.25f))
                }
            }
        }
    }
}

/** Numbered step dots top right; the current one filled ink, done ones grey. [starts] are film times. */
@Composable
fun StepDots(t: Float, starts: List<Float>, visibleFrom: Float, visibleTo: Float) {
    val alpha = window(t, visibleFrom, 0.4f) * (1f - window(t, visibleTo - 0.4f, 0.4f))
    if (alpha <= 0f) return
    val current = starts.indexOfLast { t >= it }
    Row(
        Modifier.at(PAGE_WIDTH - 24f - (34f + starts.size * 30f), 24f).graphicsLayer { this.alpha = alpha },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText("Step", style = body(11.sp, Ink.muted))
        starts.indices.forEach { i ->
            val on = i == current
            val done = i < current
            Box(
                Modifier.size(22.dp).background(if (on) Ink.ink else if (done) Ink.line else Ink.card, CircleShape).border(1.dp, if (on) Ink.ink else Ink.line, CircleShape),
                contentAlignment = Alignment.Center,
            ) { BasicText("${i + 1}", style = body(10.sp, if (on) Color.White else Ink.muted).copy(fontWeight = FontWeight.Medium)) }
        }
    }
}

/** A tutorial title card: [icon] + a yellow mono tag, a 62 sp title, one italic serif line. */
@Composable
fun TitleCard(t: Float, start: Float, tag: String, title: String, line: String, modifier: Modifier = Modifier, icon: @Composable () -> Unit = {}) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.enter(easeOutCubic(window(t, start, 0.4f)))) {
            icon()
            Spacer(Modifier.width(10.dp))
            Pill(tag, Ink.yellow, mono = true, size = 12.sp)
        }
        Spacer(Modifier.height(22.dp))
        Words(title, t, start + 0.2f, display(62.sp, FontWeight.Black), "title:$title")
        Spacer(Modifier.height(12.dp))
        BasicText(line, Modifier.enter(easeOutCubic(window(t, start + 0.8f, 0.5f))), style = note(24.sp))
    }
}

/** A form field typing [value] from [typeAt]: blue 2 dp focus ring and caret while typing. */
@Composable
fun TypingField(label: String, value: String, typeAt: Float, t: Float, perChar: Float = TypeSpeed.FIELD) {
    val shown = typed(value, t, typeAt, perChar)
    val focused = t >= typeAt - 0.2f && shown.length < value.length
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        BasicText(label, style = body(11.sp, Ink.muted))
        BasicText(
            shown.ifEmpty { " " } + if (focused) caret(t, false) else "",
            Modifier.fillMaxWidth().background(Ink.paper, RoundedCornerShape(8.dp))
                .border(if (focused) 2.dp else 1.dp, if (focused) Ink.blueInk else Ink.line, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            style = body(15.sp, Ink.ink),
        )
    }
}

/**
 * An assistant or terminal, as a light card: context lines pasted at [pasteAt], the request typed from
 * [typeAt], "working" dots from [workAt], a green tick and [doneLine] at [doneAt].
 */
@Composable
fun PromptCard(
    t: Float, start: Float, label: String, context: String, pasted: List<String>, ask: String,
    pasteAt: Float, typeAt: Float, workAt: Float, doneAt: Float, workingLine: String, doneLine: String,
    x: Float, y: Float, width: Float,
) {
    Column(
        Modifier.at(x, y).width(width.dp).enter(easeOutCubic(window(t, start, 0.5f)), rise = 40f).card(14f, 14f).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Pill(label, Ink.purple, mono = true)
            Spacer(Modifier.width(8.dp))
            BasicText(context, style = code(11.sp, Ink.faint))
        }
        Spacer(Modifier.height(4.dp))
        if (t >= pasteAt) pasted.forEach { BasicText(it, Modifier.enter(window(t, pasteAt, 0.2f)), style = code(11.sp)) }
        if (t >= typeAt) {
            val shown = typed(ask, t, typeAt, TypeSpeed.ASSISTANT)
            BasicText("› $shown${caret(t, shown.length < ask.length)}", style = code(13.sp, Ink.ink).copy(lineHeight = 19.sp))
        }
        if (t >= workAt) {
            Spacer(Modifier.height(4.dp))
            if (t < doneAt) {
                BasicText(workingLine + ".".repeat(((t - workAt) * 4).toInt() % 4), style = code(12.sp, Ink.blueInk))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(Modifier.size(16.dp)) { inDp { tick(Offset(8f, 8f), 12f, easeOutCubic(window(t, doneAt, 0.35f))) } }
                    Spacer(Modifier.width(6.dp))
                    BasicText(doneLine, style = code(12.sp, Ink.greenInk))
                }
            }
        }
    }
}

/** [before] turns into [after] at [at]: 0.45 s dissolve, a spring bump from 0.9, a sparkle, the label turns green. */
@Composable
fun Morph(t: Float, at: Float, before: ImageBitmap, after: ImageBitmap, x: Float, y: Float, width: Float, height: Float, beforeLabel: String, afterLabel: String, stage: Boolean = false) {
    val p = window(t, at, 0.45f)
    val bump = if (t < at) 1f else lerp(0.9f, 1f, spring(window(t, at, 0.8f), 7f, 2.2f))
    @Composable
    fun fitted(image: ImageBitmap, alpha: Float) {
        val scale = minOf(width / image.width, height / image.height)
        Picture(image, Modifier.size((image.width * scale).dp, (image.height * scale).dp).graphicsLayer { this.alpha = alpha })
    }
    val card = if (stage) Modifier.card(14f, 10f).padding(10.dp) else Modifier
    Box(Modifier.at(x, y).size(width.dp, height.dp).graphicsLayer { scaleX = bump; scaleY = bump }.then(card), contentAlignment = Alignment.Center) {
        if (p < 1f) fitted(before, 1f - p)
        if (p > 0f) fitted(after, p)
    }
    val done = t >= at + 0.2f
    Pill(if (done) afterLabel else beforeLabel, if (done) Ink.green else Ink.line, Modifier.at(x, y + height + 10f), mono = true, size = 11.sp)
    if (t >= at) Canvas(Modifier.fillMaxSize()) { inDp { sparkle(Offset(x + width - 6f, y + 4f), 18f, window(t, at + 0.1f, 0.6f)) } }
}

/** A few lines of a real source file, each line landing 0.08 s after the last. */
@Composable
fun CodeCard(t: Float, start: Float, file: String, lines: List<String>, x: Float, y: Float, width: Float) {
    Column(Modifier.at(x, y).width(width.dp).enter(easeOutCubic(window(t, start, 0.45f)), rise = 30f).card(12f, 10f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Pill(file, Ink.blue, mono = true, size = 10.sp)
        Spacer(Modifier.height(4.dp))
        lines.forEachIndexed { i, line -> BasicText(line, Modifier.enter(window(t, start + 0.15f + i * 0.08f, 0.25f)), style = code(10.5.sp, Ink.ink).copy(lineHeight = 15.sp)) }
    }
}

/** A result card (a pull request, a published page, a passed review): status pill, title, a line, then ticks 0.3 s apart. */
@Composable
fun ChecklistCard(t: Float, start: Float, status: String, title: String, line: String, checks: List<String>, x: Float, y: Float, width: Float = 430f) {
    Column(
        Modifier.at(x, y).width(width.dp).enter(easeOutCubic(window(t, start, 0.55f)), rise = 50f).card(14f, 18f).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Pill(status, Ink.green, color = Ink.greenInk)
            Spacer(Modifier.width(10.dp))
            BasicText(title, style = display(17.sp, FontWeight.SemiBold))
        }
        BasicText(line, style = code(11.sp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Ink.line))
        checks.forEachIndexed { i, check ->
            val at = start + 0.6f + i * 0.3f
            Row(Modifier.enter(window(t, at, 0.3f)), verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.size(18.dp)) { inDp { tick(Offset(9f, 9f), 13f, easeOutCubic(window(t, at, 0.35f))) } }
                Spacer(Modifier.width(8.dp))
                BasicText(check, style = body(13.sp, Ink.ink))
            }
        }
    }
}
