package motion

import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * Renders [content] (your REAL product UI, fed with fixture data) to a PNG, off screen, at [scale]x:
 * 3x for phone screens, 2x for desktop or web. It runs [frames] frames of 16 ms first so animations,
 * layout and first-frame effects settle.
 *
 * If your UI loads images asynchronously (Coil, Kingfisher, a web fetch), render once, wait until the
 * loader's memory cache is warm, then render again: our captures slept 3.5 s between two renders.
 * Never ship a capture with a spinner, a skeleton or a grey placeholder where content belongs.
 */
fun capture(out: File, widthDp: Int, heightDp: Int, scale: Float = 3f, frames: Int = 30, content: @Composable () -> Unit) {
    out.parentFile?.mkdirs()
    ImageComposeScene((widthDp * scale).toInt(), (heightDp * scale).toInt(), Density(scale)) { content() }.use { scene ->
        var time = 0L
        repeat(frames) {
            scene.render(time)
            time += 16_000_000L
        }
        out.writeBytes(requireNotNull(scene.render(time).encodeToData(EncodedImageFormat.PNG)).bytes)
        println("capture: ${out.absolutePath}")
    }
}
