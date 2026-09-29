package motion

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import java.io.File

/** The house palette: warm paper, black ink, pastel highlighters, two saturated inks for UI marks. */
object Ink {
    val paper = Color(0xFFFBFAF7)
    val card = Color(0xFFFFFFFF)
    val ink = Color(0xFF191919)
    val muted = Color(0xFF6F6E69)
    val faint = Color(0xFFA5A39E)
    val line = Color(0xFFE6E4DF)
    val dot = Color(0xFFE3E1DB)
    val codeBg = Color(0xFFF6F5F2)
    val windowBar = Color(0xFFF3F2EF)

    // Highlighters: tag backgrounds, chips, the marker sweep. Text on them is always ink.
    val yellow = Color(0xFFFBE7A1)
    val blue = Color(0xFFD6E8F5)
    val green = Color(0xFFDDEEDC)
    val pink = Color(0xFFF7DDE6)
    val purple = Color(0xFFE9E1F5)
    val orange = Color(0xFFFBE3CF)

    // Inks: selection boxes, focus rings, click ripples (blue); success ticks and copy (green).
    val blueInk = Color(0xFF2F6FDE)
    val greenInk = Color(0xFF2E8B57)

    /** Placeholder: set to your product's primary colour. Use it for the app icon and at most one accent. */
    val brand = Color(0xFF4F46E5)
}

/**
 * Four families. Files are read from ./fonts (or -Dmotion.fonts=<dir>); scripts/fetch-fonts.sh puts the
 * open-licence set there. Missing files fall back to the system sans/serif/mono, with a warning.
 */
object Type {
    private val dir = File(System.getProperty("motion.fonts", "fonts"))

    private fun face(file: String, weight: FontWeight, style: FontStyle = FontStyle.Normal): Font? =
        File(dir, file).takeIf { it.isFile }?.let { Font(it, weight, style) }

    private fun family(name: String, fallback: FontFamily, vararg faces: Font?): FontFamily {
        val found = faces.filterNotNull()
        if (found.isEmpty()) System.err.println("fonts: no $name faces in ${dir.absolutePath}, using the system fallback")
        return if (found.isEmpty()) fallback else FontFamily(found)
    }

    /** Headlines. Tight, heavy, slightly negative tracking. */
    val display = family(
        "display", FontFamily.SansSerif,
        face("InterDisplay-Regular.ttf", FontWeight.Normal),
        face("InterDisplay-Medium.ttf", FontWeight.Medium),
        face("InterDisplay-SemiBold.ttf", FontWeight.SemiBold),
        face("InterDisplay-Bold.ttf", FontWeight.Bold),
        face("InterDisplay-Black.ttf", FontWeight.Black),
    )

    /** Body copy, labels, UI inside cards. */
    val text = family(
        "text", FontFamily.SansSerif,
        face("Inter-Regular.ttf", FontWeight.Normal),
        face("Inter-Medium.ttf", FontWeight.Medium),
    )

    /** The handwritten-note voice: italic serif, only for one emphasised word or a margin note. */
    val serif = family("serif", FontFamily.Serif, face("Newsreader-Italic.ttf", FontWeight.Normal, FontStyle.Italic))

    /** Tags, file paths, code, prompts. */
    val mono = family(
        "mono", FontFamily.Monospace,
        face("JetBrainsMono-Regular.ttf", FontWeight.Normal),
        face("JetBrainsMono-Medium.ttf", FontWeight.Medium),
    )
}

fun display(size: TextUnit, weight: FontWeight = FontWeight.Bold, color: Color = Ink.ink) =
    TextStyle(fontFamily = Type.display, fontWeight = weight, fontSize = size, color = color, letterSpacing = (-0.02).em, lineHeight = size * 1.06f)

fun body(size: TextUnit, color: Color = Ink.muted, family: FontFamily = Type.text, style: FontStyle = FontStyle.Normal) =
    TextStyle(fontFamily = family, fontSize = size, color = color, lineHeight = size * 1.4f, fontStyle = style)

/** A margin note next to a doodle: italic serif, ink. */
fun note(size: TextUnit = 16.sp) = body(size, Ink.ink, Type.serif, FontStyle.Italic)

fun code(size: TextUnit = 11.sp, color: Color = Ink.muted) = body(size, color, Type.mono)
