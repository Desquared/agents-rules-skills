package motion

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tanh
import kotlin.random.Random

// The whole soundtrack is synthesised from the film's own cue list: no stock music, no samples, no
// voice, so there is nothing to license and every sound lands on its frame. Output: 16-bit 44.1 kHz
// stereo WAV, soft-clipped and normalised to -1 dBFS peak.

private const val RATE = 44_100
private const val TAU = 2f * PI.toFloat()

/** The music bed. PROMO has a groove (kick, bass, hats); TUTORIAL is calm and sits under reading. */
enum class Bed(val bpm: Float) { PROMO(112f), TUTORIAL(96f) }

fun hz(midi: Int) = 440f * 2f.pow((midi - 69) / 12f)

/** Cmaj9, Am9, Fmaj9, G6/9: bright, unresolved, loops forever. Eight beats each. */
private val CHORDS = listOf(
    listOf(48, 52, 55, 59, 62),
    listOf(45, 48, 52, 55, 59),
    listOf(41, 45, 48, 52, 55),
    listOf(43, 47, 50, 52, 57),
)

private class Mix(val duration: Float, seed: Int) {
    val n = (duration * RATE).toInt()
    val left = FloatArray(n)
    val right = FloatArray(n)
    val random = Random(seed)

    fun noise() = random.nextFloat() * 2f - 1f

    /** Adds [voice] (a function of the voice's own time) at [at] for [seconds], panned -1..1. */
    fun add(at: Float, seconds: Float, pan: Float = 0f, voice: (Float) -> Float) {
        val start = (at * RATE).toInt()
        val l = 1f - pan.coerceAtLeast(0f)
        val r = 1f + pan.coerceAtMost(0f)
        for (i in 0 until (seconds * RATE).toInt()) {
            val index = start + i
            if (index !in 0 until n) continue
            val v = voice(i / RATE.toFloat())
            left[index] += v * l
            right[index] += v * r
        }
    }
}

/**
 * Writes the soundtrack for a film of [duration] seconds. [grooveFrom]..[grooveTo] is where the PROMO
 * drums play (from the brand reveal to the end card); the TUTORIAL bed ignores it.
 */
fun soundtrack(out: File, duration: Float, cues: List<Cue>, bed: Bed, grooveFrom: Float = 4.3f, grooveTo: Float = duration - 2.5f) {
    val mix = Mix(duration, seed = 7)
    when (bed) {
        Bed.PROMO -> promoBed(mix, grooveFrom, grooveTo)
        Bed.TUTORIAL -> tutorialBed(mix)
    }
    cues.forEach { sfx(mix, it) }
    master(mix, out, fadeOut = if (bed == Bed.PROMO) 1.4f else 1.6f)
}

private fun promoBed(mix: Mix, grooveFrom: Float, grooveTo: Float) {
    val beat = 60f / Bed.PROMO.bpm
    val chordLength = beat * 8
    var chordStart = 0f
    var index = 0
    while (chordStart < mix.duration) {
        val chord = CHORDS[index % CHORDS.size]
        val final = chordStart >= grooveTo - 0.1f
        val length = if (final) mix.duration - chordStart else chordLength
        val padLevel = if (chordStart < grooveFrom - 0.1f) 0.05f else 0.065f
        // Pad: three organ partials, alternate voices panned, 0.5 s attack, the first chord fading in over 2.2 s.
        chord.drop(1).forEachIndexed { v, note ->
            val f = hz(note + 12)
            val first = chordStart == 0f
            mix.add(chordStart, length + 0.8f, pan = if (v % 2 == 0) -0.25f else 0.25f) { t ->
                val env = (t / 0.5f).coerceAtMost(1f) * (if (t > length) exp(-(t - length) * 4f) else 1f)
                val fade = if (first) (t / 2.2f).coerceAtMost(1f) else 1f
                padLevel * env * fade * (sin(TAU * f * t) + 0.3f * sin(TAU * 2f * f * 1.002f * t) + 0.1f * sin(TAU * 3f * f * 0.998f * t)) * 0.5f
            }
        }
        if (!final) {
            // Marimba arpeggio two octaves up: quarters before the groove, eighths in it.
            val pattern = listOf(0, 2, 1, 3, 2, 4, 1, 3, 0, 2, 4, 3, 2, 1, 3, 4)
            val inGroove = chordStart >= grooveFrom - 0.1f
            val step = if (inGroove) beat / 2f else beat
            val level = if (inGroove) 0.085f else 0.07f
            var s = 0
            var at = chordStart
            while (at < chordStart + chordLength - 0.01f) {
                val f = hz(chord[pattern[s % pattern.size]] + 24)
                mix.add(at, 0.7f, pan = if (s % 2 == 0) -0.35f else 0.35f) { t ->
                    level * exp(-t * 7f) * (sin(TAU * f * t) + 0.18f * sin(TAU * 3.93f * f * t) * exp(-t * 30f))
                }
                s++
                at += step
            }
            // Kick every two beats, bass root every four, a quiet off-beat hat a few seconds into the groove.
            for (b in 0 until 8) {
                val at2 = chordStart + b * beat
                if (at2 < grooveFrom - 0.05f || at2 > grooveTo) continue
                if (b % 2 == 0) mix.add(at2, 0.4f) { t -> 0.32f * exp(-t * 9f) * sin(TAU * (50f * t + 70f / 30f * (1f - exp(-t * 30f)))) }
                if (b % 4 == 0) {
                    val f = hz(chord[0] - 12)
                    mix.add(at2, beat * 2f) { t -> 0.16f * exp(-t * 2.2f) * (sin(TAU * f * t) + 0.3f * sin(TAU * 2f * f * t)) }
                }
                if (at2 > grooveFrom + 3.8f) mix.add(at2 + beat / 2f, 0.08f, pan = 0.3f) { t -> 0.035f * exp(-t * 45f) * mix.noise() }
            }
        }
        chordStart += chordLength
        index++
    }
}

private fun tutorialBed(mix: Mix) {
    val beat = 60f / Bed.TUTORIAL.bpm
    val length = beat * 8
    var at = 0f
    var c = 0
    while (at < mix.duration) {
        val chord = CHORDS[c % CHORDS.size]
        val first = at == 0f
        chord.drop(1).forEachIndexed { v, note ->
            val f = hz(note + 12)
            mix.add(at, length + 1f, if (v % 2 == 0) -0.3f else 0.3f) { t ->
                val env = (t / 0.8f).coerceAtMost(1f) * (if (t > length) exp(-(t - length) * 3f) else 1f)
                val fadeIn = if (first) (t / 2.5f).coerceAtMost(1f) else 1f
                0.045f * env * fadeIn * (sin(TAU * f * t) + 0.25f * sin(TAU * 2.003f * f * t))
            }
        }
        listOf(0, 2, 4, 2, 1, 3, 4, 3).forEachIndexed { i, idx ->
            val f = hz(chord[idx] + 24)
            mix.add(at + i * beat, 0.9f, if (i % 2 == 0) -0.4f else 0.4f) { t -> 0.05f * exp(-t * 6f) * (sin(TAU * f * t) + 0.15f * sin(TAU * 3.93f * f * t) * exp(-t * 25f)) }
        }
        val root = hz(chord[0] - 12)
        mix.add(at, length) { t -> 0.09f * exp(-t * 1.6f) * sin(TAU * root * t) }
        at += length
        c++
    }
}

private fun sfx(mix: Mix, cue: Cue) {
    when (cue) {
        is Cue.Click -> mix.add(cue.at, 0.06f) { t -> 0.27f * exp(-t * 90f) * (sin(TAU * 1900f * t) * 0.6f + mix.noise() * exp(-t * 400f)) }
        is Cue.Pop -> mix.add(cue.at, 0.12f) { t -> cue.level * exp(-t * 28f) * sin(TAU * (520f * t + 2600f * t * t)) }
        is Cue.Keys -> repeat(cue.count) { i ->
            mix.add(cue.at + i * cue.perChar, 0.02f) { t -> (cue.level + mix.random.nextFloat() * 0.02f) * exp(-t * 300f) * mix.noise() }
        }
        is Cue.Whoosh -> {
            // Low-passed noise swelling over 0.6 s, centred just after the cut.
            var low = 0f
            mix.add(cue.at - 0.25f, 0.6f) { t ->
                val x = t / 0.6f
                low += (0.02f + 0.25f * x) * (mix.noise() - low)
                0.2f * sin(PI.toFloat() * x).pow(2) * low
            }
        }
        is Cue.Scribble -> {
            var low = 0f
            mix.add(cue.at, cue.seconds) { t ->
                low += 0.3f * (mix.noise() - low)
                cue.level * (0.6f + 0.4f * sin(TAU * 27f * t)) * low * sin(PI.toFloat() * t / cue.seconds)
            }
        }
        is Cue.Chime -> {
            val f = hz(cue.midi)
            mix.add(cue.at, 1.6f) { t ->
                cue.level * exp(-t * 3f) * (sin(TAU * f * t) + 0.4f * sin(TAU * 2.76f * f * t) * exp(-t * 6f) + 0.15f * sin(TAU * 5.4f * f * t) * exp(-t * 10f))
            }
        }
    }
}

/** Linear fade over the last [fadeOut] s, tanh soft clip (drive 1.2), normalise the peak to 0.89 (-1 dBFS). */
private fun master(mix: Mix, out: File, fadeOut: Float) {
    val n = mix.n
    val fadeFrom = ((mix.duration - fadeOut) * RATE).toInt().coerceAtLeast(0)
    for (i in fadeFrom until n) {
        val g = 1f - (i - fadeFrom) / (n - fadeFrom).toFloat()
        mix.left[i] *= g
        mix.right[i] *= g
    }
    for (i in 0 until n) {
        mix.left[i] = tanh(mix.left[i] * 1.2f) / tanh(1.2f)
        mix.right[i] = tanh(mix.right[i] * 1.2f) / tanh(1.2f)
    }
    val peak = (0 until n).maxOf { maxOf(abs(mix.left[it]), abs(mix.right[it])) }.coerceAtLeast(1e-6f)
    val gain = 0.89f / peak
    out.parentFile?.mkdirs()
    DataOutputStream(FileOutputStream(out).buffered()).use { w ->
        fun i32(v: Int) { w.write(v and 0xff); w.write(v shr 8 and 0xff); w.write(v shr 16 and 0xff); w.write(v shr 24 and 0xff) }
        fun i16(v: Int) { w.write(v and 0xff); w.write(v shr 8 and 0xff) }
        w.writeBytes("RIFF"); i32(36 + n * 4); w.writeBytes("WAVE")
        w.writeBytes("fmt "); i32(16); i16(1); i16(2); i32(RATE); i32(RATE * 4); i16(4); i16(16)
        w.writeBytes("data"); i32(n * 4)
        for (i in 0 until n) {
            i16((mix.left[i] * gain * 32767f).toInt())
            i16((mix.right[i] * gain * 32767f).toInt())
        }
    }
    println("audio: ${out.absolutePath}")
}
