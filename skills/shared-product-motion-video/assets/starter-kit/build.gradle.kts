// Plain Compose Desktop. No product classpath is needed: product UI arrives either as PNGs in
// assets/ or as composables you add as a dependency and render with Capture.kt.
plugins {
    kotlin("jvm") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0"
    id("org.jetbrains.compose") version "1.11.1"
    application
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
}

kotlin {
    compilerOptions {
        // ImageComposeScene, the off-screen renderer every frame goes through.
        optIn.add("androidx.compose.ui.InternalComposeUiApi")
    }
}

application {
    mainClass.set("motion.MainKt")
    // Headless: nothing opens a window. 4 GB is plenty for 1080p; raise it for 4K footage.
    applicationDefaultJvmArgs = listOf("-Djava.awt.headless=true", "-Xmx4g")
}

tasks.named<JavaExec>("run") {
    workingDir = projectDir
}
