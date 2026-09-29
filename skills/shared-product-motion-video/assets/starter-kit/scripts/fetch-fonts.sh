#!/usr/bin/env bash
# Puts the open-licence (SIL OFL) house fonts into ./fonts, where Style.kt looks for them:
#   Inter + Inter Display (rsms/inter), Newsreader Italic (Google Fonts), JetBrains Mono.
# Run from the starter-kit folder. Needs curl and unzip. Downloads about 30 MB, once.
set -euo pipefail

INTER_VERSION="${INTER_VERSION:-4.1}"
DEST="fonts"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
mkdir -p "$DEST"

echo "Inter $INTER_VERSION"
curl -fsSL -o "$TMP/inter.zip" "https://github.com/rsms/inter/releases/download/v$INTER_VERSION/Inter-$INTER_VERSION.zip"
unzip -q "$TMP/inter.zip" -d "$TMP/inter"
for face in Inter-Regular Inter-Medium InterDisplay-Regular InterDisplay-Medium InterDisplay-SemiBold InterDisplay-Bold InterDisplay-Black; do
  # The zip layout has moved between releases, so search for the static TTF instead of assuming a path.
  file="$(find "$TMP/inter" -type f -name "$face.ttf" | head -n 1)"
  if [ -z "$file" ]; then echo "  missing $face.ttf in the Inter zip" >&2; exit 1; fi
  cp "$file" "$DEST/$face.ttf"
done

echo "Newsreader Italic"
curl -fsSL -o "$DEST/Newsreader-Italic.ttf" "https://github.com/google/fonts/raw/main/ofl/newsreader/Newsreader-Italic%5Bopsz,wght%5D.ttf"

echo "JetBrains Mono"
for face in JetBrainsMono-Regular JetBrainsMono-Medium; do
  curl -fsSL -o "$DEST/$face.ttf" "https://github.com/JetBrains/JetBrainsMono/raw/master/fonts/ttf/$face.ttf"
done

ls -1 "$DEST"
