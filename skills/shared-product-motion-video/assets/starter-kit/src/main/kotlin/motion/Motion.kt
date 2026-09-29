package motion

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

// Everything in a film is a pure function of time t (seconds). There is no animation state: a
// frame at 12.5 s looks the same whether you render the whole film or just that still.

/** 0..1 progress of [t] through the window starting at [start] lasting [duration] seconds. */
fun window(t: Float, start: Float, duration: Float): Float = ((t - start) / duration).coerceIn(0f, 1f)

/** Entrances: fast out, soft landing. The default for anything appearing. */
fun easeOutCubic(x: Float) = 1f - (1f - x).pow(3)

/** Moves between two places: camera moves, cursor travel, loops being drawn. */
fun easeInOutCubic(x: Float) = if (x < 0.5f) 4f * x * x * x else 1f - (-2f * x + 2f).pow(3) / 2f

/** Snappier entrance, available but rarely needed. */
fun easeOutExpo(x: Float) = if (x >= 1f) 1f else 1f - 2f.pow(-10f * x)

/** Exits: slow start, then gone. */
fun easeInCubic(x: Float) = x * x * x

/** Overshoots and settles: the pop of a card or a word landing. 1.4 for words, 1.8 to 2 for cards and chips. */
fun easeOutBack(x: Float, overshoot: Float = 1.70158f): Float {
    val c3 = overshoot + 1f
    return 1f + c3 * (x - 1f).pow(3) + overshoot * (x - 1f).pow(2)
}

/** A damped spring from 0 to 1 over roughly a second: the wobble of a state change or an icon arriving. */
fun spring(x: Float, damping: Float = 7f, frequency: Float = 2.2f): Float =
    if (x <= 0f) 0f else (1f - exp(-damping * x) * cos(2f * PI.toFloat() * frequency * x))

fun lerp(a: Float, b: Float, x: Float) = a + (b - a) * x

/** A gentle idle float for things waiting on screen. The house value is 0.35 Hz, 4 dp. */
fun bob(t: Float, speed: Float = 1f, amount: Float = 1f) = sin(t * speed * 2f * PI.toFloat()) * amount

/** A press: scale dips by [depth] and comes back over the press window (0..1). */
fun pressScale(p: Float, depth: Float = 0.07f) = 1f - sin(p * PI.toFloat()) * depth

/** Visibility of a scene with a fade in and out: 0 outside, 1 inside. */
fun sceneAlpha(t: Float, start: Float, end: Float, fadeIn: Float = 0.3f, fadeOut: Float = 0.35f): Float = when {
    t < start || t > end -> 0f
    t < start + fadeIn -> easeOutCubic((t - start) / fadeIn)
    t > end - fadeOut -> 1f - easeInCubic((t - (end - fadeOut)) / fadeOut)
    else -> 1f
}

/** Characters of [text] visible at [t] when typing starts at [start] at [perChar] seconds a character. */
fun typed(text: String, t: Float, start: Float, perChar: Float): String =
    text.take(((t - start) / perChar).toInt().coerceIn(0, text.length))

/** The block caret: solid while typing, blinking at 1 Hz once idle. */
fun caret(t: Float, typing: Boolean): String = if (typing || (t * 2f).toInt() % 2 == 0) "▍" else " "

/** Typing speeds, seconds per character. The sound's key ticks use the same numbers. */
object TypeSpeed {
    const val FIELD = 0.045f // a person filling a form field
    const val PROMPT = 0.038f // a hero prompt in a promo
    const val ASSISTANT = 0.032f // a longer request typed into an assistant or terminal
}
