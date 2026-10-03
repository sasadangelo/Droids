#!/usr/bin/env python3
# Copyright (c) 2016-2026 Salvatore D'Angelo
# Licensed under the MIT License. See the LICENSE file in the project root.
"""
Generate the splash screen art and the blocks used by the block-wipe screen transition:

  assets/splash_background.png  640x960 blue background with faint tetromino silhouettes
  assets/splash_logo.png        the "DROIDS" wordmark made of glossy blocks
  assets/wipe_<color>.png       one glossy block per palette color, for the block wipe

The SVG sources are written next to this script. Requirements: see ../artkit.py.

Usage: python3 app/src/main/assetstemplate/splash/generate_splash.py
"""
import pathlib
import random
import sys
import tempfile

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent))
from artkit import PALETTE, TETROMINOES, blue_background, downscale, glossy_block, render, svg  # noqa: E402

ASSETS = HERE.parent.parent / "assets"
SUPERSAMPLE = 3

WIDTH, HEIGHT = 640, 960
BACKGROUND_CELL = 32

# 3x5 pixel font, one color per letter (same rainbow order as the icon).
LETTERS = {
    "D": ["XX.", "X.X", "X.X", "X.X", "XX."],
    "R": ["XX.", "X.X", "XX.", "X.X", "X.X"],
    "O": ["XXX", "X.X", "X.X", "X.X", "XXX"],
    "I": ["XXX", ".X.", ".X.", ".X.", "XXX"],
    "S": ["XXX", "X..", "XXX", "..X", "XXX"],
}
WORD = [("D", "pink"), ("R", "orange"), ("O", "yellow"), ("I", "green"), ("D", "cyan"), ("S", "purple")]
LOGO_CELL, LOGO_GAP, LETTER_GAP = 24, 3, 12

WIPE_BLOCK = 80


def background_spots():
    """Scatter non-overlapping tetromino silhouettes on the background grid (fixed seed)."""
    cols, rows = WIDTH // BACKGROUND_CELL, HEIGHT // BACKGROUND_CELL
    rng = random.Random(1985)
    taken, spots = set(), []
    for _ in range(400):
        k = rng.randrange(len(TETROMINOES))
        gx, gy = rng.randrange(-1, cols), rng.randrange(-1, rows)
        cells = {(gx + dx, gy + dy) for dx, dy in TETROMINOES[k]}
        halo = {(x + ox, y + oy) for x, y in cells for ox in (-1, 0, 1) for oy in (-1, 0, 1)}
        if halo & taken:
            continue
        taken |= cells
        spots.append((gx, gy, k))
    return spots


def logo():
    blocks, x = [], 0
    for letter, color in WORD:
        glyph = LETTERS[letter]
        for r, row in enumerate(glyph):
            for c, ch in enumerate(row):
                if ch == "X":
                    blocks.append(glossy_block(x + c * (LOGO_CELL + LOGO_GAP), r * (LOGO_CELL + LOGO_GAP), LOGO_CELL, color))
        x += len(glyph[0]) * (LOGO_CELL + LOGO_GAP) - LOGO_GAP + LETTER_GAP
    width = x - LETTER_GAP
    height = 5 * (LOGO_CELL + LOGO_GAP) - LOGO_GAP + round(LOGO_CELL * 0.12)  # room for the drop shadow
    return width, height, svg(width, height, "".join(blocks))


def wipe_block(color):
    inset = WIPE_BLOCK * 0.04
    size = WIPE_BLOCK - 2 * inset
    return svg(WIPE_BLOCK, WIPE_BLOCK, glossy_block(inset, inset * 0.5, round(size, 2), color))


def main():
    with tempfile.TemporaryDirectory() as tmp:
        tmp = pathlib.Path(tmp)

        def export(name, content, width, height, opaque=False):
            src = HERE / f"{name}.svg"
            src.write_text(content)
            master = tmp / f"{name}.png"
            render(src, master, width * SUPERSAMPLE, height * SUPERSAMPLE)
            downscale(master, ASSETS / f"{name}.png", width, height, opaque=opaque)

        export("splash_background", blue_background(WIDTH, HEIGHT, BACKGROUND_CELL, background_spots(), glow_y=0.4),
               WIDTH, HEIGHT, opaque=True)
        width, height, content = logo()
        export("splash_logo", content, width, height)
        for color in PALETTE:
            export(f"wipe_{color}", wipe_block(color), WIPE_BLOCK, WIPE_BLOCK)


if __name__ == "__main__":
    main()
