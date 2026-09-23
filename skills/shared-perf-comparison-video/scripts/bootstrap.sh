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

fail() {
  echo "$1" >&2
  echo "#" >&2
  echo "# Fix this rather than hand-writing ffmpeg: the entire look of the film lives" >&2
  echo "# in make_video.py, and an improvised renderer produces an off-style video" >&2
  echo "# that gets rejected. If you cannot fix it, report the blocker and hand over" >&2
  echo "# the measured instants in a film.json instead." >&2
  exit 1
}

missing=""
command -v ffmpeg  >/dev/null 2>&1 || missing="$missing ffmpeg"
command -v ffprobe >/dev/null 2>&1 || missing="$missing ffprobe"
[ -n "$missing" ] && fail "# MISSING:$missing  -> install with: brew install ffmpeg"
command -v python3 >/dev/null 2>&1 || fail "# MISSING python3 -> install with: brew install python"

if [ ! -x "$VENV/bin/python" ]; then
  echo "# creating venv at $VENV" >&2
  python3 -m venv "$VENV" >&2 || fail "# could not create a venv at $VENV"
fi
"$VENV/bin/python" - <<'PY' >/dev/null 2>&1 || "$VENV/bin/pip" install --quiet --disable-pip-version-check pillow numpy >&2
import PIL, numpy
PY
"$VENV/bin/python" - <<'PY' >/dev/null 2>&1 || fail "# pillow/numpy would not install into $VENV"
import PIL, numpy
PY

echo "# ffmpeg $(ffmpeg -version 2>/dev/null | head -1 | cut -d' ' -f3), venv ready" >&2
echo "export PERF_PY=\"$VENV/bin/python\""
