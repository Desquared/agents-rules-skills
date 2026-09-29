# Capturing the real product

The rule that matters most in this style: **the product on screen is the real product.** Not a Figma
mock, not a redraw, not a screen recording with a notification sliding in. Viewers who use the
product will notice a fake, and a fake has to be redrawn every time the product changes.

## How the reference films did it

The films were rendered by a Kotlin program that had the product's real code on its classpath, so
every image was rendered from code, headlessly, at the moment of capture:

1. **Screens** were the product's own preview stories (real composables, fixture data) rendered off
   screen at 3x, inside the device shell, through the product's screenshot API. Each was rendered
   twice with a 3.5 s pause between, because the first render starts the image loader and only the
   second shows the images from its memory cache.
2. **The desktop tool** itself was composed off screen at 3360 x 2100 (2x of 1680 x 1050), light
   theme, with its state set programmatically (which mode, which board, which item selected, which
   panel open). It was pumped for 70 frames of 16 ms with short sleeps so async work settled, then
   saved. Its bottom 70 px (a status strip) were cropped in the film.
3. **Component states** (every property combination shown in a "try the properties" beat) were
   rendered one PNG per combination through the same invoker the product uses.
4. **Facts** shown on screen (counts of states, components, properties, a string's resource key and
   both translations) were computed from the product at capture time into a `facts.txt`, and the film
   read them. A number on screen is never typed by hand.
5. **Before and after** for tutorials: the product state was staged into a working copy twice (an
   `install.sh draft | designed | clean` script copied two sets of source files and board files in),
   and the capture ran once per phase. The film then morphs from the draft capture to the designed one.

Capture is a separate step from rendering the film (`capture` then `render`), so the slow, stateful
part runs once and the film renders fast and deterministically from PNGs.

## Choosing a technique for your product

Prefer the first that your stack allows.

| Technique | Works for | How |
| --- | --- | --- |
| Render from code, off screen | Compose (Android, Desktop, Multiplatform) | Put the UI module on the video project's classpath and call `capture()` in the starter kit, or use Paparazzi / Roborazzi to write PNGs from previews |
| Snapshot tests / previews | SwiftUI, UIKit | `swift-snapshot-testing` or `ImageRenderer` from a test target, at `@3x` |
| Headless browser | Web apps, Storybook | Playwright: `browser.newContext({ deviceScaleFactor: 2 })`, then `page.screenshot({ scale: 'device' })` on a fixture-backed route or a Storybook story |
| Simulator / emulator screenshot | Any mobile app | Clean status bar first (below), then `xcrun simctl io booted screenshot` or `adb exec-out screencap -p` |
| Screen recording | Only for motion that must be real (a live animation, video playback) | Record at the device's native resolution; cut it into the film as footage |

Clean status bars (9:41, full battery, full signal, no notifications):

```bash
# iOS Simulator
xcrun simctl status_override booted --time "9:41" --batteryState charged --batteryLevel 100 --cellularBars 4 --wifiBars 3
# Android emulator (demo mode)
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 0941
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
```

## Resolution

- Phones at **3x** (a 393 dp wide screen is 1179 px). Desktop and web at **2x**.
- For footage the camera will push into: source width at least `displayed width in px x max zoom`.
  A 560 dp footage window is 1120 px on the 1080p frame; a 2.6x push needs at least 2912 px.
- Keep every capture's own aspect ratio. Fit, never stretch.

## Content in captures

- **Fixture data that reads as real**: real-looking names, prices, dates in the product's language.
  The reference films kept the product's own shipped language (not English) inside the UI, while the
  film's captions were in English. Decide this per audience and keep it consistent.
- **No placeholder text, no lorem ipsum, no "Test", no spinners, no skeletons, no grey boxes** where
  an image belongs. If an image does not load in time, render again.
- No personal data. Use seeded demo accounts only.
- Store captures as PNG in the video project's `assets/` (or `tutorial-assets/`), named for what they
  show (`checkout.png`, `checkout-dark.png`, `draft-login.png`, `designed-login.png`). They are build
  outputs: regenerate them, do not hand-edit them.
