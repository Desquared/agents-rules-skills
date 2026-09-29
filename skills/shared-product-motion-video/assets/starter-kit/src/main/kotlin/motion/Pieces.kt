package motion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Where named things landed on the page this frame (dp), so doodles and the cursor can find words,
 * buttons and chips instead of hard-coded coordinates. Filled during layout, read while drawing: this
 * is why every frame is rendered twice (see Render.kt). Keys must be unique across overlapping scenes.
 */
object Marks {
    private val rects = HashMap<String, Rect>()
    fun put(key: String, rect: Rect) { rects[key] = rect }
    operator fun get(key: String): Rect? = rects[key]
}

fun Modifier.at(x: Float, y: Float): Modifier = offset(x.dp, y.dp)

/** Rise-and-fade in over [p] (0..1): the house entrance for everything. Feed it an eased progress. */
fun Modifier.enter(p: Float, rise: Float = 18f, scaleFrom: Float = 1f): Modifier = graphicsLayer {
    alpha = p.coerceIn(0f, 1f)
    translationY = (1f - p) * rise * density
    val s = lerp(scaleFrom, 1f, p)
    scaleX = s
    scaleY = s
}

/** A pop-in: fade over the first 0.2 s, scale from [from] with an overshoot. Cards, phones, chips. */
fun Modifier.pop(t: Float, start: Float, from: Float = 0.6f, overshoot: Float = 1.8f, rise: Float = 30f): Modifier = graphicsLayer {
    val p = easeOutBack(window(t, start, 0.55f), overshoot)
    alpha = window(t, start, 0.2f)
    scaleX = lerp(from, 1f, p)
    scaleY = lerp(from, 1f, p)
    translationY = (1f - p) * rise * density
}

fun Modifier.mark(key: String): Modifier = onGloballyPositioned { coordinates ->
    val r = coordinates.boundsInRoot()
    Marks.put(key, Rect(r.left / DENSITY, r.top / DENSITY, r.right / DENSITY, r.bottom / DENSITY))
}

/** An image drawn crisp at any size, optionally cropped to [crop] (source pixels). */
@Composable
fun Picture(image: ImageBitmap, modifier: Modifier, crop: Rect? = null) {
    Canvas(modifier) {
        val src = crop ?: Rect(0f, 0f, image.width.toFloat(), image.height.toFloat())
        drawImage(
            image,
            srcOffset = IntOffset(src.left.roundToInt(), src.top.roundToInt()),
            srcSize = IntSize(src.width.roundToInt(), src.height.roundToInt()),
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
            filterQuality = FilterQuality.High,
        )
    }
}

/** Width of a [Device] of [height] dp showing [image]. */
fun deviceWidth(image: ImageBitmap, height: Float, bezel: Boolean = true): Float {
    val b = if (bezel) height * BEZEL else 0f
    return (height - 2f * b) * image.width / image.height + 2f * b
}

private const val BEZEL = 0.018f

/**
 * A phone at [height] dp. With [bezel] a plain black device frame is drawn around a raw screenshot;
 * pass false when the capture already includes its device shell.
 */
@Composable
fun Device(image: ImageBitmap, x: Float, y: Float, height: Float, modifier: Modifier = Modifier, bezel: Boolean = true, island: Boolean = true) {
    val width = deviceWidth(image, height, bezel)
    Canvas(modifier.at(x, y).size(width.dp, height.dp)) {
        val b = if (bezel) size.height * BEZEL else 0f
        val screen = Rect(b, b, size.width - b, size.height - b)
        if (bezel) drawRoundRect(Ink.ink, cornerRadius = CornerRadius(size.width * 0.15f))
        val clip = Path().apply { addRoundRect(RoundRect(screen, CornerRadius(if (bezel) size.width * 0.13f else 0f))) }
        clipPath(clip) {
            drawImage(
                image,
                srcSize = IntSize(image.width, image.height),
                dstOffset = IntOffset(screen.left.roundToInt(), screen.top.roundToInt()),
                dstSize = IntSize(screen.width.roundToInt(), screen.height.roundToInt()),
                filterQuality = FilterQuality.High,
            )
        }
        if (bezel && island) {
            val w = screen.width * 0.3f
            val h = screen.height * 0.034f
            drawRoundRect(Color.Black, Offset(screen.center.x - w / 2f, screen.top + screen.height * 0.013f), Size(w, h), CornerRadius(h / 2f))
        }
    }
}

/** A light macOS window around [content]: 24 dp title bar, traffic lights, 12 dp radius, soft shadow. */
@Composable
fun MacWindow(x: Float, y: Float, width: Float, height: Float, title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier
            .at(x, y)
            .size(width.dp, height.dp)
            .shadow(22.dp, shape, ambientColor = Color(0x33000000), spotColor = Color(0x44000000))
            .background(Ink.card, shape)
            .clip(shape),
    ) {
        Row(Modifier.fillMaxWidth().height(24.dp).background(Ink.windowBar).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf(Color(0xFFFF5F57), Color(0xFFFEBC2E), Color(0xFF28C840)).forEach {
                Box(Modifier.size(8.dp).background(it, CircleShape))
                Spacer(Modifier.width(5.dp))
            }
            BasicText(title, Modifier.weight(1f), style = TextStyle(fontFamily = Type.text, fontSize = 9.sp, color = Ink.muted, textAlign = TextAlign.Center))
            Spacer(Modifier.width(39.dp))
        }
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))) { content() }
    }
}

/** A tag: pastel background, 6 dp radius, 8 x 3 dp padding, medium weight. Mono for section tags. */
@Composable
fun Pill(text: String, background: Color, modifier: Modifier = Modifier, color: Color = Ink.ink, size: TextUnit = 11.sp, mono: Boolean = false) {
    BasicText(
        text,
        modifier.background(background, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp),
        style = TextStyle(fontFamily = if (mono) Type.mono else Type.text, fontWeight = FontWeight.Medium, fontSize = size, color = color),
    )
}

/** A white card with a hairline and a soft shadow. */
fun Modifier.card(radius: Float = 12f, elevation: Float = 12f): Modifier {
    val shape = RoundedCornerShape(radius.dp)
    return shadow(elevation.dp, shape, ambientColor = Color(0x22000000), spotColor = Color(0x33000000))
        .background(Ink.card, shape)
        .border(1.dp, Ink.line, shape)
}

/** The dark call-to-action pill a cursor clicks. [press] is the 0..1 press window. */
@Composable
fun Cta(label: String, press: Float, modifier: Modifier = Modifier, color: Color = Ink.ink, size: TextUnit = 14.sp) {
    Box(
        modifier.graphicsLayer { val s = pressScale(press); scaleX = s; scaleY = s }
            .background(color, RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 8.dp),
    ) { BasicText(label, style = body(size, Color.White).copy(fontWeight = FontWeight.Medium)) }
}

/**
 * Words that pop in one after another from [start], [stagger] apart (0.09 s; 0.12 s for a wordmark).
 * Each word is marked "<key>:<index>" so a doodle can strike, underline or sparkle it.
 */
@Composable
fun Words(text: String, t: Float, start: Float, style: TextStyle, key: String, modifier: Modifier = Modifier, stagger: Float = 0.09f) {
    Row(modifier) {
        text.split(' ').forEachIndexed { i, word ->
            val p = easeOutBack(window(t, start + i * stagger, 0.5f), 1.4f)
            BasicText(
                if (i == 0) word else " $word",
                Modifier.mark("$key:$i").enter(window(t, start + i * stagger, 0.35f), rise = 26f * (1f - p)),
                style = style,
            )
        }
    }
}

/**
 * The left-column caption every product scene uses: a mono tag, a two or three line title, one line of
 * detail. Tag at [start], title lines 0.12 s later and 0.22 s apart, detail at +0.55 s.
 */
@Composable
fun Caption(t: Float, start: Float, tag: String, tagColor: Color, title: List<String>, detail: String, x: Float = 60f, y: Float = 150f, width: Float = 300f, titleSize: Float = 40f) {
    Column(Modifier.at(x, y).width(width.dp)) {
        Pill(tag, tagColor, Modifier.enter(easeOutCubic(window(t, start, 0.4f))), mono = true)
        Spacer(Modifier.height(14.dp))
        title.forEachIndexed { i, line ->
            Words(line, t, start + 0.12f + i * 0.22f, display(titleSize.sp, FontWeight.Bold), "cap:$tag:$i")
        }
        Spacer(Modifier.height(14.dp))
        BasicText(detail, Modifier.enter(easeOutCubic(window(t, start + 0.55f, 0.5f))), style = body(15.sp))
    }
}

/** The film's paper: warm off-white with a faint 24 dp dot grid drifting 3 dp/s, like a design canvas. */
@Composable
fun Paper(t: Float) {
    Canvas(Modifier.fillMaxSize().background(Ink.paper)) {
        inDp {
            val step = 24f
            val drift = (t * 3f) % step
            var y = -step + drift
            while (y < PAGE_HEIGHT + 20f) {
                var x = -step + drift * 0.5f
                while (x < PAGE_WIDTH + 20f) {
                    drawCircle(Ink.dot, radius = 0.9f, center = Offset(x, y))
                    x += step
                }
                y += step
            }
        }
    }
}
