package motion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import java.io.File

// The frame pump: Compose renders off screen into a Skia image for each frame time, the raw BGRA
// pixels are piped into ffmpeg's stdin, and ffmpeg writes H.264. No window, no screen recording.

const val WIDTH = 1920
const val HEIGHT = 1080
const val FPS = 30

/** Layout happens on a 960 x 540 dp page at density 2, so 1 dp = 2 px of the 1080p frame. */
const val DENSITY = 2f
const val PAGE_WIDTH = WIDTH / DENSITY
const val PAGE_HEIGHT = HEIGHT / DENSITY

fun ffmpeg(): String = System.getenv("FFMPEG") ?: "ffmpeg"

private class Pump(content: @Composable (Float) -> Unit) : AutoCloseable {
    private val clock = mutableFloatStateOf(0f)
    private val scene = ImageComposeScene(WIDTH, HEIGHT, Density(DENSITY)) { content(clock.floatValue) }

    fun frame(seconds: Float): Image {
        clock.floatValue = seconds
        Snapshot.sendApplyNotifications()
        scene.render((seconds * 1e9).toLong())
        // A second pass so layout-driven marks (word bounds for doodles, button centres for the cursor) are current.
        return scene.render((seconds * 1e9).toLong() + 1)
    }

    override fun close() = scene.close()
}

/** Renders [content] from [from] to [to] seconds into a silent mp4 at [out]. */
fun renderFilm(out: File, from: Float, to: Float, content: @Composable (Float) -> Unit) {
    out.parentFile?.mkdirs()
    Pump(content).use { pump ->
        val encoder = ProcessBuilder(
            ffmpeg(), "-y", "-loglevel", "error",
            "-f", "rawvideo", "-pix_fmt", "bgra", "-s", "${WIDTH}x$HEIGHT", "-r", "$FPS", "-i", "-",
            "-c:v", "libx264", "-preset", "slow", "-crf", "14", "-pix_fmt", "yuv420p", "-movflags", "+faststart",
            out.absolutePath,
        ).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT).start()
        val bitmap = Bitmap().apply { allocPixels(ImageInfo.makeN32(WIDTH, HEIGHT, ColorAlphaType.PREMUL)) }
        val frames = ((to - from) * FPS).toInt()
        encoder.outputStream.buffered(1 shl 22).use { pipe ->
            for (i in 0 until frames) {
                val image = pump.frame(from + i / FPS.toFloat())
                image.readPixels(bitmap, 0, 0)
                pipe.write(requireNotNull(bitmap.readPixels()))
                image.close()
                if (i % 60 == 0) println("frame $i / $frames")
            }
        }
        check(encoder.waitFor() == 0) { "ffmpeg failed" }
        println("video: ${out.absolutePath}")
    }
}

/** PNG stills at [times], named <prefix>-still-SS.ss.png. The QA loop: check stills before rendering. */
fun renderStills(dir: File, prefix: String, times: List<Float>, content: @Composable (Float) -> Unit) {
    dir.mkdirs()
    Pump(content).use { pump ->
        times.forEach { at ->
            // Walk up to the still so time-dependent layout (word marks) has settled.
            pump.frame(at - 0.05f).close()
            val file = File(dir, "$prefix-still-${"%05.2f".format(at)}.png")
            file.writeBytes(requireNotNull(pump.frame(at).encodeToData(EncodedImageFormat.PNG)).bytes)
            println("still: ${file.absolutePath}")
        }
    }
}

/** Puts the soundtrack on the picture without re-encoding the video: AAC 192 kbps. */
fun mux(video: File, audio: File, out: File) {
    val process = ProcessBuilder(
        ffmpeg(), "-y", "-loglevel", "error", "-i", video.absolutePath, "-i", audio.absolutePath,
        "-map", "0:v:0", "-map", "1:a:0", "-c:v", "copy", "-c:a", "aac", "-b:a", "192k", "-shortest", "-movflags", "+faststart",
        out.absolutePath,
    ).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT).start()
    check(process.waitFor() == 0) { "ffmpeg mux failed" }
    println("final: ${out.absolutePath}")
}
