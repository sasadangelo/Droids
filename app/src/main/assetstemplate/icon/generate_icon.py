#!/usr/bin/env python3
# Copyright (c) 2016-2026 Salvatore D'Angelo
# Licensed under the MIT License. See the LICENSE file in the project root.
"""
Generate the Droids launcher icon: a "D" made of glossy blocks on a blue background.

Writes the three adaptive-icon layers as SVG sources next to this script, then rasterizes them
into every mipmap density bucket, together with the legacy square/round icons for launchers
older than API 26 and the 512x512 Play Store icon.

Requirements: see ../artkit.py.

Usage: python3 app/src/main/assetstemplate/icon/generate_icon.py
"""
import pathlib
import sys
import tempfile

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent))
from artkit import blue_background, downscale, glossy_block, magick, render, svg  # noqa: E402

MAIN = HERE.parent.parent
RES = MAIN / "res"

# Adaptive-icon layers are 108dp; only the central 72dp are visible once the launcher masks them.
DENSITIES = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}
MASTER = 1296  # 12x the 108dp canvas, so every bucket is a clean downscale

# The "D", walked clockwise from its top-left corner so the colors loop around it like a rainbow.
D_CELLS = [
    ((0, 0), "pink"), ((1, 0), "orange"), ((2, 0), "yellow"),
    ((3, 1), "yellow"), ((3, 2), "green"), ((3, 3), "green"),
    ((2, 4), "cyan"), ((1, 4), "cyan"), ((0, 4), "blue"),
    ((0, 3), "blue"), ((0, 2), "purple"), ((0, 1), "purple"),
]
# Small enough for the whole "D" to fit the 66dp safe zone every launcher mask keeps visible.
CELL = 9.4
GAP = 1.1

BACKGROUND_SPOTS = [(-1, 1, 0), (6, 0, 1), (12, 2, 2), (2, 6, 3), (10, 8, 4), (-1, 11, 1),
                    (13, 12, 0), (4, 13, 2), (8, 15, 3), (15, 5, 2), (1, 16, 4)]


def cell_origins():
    cols = max(c for (c, _), _ in D_CELLS) + 1
    rows = max(r for (_, r), _ in D_CELLS) + 1
    ox = (108 - (cols * CELL + (cols - 1) * GAP)) / 2
    oy = (108 - (rows * CELL + (rows - 1) * GAP)) / 2
    for (c, r), color in D_CELLS:
        yield ox + c * (CELL + GAP), oy + r * (CELL + GAP), color


def foreground():
    return svg(108, 108, "".join(glossy_block(x, y, CELL, color) for x, y, color in cell_origins()))


def monochrome():
    # Themed icons (Android 13+) only use this layer's alpha, so a flat silhouette is enough.
    return svg(108, 108, "".join(
        f'<rect x="{x:.2f}" y="{y:.2f}" width="{CELL}" height="{CELL}" rx="{CELL * 0.2:.2f}" fill="#ffffff"/>'
        for x, y, _ in cell_origins()
    ))


def main():
    layers = {
        "background": blue_background(108, 108, 6.5, BACKGROUND_SPOTS),
        "foreground": foreground(),
        "monochrome": monochrome(),
    }
    with tempfile.TemporaryDirectory() as tmp:
        tmp = pathlib.Path(tmp)
        masters = {}
        for name, content in layers.items():
            src = HERE / f"ic_launcher_{name}.svg"
            src.write_text(content)
            masters[name] = tmp / f"{name}.png"
            render(src, masters[name], MASTER)

        # Legacy icons show only the 72dp visible part of the 108dp layers.
        crop = MASTER * 72 // 108
        flat = tmp / "flat.png"
        magick(masters["background"], masters["foreground"], "-composite",
               "-gravity", "center", "-crop", f"{crop}x{crop}+0+0", "+repage", flat)
        square_mask, round_mask = tmp / "square_mask.png", tmp / "round_mask.png"
        magick("-size", f"{crop}x{crop}", "xc:none", "-fill", "white",
               "-draw", f"roundrectangle 0,0 {crop - 1},{crop - 1} {crop * 0.18},{crop * 0.18}", square_mask)
        magick("-size", f"{crop}x{crop}", "xc:none", "-fill", "white",
               "-draw", f"circle {crop / 2},{crop / 2} {crop / 2},0", round_mask)
        legacy = {}
        for kind, mask in (("ic_launcher", square_mask), ("ic_launcher_round", round_mask)):
            legacy[kind] = tmp / f"{kind}.png"
            magick(flat, "-alpha", "set", mask, "-compose", "DstIn", "-composite", legacy[kind])

        for bucket, scale in DENSITIES.items():
            out = RES / f"mipmap-{bucket}"
            out.mkdir(exist_ok=True)
            for name, master in masters.items():
                downscale(master, out / f"ic_launcher_{name}.png", round(108 * scale))
            for kind, master in legacy.items():
                downscale(master, out / f"{kind}.png", round(48 * scale))

        # Play Store listing icon: full square, Google Play applies its own rounding.
        downscale(flat, MAIN / "ic_launcher-playstore.png", 512)


if __name__ == "__main__":
    main()
