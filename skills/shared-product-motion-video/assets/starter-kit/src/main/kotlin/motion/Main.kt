package motion

import java.io.File
import kotlin.system.exitProcess

/**
 *   capture                render the stand-in product into assets/ (replace with your own capture)
 *   stills 1.0 6.2 ...     PNG stills at those film times into out/ (check these before any render)
 *   render [from to]       the silent film, or a preview of one range, into out/
 *   audio                  the synthesised soundtrack into out/
 *   all                    capture, render, audio, mux: out/example.mp4
 */
fun main(args: Array<String>) {
    val root = File(".").absoluteFile
    val assets = File(root, "assets")
    val out = File(root, "out")
    val film = exampleFilm(SampleAssets(assets))
    fun audio() = File(out, "example.wav").also { soundtrack(it, film.duration, film.cues(), Bed.PROMO, grooveFrom = film.start("lockup") + 0.2f, grooveTo = film.start("end")) }
    fun silent() = File(out, "example-silent.mp4").also { renderFilm(it, 0f, film.duration) { t -> film.Frame(t) } }
    when (args.firstOrNull()) {
        "capture" -> captureSamples(assets)
        "stills" -> renderStills(out, "example", args.drop(1).map { it.toFloat() }) { t -> film.Frame(t) }
        "render" -> {
            val from = args.getOrNull(1)?.toFloat()
            val to = args.getOrNull(2)?.toFloat()
            if (from == null || to == null) silent() else renderFilm(File(out, "preview-$from-$to.mp4"), from, to) { t -> film.Frame(t) }
        }
        "audio" -> audio()
        "all" -> {
            captureSamples(assets)
            mux(silent(), audio(), File(out, "example.mp4"))
        }
        else -> {
            System.err.println("usage: capture | stills <seconds...> | render [from to] | audio | all")
            exitProcess(2)
        }
    }
    println("film: %.2f s, scenes start at %s".format(film.duration, film.starts.joinToString { "%.2f".format(it) }))
    // Compose keeps non-daemon threads alive; exit explicitly.
    exitProcess(0)
}
