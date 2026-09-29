package motion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// An example film in the house style, about 23 s: hook, brand lockup, one feature beat on a phone,
// one tutorial beat on desktop footage, end card. Every time below is the scene's LOCAL time.
// Replace the three constants and the stand-in product, then build your own storyboard.

const val PRODUCT = "Product" // REPLACE: the product name, exactly as the brand writes it
const val TAGLINE = "Does one thing, " // REPLACE: the tagline, ending before its emphasised word
const val TAGLINE_WORD = "beautifully." // REPLACE: the one word set in italic serif and underlined
const val CTA = "product.example" // REPLACE: the one thing to do next (a URL, a command, a store name)
const val CTA_LINE = "Available today" // REPLACE: where or when it is available

fun exampleFilm(a: SampleAssets) = Film(
    listOf(
        Scene("hook", 4.4f, cues = {
            listOf(0.15f, 0.24f, 0.55f, 0.64f).forEach { pop(it, 0.09f) }
            scribble(1.45f, 0.35f, 0.25f)
            listOf(2.65f, 2.8f, 2.89f, 2.98f).forEach { pop(it, 0.1f) }
            scribble(3.05f, 0.45f, 0.12f)
        }) { t -> Hook(t) },
        Scene("lockup", 4.4f, cues = {
            arpeggio(0.25f, listOf(84, 88, 91), gap = 0.075f, level = 0.11f)
            scribble(1.4f, 0.45f, 0.12f)
            repeat(3) { pop(1.65f + it * 0.14f, 0.12f) }
        }) { t -> Lockup(t, 0.2f, "lockup") },
        Scene("feature", 5.6f, cues = {
            pop(0.3f)
            scribble(1.7f, 0.5f)
            click(2.3f)
            chime(2.45f, 88)
            scribble(2.9f, 0.35f)
        }) { t -> Feature(t, a) },
        Scene("walkthrough", 6.8f, cues = {
            scribble(2.6f, 0.5f)
            click(3.2f)
            pop(3.9f, 0.1f)
        }) { t -> Walkthrough(t, a) },
        Scene("end", 3.4f, cues = {
            arpeggio(0.05f, listOf(72, 79, 84, 88), gap = 0.08f, level = 0.1f)
            scribble(0.9f, 0.5f, 0.12f)
        }) { t -> EndCard(t) },
    ),
)

// ---------------------------------------------------------------- 1. Hook: a problem struck out, the answer highlighted

@Composable
private fun Hook(t: Float) {
    Box(Modifier.fillMaxSize()) {
        val out = easeInCubic(window(t, 2.35f, 0.4f))
        Column(Modifier.align(Alignment.Center).graphicsLayer { alpha = 1f - out; translationY = -out * 60f * density }, horizontalAlignment = Alignment.CenterHorizontally) {
            Words("Stop describing", t, 0.15f, display(66.sp, FontWeight.Black), "hook1")
            Words("your product.", t, 0.55f, display(66.sp, FontWeight.Black), "hook2")
        }
        Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.Bottom) {
            Words("Show", t, 2.65f, display(74.sp, FontWeight.Black), "hook3")
            BasicText(" ", style = display(74.sp, FontWeight.Black))
            Box(Modifier.marker(easeInOutCubic(window(t, 3.05f, 0.45f)))) {
                Words("the real thing.", t, 2.8f, display(74.sp, FontWeight.Black), "hook4")
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            inDp {
                Marks["hook1:1"]?.let { pen(strikePath(it), easeOutCubic(window(t, 1.45f, 0.35f)), Ink.ink.copy(alpha = 1f - out), 5f) }
                Marks["hook4:2"]?.let { sparkle(Offset(it.right + 22f, it.top + 6f), 20f, window(t, 3.4f, 0.6f)) }
                Marks["hook3:0"]?.let { sparkle(Offset(it.left - 24f, it.bottom - 8f), 12f, window(t, 3.6f, 0.6f)) }
            }
        }
    }
}

// ---------------------------------------------------------------- 2. Lockup: icon on a spring, wordmark, tagline, chips

/** A placeholder app icon: brand square, the product's initial. Use the real icon, rendered crisp. */
@Composable
private fun AppIcon(size: Float, modifier: Modifier = Modifier) {
    Box(modifier.size(size.dp).background(Ink.brand, RoundedCornerShape((size * 0.26f).dp)), contentAlignment = Alignment.Center) {
        BasicText(PRODUCT.take(1), style = display((size * 0.55f).sp, FontWeight.Black, Color.White))
    }
}

@Composable
private fun Lockup(t: Float, start: Float, key: String, chips: Boolean = true) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            val icon = spring(window(t, start, 1.1f), damping = 6f, frequency = 1.6f)
            AppIcon(
                88f,
                Modifier.graphicsLayer {
                    scaleX = lerp(0.3f, 1f, icon); scaleY = lerp(0.3f, 1f, icon); alpha = window(t, start, 0.2f); rotationZ = (1f - icon) * -18f
                }.mark("$key:icon"),
            )
            Spacer(Modifier.height(22.dp))
            Words(PRODUCT, t, start + 0.35f, display(70.sp, FontWeight.Black), "$key:brand", stagger = 0.12f)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.enter(easeOutCubic(window(t, start + 0.8f, 0.5f))), verticalAlignment = Alignment.Bottom) {
                BasicText(TAGLINE, style = body(23.sp))
                BasicText(TAGLINE_WORD, Modifier.mark("$key:word"), style = note(25.sp))
            }
        }
        if (chips) {
            listOf(
                Triple("Real UI", Ink.green, Offset(118f, 118f)),
                Triple("Every state", Ink.blue, Offset(700f, 96f)),
                Triple("Built in", Ink.purple, Offset(96f, 404f)),
                Triple("Android + iOS", Ink.orange, Offset(690f, 420f)),
            ).forEachIndexed { i, (label, color, at) ->
                val s = start + 1.45f + i * 0.14f
                val p = easeOutBack(window(t, s, 0.5f), 2f)
                Pill(label, color, Modifier.at(at.x, at.y + bob(t + i * 0.3f, 0.35f, 4f)).graphicsLayer { alpha = window(t, s, 0.2f); scaleX = p; scaleY = p }, size = 15.sp)
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            inDp {
                Marks["$key:icon"]?.let {
                    sparkle(Offset(it.right + 14f, it.top - 4f), 16f, window(t, start + 0.6f, 0.6f))
                    sparkle(Offset(it.left - 18f, it.bottom - 10f), 10f, window(t, start + 0.75f, 0.6f))
                }
                Marks["$key:word"]?.let { pen(underlinePath(it.left, it.right, it.bottom + 2f), easeOutCubic(window(t, start + 1.2f, 0.45f)), Ink.ink, 2.6f) }
            }
        }
    }
}

// ---------------------------------------------------------------- 3. Feature: caption left, real UI right, cursor, loop, result

@Composable
private fun Feature(t: Float, a: SampleAssets) {
    Box(Modifier.fillMaxSize()) {
        Caption(t, 0.15f, "Feature", Ink.blue, listOf("One screen.", "One claim."), "Say what it does in a line, then show it doing it on the real product.")
        val x = 542f
        val y = 40f
        val h = 460f
        val enter = easeOutCubic(window(t, 0.3f, 0.5f))
        val swap = window(t, 2.4f, 0.35f)
        Device(a.screen, x, y, h, Modifier.enter(enter, rise = 30f))
        if (swap > 0f) Device(a.screenDone, x, y, h, Modifier.graphicsLayer { alpha = swap })
        // Footage fractions to page dp, through the bezel.
        val b = h * 0.018f
        val screenH = h - 2f * b
        val screenW = screenH * a.screen.width / a.screen.height
        fun onScreen(fx: Float, fy: Float) = Offset(x + b + fx * screenW, y + b + fy * screenH)
        val rowTop = onScreen(0.05f, 288f / 852f)
        val rowBottom = onScreen(0.95f, 352f / 852f)
        Canvas(Modifier.fillMaxSize()) {
            inDp {
                pen(loopPath(Rect(rowTop, rowBottom)), easeInOutCubic(window(t, 1.7f, 0.5f)), Ink.ink, 2.6f)
                val target = onScreen(0.1f, 320f / 852f)
                val move = easeInOutCubic(window(t, 1.0f, 0.6f))
                val tip = Offset(lerp(930f, target.x, move), lerp(520f, target.y, move))
                cursor(tip, if (t in 2.3f..2.6f) window(t, 2.3f, 0.3f) else 0f, alpha = window(t, 1.0f, 0.2f) * (1f - window(t, 3.6f, 0.3f)))
                arrow(Offset(rowBottom.x + 40f, rowBottom.y + 40f), Offset(rowBottom.x + 4f, rowBottom.y + 4f), easeOutCubic(window(t, 2.9f, 0.4f)), bend = 0.3f)
            }
        }
        BasicText("done in one tap", Modifier.at(rowBottom.x + 46f, rowBottom.y + 30f).enter(easeOutCubic(window(t, 3.1f, 0.4f))), style = note())
    }
}

// ---------------------------------------------------------------- 4. Walkthrough: footage, camera push, loop, click, next state

@Composable
private fun Walkthrough(t: Float, a: SampleAssets) {
    Box(Modifier.fillMaxSize()) {
        Caption(t, 0.1f, "Step 1", Ink.blue, listOf("Click + New"), "Top right, on every page. It is the one place to start something.", titleSize = 38f)
        Footage(
            t, 372f, 70f, 560f, PRODUCT,
            shots = listOf(Shot(0f, a.desktop), Shot(3.9f, a.desktopNew)),
            cams = listOf(Cam(0f, .5f, .5f, 1f), Cam(1.6f, .5f, .5f, 1f), Cam(2.8f, .9f, .08f, 2.6f), Cam(3.8f, .9f, .08f, 2.6f), Cam(4.6f, .5f, .5f, 1.3f)),
            points = listOf(Point(1.3f, .8f, .8f), Point(3.2f, .95f, .031f, click = true), Point(4.8f, .56f, .62f)),
            loops = listOf(Loop(2.6f, Rect(.917f, .013f, .983f, .049f))),
            enter = easeOutCubic(window(t, 0f, 0.6f)),
        )
        StepDots(t, listOf(0f, 99f, 99f), 0f, 99f)
    }
}

// ---------------------------------------------------------------- 5. End card: lockup again, one call to action

@Composable
private fun EndCard(t: Float) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.padding(bottom = 30.dp)) { Lockup(t, 0.05f, "end", chips = false) }
        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 58.dp).enter(easeOutCubic(window(t, 1.1f, 0.5f))), verticalAlignment = Alignment.CenterVertically) {
            Pill(CTA, Ink.card, Modifier.border(1.dp, Ink.line, RoundedCornerShape(6.dp)), mono = true, size = 13.sp)
            Spacer(Modifier.width(12.dp))
            Box(Modifier.size(5.dp).background(Ink.faint, CircleShape))
            Spacer(Modifier.width(12.dp))
            BasicText(CTA_LINE, style = body(14.sp))
        }
    }
}
