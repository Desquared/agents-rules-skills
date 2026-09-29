# House style: sound

The reference films have **no voiceover and no licensed music**. The whole soundtrack is synthesised
in code from the film's own timings: a quiet music bed plus UI sounds that land on the exact frame of
the thing they belong to. The on-screen captions carry the story, so the film works muted (most
autoplay does) and there is nothing to license, record or re-record when copy changes.

If a team wants a voiceover, it is an addition, not the default: write the script from the captions,
keep scenes long enough for it, and **ask before using any paid text-to-speech or music service**.

## Where each sound goes

| Sound | On | Level (linear, pre-master) | Recipe |
| --- | --- | --- | --- |
| Whoosh | every scene change, starting 0.25 s before the new scene | 0.18 to 0.22 | 0.6 s white noise through a one-pole low-pass whose cutoff rises (0.02 to 0.27), envelope `sin(pi x)^2` |
| Click | every cursor press, same frame | 0.26 to 0.28 | 60 ms: 1900 Hz sine x 0.6 plus a noise burst, `e^(-90t)` (noise `e^(-400t)`) |
| Pop | a word, chip, card or phone landing | 0.07 to 0.18 (words 0.09, chips 0.12, cards 0.14, heroes 0.18) | 120 ms chirp `sin(2 pi (520 t + 2600 t^2))`, `e^(-28t)` |
| Key tick | every typed character, at the typing speed | 0.035 to 0.08 | 20 ms of noise, `e^(-300t)`, level jittered +0.02 |
| Scribble | every pen doodle while it draws | 0.1 to 0.25 (the hook's strike is loudest) | low-passed noise (0.3), 26 to 28 Hz tremolo (60% to 100%), `sin(pi t / len)` envelope, 0.35 to 0.5 s |
| Chime | success: a tick, "done", a reveal | 0.06 to 0.11 | bell: `f` + 0.4 x `2.76 f` (decay 6) + 0.15 x `5.4 f` (decay 10), `e^(-3t)`, 1.6 s |

Chime notes (MIDI): a brand reveal is a rising triad `84, 88, 91` (C6, E6, G6) 0.07 to 0.08 s apart,
levels falling 0.11, 0.08, 0.07. A checklist is one note per tick rising (`88, 91, 96`). The end card
is `72, 79, 84, 88` 0.08 s apart. A single "done" is `88` then `91` 0.1 s later.

## The music beds

Both beds loop four chords of eight beats: **Cmaj9, Am9, Fmaj9, G6/9** (MIDI
`48 52 55 59 62 / 45 48 52 55 59 / 41 45 48 52 55 / 43 47 50 52 57`). Bright, unresolved, never
dramatic.

**Promo bed, 112 BPM**

- Pad: chord tones except the root, one octave up, partials 1 + 0.3 x 2f (detuned x1.002) + 0.1 x 3f
  (x0.998), 0.5 s attack, 4/s release, voices panned +-0.25. Level 0.05 in the intro, 0.065 from the
  brand reveal. The first chord fades in over 2.2 s.
- Marimba arpeggio: chord tones two octaves up in the pattern `0 2 1 3 2 4 1 3 0 2 4 3 2 1 3 4`,
  `e^(-7t)` plus a 3.93f partial decaying at 30/s, panned +-0.35. Quarter notes at 0.07 in the intro,
  eighths at 0.085 in the groove.
- Groove, from the brand reveal (about 4.3 s) to the end card: kick every two beats (a 50 Hz sine with
  a fast upward pitch drop, 0.32, `e^(-9t)`), bass root one octave down every four beats (0.16,
  `e^(-2.2t)`), a quiet off-beat noise hat (0.035, `e^(-45t)`) from about 4 s into the groove.
- The last chord (at the end card) is held to the end with no arpeggio or drums.

**Tutorial bed, 96 BPM, no drums** (it sits under reading)

- Pad at 0.045 (partials 1 + 0.25 x 2.003f), 0.8 s attack, voices panned +-0.3, first chord fading
  in over 2.5 s.
- A pluck per beat in the pattern `0 2 4 2 1 3 4 3`, two octaves up, 0.05, `e^(-6t)`, panned +-0.4.
- A sub bass on each chord root (0.09, `e^(-1.6t)`).

## Mastering

1. Linear fade to silence over the last 1.4 s (promo) or 1.6 s (tutorial).
2. Soft clip: `tanh(x * 1.2) / tanh(1.2)`.
3. Normalise the peak to 0.89 (-1 dBFS).
4. Write 16-bit, 44.1 kHz, stereo PCM WAV.
5. Mux as AAC-LC 192 kbps.

Measured on the delivered films: integrated loudness **-17.5 LUFS** (promo) and **-14.7 LUFS**
(tutorials), loudness range 1 to 2 LU. For a new film aim for **-16 LUFS integrated, -1 dBTP**,
which suits web players and social platforms. Check with:

```bash
ffmpeg -hide_banner -nostats -i final.mp4 -af ebur128=peak=true -f null - 2>&1 | grep -A12 Summary
```

## One source of truth for timing

Sound drifts out of sync the moment its times are typed twice. In the reference project the audio
event lists were computed from the same constants the picture used (typing start plus
`text.length x 0.045`, and so on). The starter kit goes one step further: each `Scene` declares its
cues next to its picture in scene-local time, and `Film.cues()` shifts them into film time and adds
the whooshes. If you move a scene, its sounds move with it.
