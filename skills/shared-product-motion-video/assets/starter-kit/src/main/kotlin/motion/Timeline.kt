package motion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * One scene: its length, what it shows, and the sounds it makes. Both [content] and [cues] use the
 * scene's LOCAL time (0 = the scene starts), so picture and sound share one set of numbers.
 */
class Scene(
    val name: String,
    val length: Float,
    val cues: Cues.() -> Unit = {},
    val content: @Composable (t: Float) -> Unit,
)

/**
 * A film is scenes in a row, each overlapping the next by [overlap] seconds so one dissolves into the
 * next: the outgoing scene fades over its last 0.35 s while drifting up 16 dp, the incoming one fades
 * in over [overlap]. The first scene does not fade in and the last does not fade out (it holds).
 * A whoosh is added at every scene change.
 */
class Film(val scenes: List<Scene>, private val overlap: Float = 0.3f) {
    val starts: List<Float> = scenes.runningFold(0f) { at, scene -> at + scene.length - overlap }.dropLast(1)
    val duration: Float = starts.last() + scenes.last().length

    fun start(name: String): Float = starts[scenes.indexOfFirst { it.name == name }]

    @Composable
    fun Frame(t: Float) {
        Box(Modifier.fillMaxSize()) {
            Paper(t)
            scenes.forEachIndexed { i, scene ->
                val start = starts[i]
                val end = start + scene.length
                if (t in start..end) {
                    key(scene.name) {
                        val last = i == scenes.lastIndex
                        val alpha = sceneAlpha(t, start, end, if (i == 0) 0.001f else overlap, if (last) 0.001f else 0.35f)
                        val exit = if (last) 0f else window(t, end - 0.35f, 0.35f)
                        Box(Modifier.fillMaxSize().graphicsLayer { this.alpha = alpha; translationY = -easeInCubic(exit) * 16f * density }) {
                            scene.content(t - start)
                        }
                    }
                }
            }
        }
    }

    /** Every sound in film time. */
    fun cues(): List<Cue> = scenes.flatMapIndexed { i, scene ->
        val offset = starts[i]
        val local = Cues().apply(scene.cues).list.map { it.shift(offset) }
        if (i > 0) local + Cue.Whoosh(offset) else local
    }
}

/** A sound on the picture. Levels are linear amplitudes before mastering. */
sealed interface Cue {
    val at: Float
    data class Click(override val at: Float) : Cue
    data class Pop(override val at: Float, val level: Float = 0.13f) : Cue
    data class Keys(override val at: Float, val count: Int, val perChar: Float, val level: Float = 0.045f) : Cue
    data class Whoosh(override val at: Float) : Cue
    data class Scribble(override val at: Float, val seconds: Float = 0.45f, val level: Float = 0.12f) : Cue
    data class Chime(override val at: Float, val midi: Int, val level: Float = 0.09f) : Cue
}

fun Cue.shift(dt: Float): Cue = when (this) {
    is Cue.Click -> copy(at = at + dt)
    is Cue.Pop -> copy(at = at + dt)
    is Cue.Keys -> copy(at = at + dt)
    is Cue.Whoosh -> copy(at = at + dt)
    is Cue.Scribble -> copy(at = at + dt)
    is Cue.Chime -> copy(at = at + dt)
}

/** The sound vocabulary, written next to the picture it belongs to. */
class Cues {
    internal val list = mutableListOf<Cue>()

    /** A mouse click: every cursor press. */
    fun click(at: Float) { list += Cue.Click(at) }

    /** A soft pop: a card, chip, word or phone landing. 0.07 to 0.18. */
    fun pop(at: Float, level: Float = 0.13f) { list += Cue.Pop(at, level) }

    /** One key tick per character, at the same speed the text types on screen. */
    fun type(at: Float, text: String, perChar: Float, level: Float = 0.045f) { list += Cue.Keys(at, text.length, perChar, level) }

    /** Pen on paper: every doodle as it draws. */
    fun scribble(at: Float, seconds: Float = 0.45f, level: Float = 0.12f) { list += Cue.Scribble(at, seconds, level) }

    /** A bell: success, a tick, a reveal. */
    fun chime(at: Float, midi: Int, level: Float = 0.09f) { list += Cue.Chime(at, midi, level) }

    /** A rising arpeggio of bells [gap] apart, e.g. listOf(84, 88, 91) for a brand reveal. */
    fun arpeggio(at: Float, notes: List<Int>, gap: Float = 0.08f, level: Float = 0.1f) {
        notes.forEachIndexed { i, midi -> chime(at + i * gap, midi, level * (1f - i * 0.12f)) }
    }
}
