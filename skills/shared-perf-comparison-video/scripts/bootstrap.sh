#!/bin/bash
# Prepare a Python environment for the skill's scripts and print the interpreter path.
#
# Pillow and numpy are not in the macOS system Python, and installing into it is
# both blocked on recent versions and rude. A throwaway venv beside the working
# files keeps the host untouched. Re-running is cheap: an existing venv is reused.
#
#   eval "$(scripts/bootstrap.sh)"     # exports PERF_PY
#   "$PERF_PY" scripts/make_video.py film.json
set -eu

VENV="${PERF_VIDEO_VENV:-$PWD/.perf-video-venv}"

missing=""
command -v ffmpeg  >/dev/null 2>&1 || missing="$missing ffmpeg"
command -v ffprobe >/dev/null 2>&1 || missing="$missing ffprobe"
if [ -n "$missing" ]; then
  echo "# MISSING:$missing  -> install with: brew install ffmpeg" >&2
  exit 1
fi

if [ ! -x "$VENV/bin/python" ]; then
  echo "# creating venv at $VENV" >&2
  python3 -m venv "$VENV" >&2
fi
"$VENV/bin/python" - <<'PY' >/dev/null 2>&1 || "$VENV/bin/pip" install --quiet --disable-pip-version-check pillow numpy >&2
import PIL, numpy
PY

echo "# ffmpeg $(ffmpeg -version 2>/dev/null | head -1 | cut -d' ' -f3), venv ready" >&2
echo "export PERF_PY=\"$VENV/bin/python\""
