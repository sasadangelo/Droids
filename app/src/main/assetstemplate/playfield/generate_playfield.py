#!/usr/bin/env python3
# Copyright (c) 2016-2026 Salvatore D'Angelo
# Licensed under the MIT License. See the LICENSE file in the project root.
"""
Generate the in-game art:

  assets/<color>block.png      64x64 glossy block for each tetromino color; the game scales it
                               (filtered) to the board's cell size and to the preview sizes
  assets/playfield.png         the 10x20 board at 64px per cell: navy checkerboard, dot texture,
                               glowing frame; the game scales it to the board's cell size
  assets/gamebg_<hue>.png      640x1520 game backgrounds, one hue per level tier

The SVG sources are written next to this script. Requirements: see ../artkit.py.

Usage: python3 app/src/main/assetstemplate/playfield/generate_playfield.py
"""
import pathlib
import sys
import tempfile

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent))
from artkit import (  # noqa: E402
    BLUE_GLOW, blue_background, downscale, glossy_block, render, scatter_tetrominoes, svg,
)

ASSETS = HERE.parent.parent / "assets"
SUPERSAMPLE = 3

# Game color name (as used by the asset file names) -> artkit palette color.
BLOCK_COLORS = {
    "red": "pink", "orange": "orange", "yellow": "yellow", "green": "green",
    "cyan": "cyan", "blue": "blue", "magenta": "purple",
}
BLOCK = 64

# Board: 10x20 cells of CELL px. The image has a MARGIN on every side for the frame and its glow;
# the game must keep that ratio (MARGIN / CELL) when scaling it.
COLS, ROWS, CELL = 10, 20, 64
MARGIN = 16
BOARD_W, BOARD_H = COLS * CELL, ROWS * CELL

# Game backgrounds: same size as the splash one (covers the centered 640x960 layout up to 21:9).
BG_WIDTH, BG_HEIGHT, BG_CELL = 640, 1520, 32
BACKGROUND_HUES = {
    "blue": BLUE_GLOW,
    "purple": ("#8a4dff", "#5a25c4", "#250b66"),
    "teal": ("#1fc4c4", "#0f8a94", "#053a4a"),
    "amber": ("#ffae2f", "#c4721a", "#663a0b"),
    "crimson": ("#ff3f6c", "#c41a45", "#660b24"),
    "olive": ("#9ccc2f", "#6b941a", "#2f4a0b"),
}


def block(size, color):
    # A small gap on every side keeps neighbouring blocks visibly separate.
    inset = size / 40
    return svg(size, size, glossy_block(inset, inset, round(size - 2 * inset, 3), color, shadow=False))


def playfield():
    k = CELL / 40  # frame/glow sizes were designed at 40px cells
    w, h = BOARD_W + 2 * MARGIN, BOARD_H + 2 * MARGIN
    cells = "".join(
        f'<rect x="{MARGIN + c * CELL}" y="{MARGIN + r * CELL}" width="{CELL}" height="{CELL}" fill="#1a2fa0"/>'
        for r in range(ROWS) for c in range(COLS) if (r + c) % 2 == 0
    )
    defs = (
        f'<pattern id="dots" width="{4 * k}" height="{4 * k}" patternUnits="userSpaceOnUse">'
        f'<circle cx="{2 * k}" cy="{2 * k}" r="{0.7 * k}" fill="#000000" opacity="0.22"/></pattern>'
        '<linearGradient id="shade" x1="0" y1="0" x2="0" y2="1">'
        '<stop offset="0" stop-color="#ffffff" stop-opacity="0.06"/>'
        '<stop offset="1" stop-color="#000000" stop-opacity="0.18"/></linearGradient>'
        '<filter id="glow" x="-10%" y="-10%" width="120%" height="120%">'
        f'<feGaussianBlur stdDeviation="{3.5 * k}"/></filter>'
    )
    board = f'x="{MARGIN}" y="{MARGIN}" width="{BOARD_W}" height="{BOARD_H}"'

    def frame(pad, stroke, color, rx, extra=""):
        return (f'<rect x="{MARGIN - pad}" y="{MARGIN - pad}" width="{BOARD_W + 2 * pad}" '
                f'height="{BOARD_H + 2 * pad}" rx="{rx}" fill="none" stroke="{color}" '
                f'stroke-width="{stroke}"{extra}/>')

    return svg(w, h, (
        frame(3 * k, 5 * k, "#4f7bff", 6 * k, ' filter="url(#glow)"')
        + f'<rect {board} fill="#14258c"/>{cells}'
        + f'<rect {board} fill="url(#dots)"/><rect {board} fill="url(#shade)"/>'
        + frame(3 * k, 5 * k, "#2a44d8", 5 * k)
        + frame(1.5 * k, 1.5 * k, "#6f95ff", 4 * k)
    ), defs)


def main():
    with tempfile.TemporaryDirectory() as tmp:
        tmp = pathlib.Path(tmp)

        def export(name, content, width, height, opaque=False):
            src = HERE / f"{name}.svg"
            src.write_text(content)
            master = tmp / f"{name}.png"
            render(src, master, width * SUPERSAMPLE, height * SUPERSAMPLE)
            downscale(master, ASSETS / f"{name}.png", width, height, opaque=opaque)

        for name, color in BLOCK_COLORS.items():
            export(f"{name}block", block(BLOCK, color), BLOCK, BLOCK)
        export("playfield", playfield(), BOARD_W + 2 * MARGIN, BOARD_H + 2 * MARGIN)

        spots = scatter_tetrominoes(BG_WIDTH // BG_CELL, BG_HEIGHT // BG_CELL, seed=2026)
        for hue, colors in BACKGROUND_HUES.items():
            export(f"gamebg_{hue}", blue_background(BG_WIDTH, BG_HEIGHT, BG_CELL, spots, glow_y=0.45, colors=colors),
                   BG_WIDTH, BG_HEIGHT, opaque=True)


if __name__ == "__main__":
    main()
