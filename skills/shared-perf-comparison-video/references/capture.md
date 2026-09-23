# Capturing the two recordings

A biased recording cannot be fixed in the edit. Spend the five minutes here.

## The rules that matter most

1. **Same device, or genuinely identical devices.** Different silicon invalidates the
   comparison entirely. If the two apps cannot coexist (same package/bundle id), either
   use two identical virtual devices or install them one after the other on one device.
2. **Release-shaped builds on both sides.** Debug builds are not what ships. On Android
   a debug build also bypasses the baseline profile, which specifically penalises startup
   — usually the thing being celebrated. If you must compare debug builds, say so.
3. **Same account, same data, same starting state.** Different delivery addresses, tiers
   or carts mean different payloads and different timings.
4. **Do not touch the screen after launching until the screen stops changing.** The single
   most common way a comparison becomes a measurement of human reaction time.
5. **Same journey, same targets.** If chapter 2 opens a detail screen, open the *same*
   one in both apps.
6. **Record with a host-side recorder** where one exists, so capture does not steal CPU
   from the app being measured.

## Android

Two virtual devices are often required, because two builds of the same app usually share
an application id and cannot be installed side by side. Clone one AVD definition twice so
the hardware profile is identical.

Record from the host, not the guest — the emulator's own recorder captures the host GL
surface and costs the guest nothing, unlike `adb shell screenrecord`:

```bash
adb -s emulator-5554 emu screenrecord start --time-limit 30 --fps 60 --bit-rate 16000000 ~/out/before.webm
# ... launch and drive the app ...
adb -s emulator-5554 emu screenrecord stop
```

Give both apps the same treatment before measuring:

```bash
adb shell am force-stop <pkg>          # a real cold start: no surviving process
adb shell cmd package compile -f -m speed-profile <pkg>   # after 2-3 warm-up launches
```

That compile step matters. A freshly installed app has not had its baseline profile
compiled in yet; measuring immediately flatters whichever side has no profile to miss.

For a cold start, `am start -W` also prints `TotalTime`, which is useful ground truth to
sanity-check your frame-derived numbers against — but do not use it as the video's t0
(see `measurement.md`).

## iOS

Check the bundle identifiers first. If they differ, both apps install on **one**
simulator, which is the fairest possible setup and less to manage.

```bash
xcrun simctl create "Perf Compare" "iPhone 17 Pro" com.apple.CoreSimulator.SimRuntime.iOS-26-2
xcrun simctl io booted recordVideo --codec h264 --mask ignored ~/out/before.mov
```

Build release-shaped for the simulator without needing signing:

```bash
xcodebuild -workspace App.xcworkspace -scheme "<Release scheme>" -configuration "<Release config>" \
  -sdk iphonesimulator -destination "id=<sim-udid>" -derivedDataPath dd \
  CODE_SIGNING_ALLOWED=NO ONLY_ACTIVE_ARCH=YES build
```

Two things that bite:

- **Crashlytics/dSYM upload build phases** often run ungated and will push simulator
  symbols to the real crash-reporting project. `DEBUG_INFORMATION_FORMAT=dwarf` produces
  no dSYM, so the upload becomes a no-op without touching the binary. Check whether the
  project's phase is gated to archives before assuming you need this.
- **Simulators are not phones.** They run on desktop silicon with desktop I/O. The ratio
  between two apps is usually indicative; the absolute seconds are not what a user gets.
  Say which you recorded on.

## Both apps on one device, sequentially

When the ids collide and you only have one device: install A, log in, record; uninstall;
install B, log in, record. Tedious but valid. Keep everything else constant, and be aware
that the second app benefits from a warmer OS page cache — if the second app is the one
you are promoting, mention it.

## Naming

Name the files so the edit cannot mix them up: `before-<version>.mov`, `after-<version>.mov`.
Two files called `client.webm` and `client2.webm` are how a before/after ends up reversed.
