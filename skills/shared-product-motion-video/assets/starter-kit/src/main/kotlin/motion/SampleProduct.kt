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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Image
import java.io.File

// A STAND-IN for your product, so the example film renders out of the box. Delete this file and
// capture your real UI instead (Capture.kt for composables; see references/capture.md otherwise).
// The layouts use fixed positions so the example can aim the cursor with footage fractions.

private val tasks = listOf("Review the launch plan", "Draft release notes", "Approve the new icon", "Book the demo room", "Reply to design review")

/** A phone screen, 393 x 852 dp. Rows are 64 dp tall from y = 160; [done] ticks that row. */
@Composable
fun SampleScreen(done: Int = -1) {
    Column(Modifier.fillMaxSize().background(Color.White)) {
        Row(Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 32.dp), verticalAlignment = Alignment.CenterVertically) {
            BasicText("9:41", style = body(16.sp, Ink.ink).copy(fontWeight = FontWeight.Medium))
        }
        Column(Modifier.height(106.dp).padding(horizontal = 20.dp), verticalArrangement = Arrangement.Center) {
            BasicText("Today", style = display(34.sp, FontWeight.Bold))
            BasicText("${tasks.size - if (done >= 0) 1 else 0} open tasks", style = body(14.sp))
        }
        tasks.forEachIndexed { i, task ->
            val on = i == done
            Row(Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).background(if (on) Ink.greenInk else Color.White, CircleShape).border(1.5.dp, if (on) Ink.greenInk else Ink.faint, CircleShape)) {
                    if (on) Canvas(Modifier.size(24.dp)) { inDp { tick(Offset(12f, 12f), 12f, 1f, Color.White) } }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    BasicText(task, style = body(17.sp, if (on) Ink.faint else Ink.ink))
                    BasicText(if (on) "Done just now" else "Due today", style = body(13.sp, if (on) Ink.greenInk else Ink.muted))
                }
            }
            Box(Modifier.padding(start = 58.dp).fillMaxWidth().height(1.dp).background(Ink.line))
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.padding(20.dp).fillMaxWidth().height(52.dp).background(Ink.ink, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            BasicText("Add task", style = body(17.sp, Color.White).copy(fontWeight = FontWeight.Medium))
        }
        Spacer(Modifier.height(20.dp))
    }
}

/** A desktop app, 1440 x 900 dp. "+ New" sits at x 1320..1416, y 12..44; [dialog] opens its form. */
@Composable
fun SampleDesktop(dialog: Boolean = false) {
    Box(Modifier.fillMaxSize().background(Color(0xFFF7F7F5))) {
        Row(Modifier.fillMaxWidth().height(56.dp).background(Color.White).padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            BasicText("Workspace", style = display(18.sp, FontWeight.Bold))
        }
        Box(Modifier.at(1320f, 12f).size(96.dp, 32.dp).background(Ink.blueInk, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            BasicText("+ New", style = body(14.sp, Color.White).copy(fontWeight = FontWeight.Medium))
        }
        Box(Modifier.at(0f, 56f).fillMaxWidth().height(1.dp).background(Ink.line))
        Column(Modifier.at(0f, 57f).width(220.dp).fillMaxSize().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf("Home", "Projects", "Reviews", "Releases", "Settings").forEachIndexed { i, item ->
                BasicText(item, style = body(14.sp, if (i == 1) Ink.ink else Ink.muted).copy(fontWeight = if (i == 1) FontWeight.Medium else FontWeight.Normal))
            }
        }
        BasicText("Projects", Modifier.at(260f, 88f), style = display(26.sp, FontWeight.Bold))
        listOf("Spring launch", "Onboarding", "Pricing page", "Mobile app", "Help centre", "Brand refresh").forEachIndexed { i, name ->
            Column(Modifier.at(260f + (i % 3) * 272f, 140f + (i / 3) * 190f).size(248.dp, 166.dp).card(10f, 2f).padding(16.dp)) {
                Box(Modifier.fillMaxWidth().height(84.dp).background(listOf(Ink.blue, Ink.green, Ink.yellow, Ink.purple, Ink.pink, Ink.orange)[i], RoundedCornerShape(6.dp)))
                Spacer(Modifier.height(12.dp))
                BasicText(name, style = body(15.sp, Ink.ink).copy(fontWeight = FontWeight.Medium))
                BasicText("Updated today", style = body(12.sp))
            }
        }
        if (dialog) {
            Box(Modifier.fillMaxSize().background(Color(0x33000000)))
            Column(Modifier.at(480f, 250f).width(480.dp).card(14f, 20f).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BasicText("New project", style = display(22.sp, FontWeight.Bold))
                TypingField("Name", "Autumn launch", 0f, 99f)
                TypingField("What is it for?", "Everything we ship in October.", 0f, 99f)
                Box(Modifier.background(Ink.blueInk, RoundedCornerShape(8.dp)).padding(horizontal = 18.dp, vertical = 9.dp)) {
                    BasicText("Create", style = body(14.sp, Color.White).copy(fontWeight = FontWeight.Medium))
                }
            }
        }
    }
}

/** Renders the stand-in product into [dir]: phone at 3x, desktop at 2x. */
fun captureSamples(dir: File) {
    capture(File(dir, "screen.png"), 393, 852, 3f) { SampleScreen() }
    capture(File(dir, "screen-done.png"), 393, 852, 3f) { SampleScreen(done = 2) }
    capture(File(dir, "desktop.png"), 1440, 900, 2f) { SampleDesktop() }
    capture(File(dir, "desktop-new.png"), 1440, 900, 2f) { SampleDesktop(dialog = true) }
}

class SampleAssets(private val dir: File) {
    private fun load(name: String): ImageBitmap {
        val file = File(dir, "$name.png")
        require(file.isFile) { "missing ${file.path}: run `gradle run --args=capture` first" }
        return Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
    }
    val screen by lazy { load("screen") }
    val screenDone by lazy { load("screen-done") }
    val desktop by lazy { load("desktop") }
    val desktopNew by lazy { load("desktop-new") }
}
