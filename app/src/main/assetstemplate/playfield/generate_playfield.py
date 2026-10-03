#!/usr/bin/env python3
# Copyright (c) 2016-2026 Salvatore D'Angelo
# Licensed under the MIT License. See the LICENSE file in the project root.
"""
Generate the in-game playfield art:

  assets/<color>block.png       40x40 glossy block for each tetromino color (falling/settled)
  assets/small<color>block.png  32x32 version for the Next/Hold previews
  assets/playfield.png          the 10x20 board: navy checkerboard, dot texture, glowing frame

The SVG sources are written next to this script. Requirements: see ../artkit.py.

Usage: python3 app/src/main/assetstemplate/playfield/generate_playfield.py
"""
import pathlib
import sys
import tempfile

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent))
from artkit import downscale, glossy_block, render, svg  # noqa: E402

ASSETS = HERE.parent.parent / "assets"
SUPERSAMPLE = 4

# Game color name (as used by the asset file names) -> artkit palette color.
BLOCK_COLORS = {
    "red": "pink", "orange": "orange", "yellow": "yellow", "green": "green",
    "cyan": "cyan", "blue": "blue", "magenta": "purple",
}
BLOCK, SMALL_BLOCK = 40, 32

# Board: 10x20 cells of 40px. The image has a MARGIN on every side for the frame and its glow,
# so it is drawn at (board x - MARGIN, board y - MARGIN).
COLS, ROWS, CELL = 10, 20, 40
MARGIN = 10
BOARD_W, BOARD_H = COLS * CELL, ROWS * CELL


def block(size, color):
    # A 1px gap on every side keeps neighbouring blocks visibly separate.
    inset = size / 40
    return svg(size, size, glossy_block(inset, inset, round(size - 2 * inset, 3), color, shadow=False))


def playfield():
    w, h = BOARD_W + 2 * MARGIN, BOARD_H + 2 * MARGIN
    cells = "".join(
        f'<rect x="{MARGIN + c * CELL}" y="{MARGIN + r * CELL}" width="{CELL}" height="{CELL}" fill="#1a2fa0"/>'
        for r in range(ROWS) for c in range(COLS) if (r + c) % 2 == 0
    )
    defs = (
        '<pattern id="dots" width="4" height="4" patternUnits="userSpaceOnUse">'
        '<circle cx="2" cy="2" r="0.7" fill="#000000" opacity="0.22"/></pattern>'
        '<linearGradient id="shade" x1="0" y1="0" x2="0" y2="1">'
        '<stop offset="0" stop-color="#ffffff" stop-opacity="0.06"/>'
        '<stop offset="1" stop-color="#000000" stop-opacity="0.18"/></linearGradient>'
        '<filter id="glow" x="-10%" y="-10%" width="120%" height="120%">'
        '<feGaussianBlur stdDeviation="3.5"/></filter>'
    )
    board = f'x="{MARGIN}" y="{MARGIN}" width="{BOARD_W}" height="{BOARD_H}"'
    return svg(w, h, (
        # outer glow, then the board itself on top of it
        f'<rect x="{MARGIN - 3}" y="{MARGIN - 3}" width="{BOARD_W + 6}" height="{BOARD_H + 6}" rx="6" '
        f'fill="none" stroke="#4f7bff" stroke-width="5" filter="url(#glow)"/>'
        f'<rect {board} fill="#14258c"/>{cells}'
        f'<rect {board} fill="url(#dots)"/><rect {board} fill="url(#shade)"/>'
        # frame: a bright inner line over a darker outer one
        f'<rect x="{MARGIN - 3}" y="{MARGIN - 3}" width="{BOARD_W + 6}" height="{BOARD_H + 6}" rx="5" '
        f'fill="none" stroke="#2a44d8" stroke-width="5"/>'
        f'<rect x="{MARGIN - 1.5}" y="{MARGIN - 1.5}" width="{BOARD_W + 3}" height="{BOARD_H + 3}" rx="4" '
        f'fill="none" stroke="#6f95ff" stroke-width="1.5"/>'
    ), defs)


def main():
    with tempfile.TemporaryDirectory() as tmp:
        tmp = pathlib.Path(tmp)

        def export(name, content, width, height):
            src = HERE / f"{name}.svg"
            src.write_text(content)
            master = tmp / f"{name}.png"
            render(src, master, width * SUPERSAMPLE, height * SUPERSAMPLE)
            downscale(master, ASSETS / f"{name}.png", width, height)

        for name, color in BLOCK_COLORS.items():
            export(f"{name}block", block(BLOCK, color), BLOCK, BLOCK)
            export(f"small{name}block", block(SMALL_BLOCK, color), SMALL_BLOCK, SMALL_BLOCK)
        export("playfield", playfield(), BOARD_W + 2 * MARGIN, BOARD_H + 2 * MARGIN)


if __name__ == "__main__":
    main()
