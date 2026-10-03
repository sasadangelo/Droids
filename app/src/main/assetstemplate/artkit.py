# Copyright (c) 2016-2026 Salvatore D'Angelo
# Licensed under the MIT License. See the LICENSE file in the project root.
"""
Shared building blocks for the generated Droids art (launcher icon, splash screen, transition
blocks): the block palette, the glossy block drawing, the blue tetromino background and an SVG
rasterizer.

Requirements: Python 3 (standard library only), Google Chrome (used headless as the SVG
renderer, since it handles gradients and patterns correctly) and ImageMagick (`magick`).
"""
import pathlib
import random
import subprocess
import tempfile

CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

# name: (light, base, dark)
PALETTE = {
    "pink": ("#ff8fa3", "#ff4f6e", "#c22a48"),
    "orange": ("#ffb347", "#ff8a00", "#c45f00"),
    "yellow": ("#fff06a", "#ffd000", "#c99a00"),
    "green": ("#8af06a", "#3cd62a", "#22941a"),
    "cyan": ("#7fe3ff", "#14b8f0", "#0a7fb0"),
    "blue": ("#9cb0ff", "#5c7cff", "#3550c8"),
    "purple": ("#e29cff", "#c055f0", "#8a2fb8"),
}

# (center, middle, edge) of the radial glow behind the blue backgrounds.
BLUE_GLOW = ("#2f6cff", "#1a3fc4", "#0b1a66")

TETROMINOES = [
    [(0, 0), (1, 0), (2, 0), (1, 1)], [(0, 0), (1, 0), (0, 1), (1, 1)],
    [(0, 0), (0, 1), (0, 2), (1, 2)], [(0, 0), (1, 0), (1, 1), (2, 1)],
    [(0, 0), (1, 0), (2, 0), (3, 0)],
]


def gradients():
    return "".join(
        f'<linearGradient id="g-{name}" x1="0" y1="0" x2="0" y2="1">'
        f'<stop offset="0" stop-color="{light}"/><stop offset="0.55" stop-color="{base}"/>'
        f'<stop offset="1" stop-color="{base}"/></linearGradient>'
        for name, (light, base, _) in PALETTE.items()
    )


def svg(width, height, content, defs=""):
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" '
        f'viewBox="0 0 {width} {height}"><defs>{gradients()}{defs}</defs>{content}</svg>\n'
    )


def glossy_block(x, y, s, color, shadow=True):
    """A rounded block with a darker bottom edge, a vertical gradient and a top highlight."""
    _, _, dark = PALETTE[color]
    r = s * 0.2
    out = ""
    if shadow:
        out += (f'<rect x="{x:.2f}" y="{y + s * 0.12:.2f}" width="{s}" height="{s}" rx="{r:.2f}" '
                f'fill="#050a30" opacity="0.45"/>')
    return out + (
        f'<rect x="{x:.2f}" y="{y:.2f}" width="{s}" height="{s}" rx="{r:.2f}" fill="{dark}"/>'
        f'<rect x="{x:.2f}" y="{y:.2f}" width="{s}" height="{s * 0.9:.2f}" rx="{r:.2f}" fill="url(#g-{color})"/>'
        f'<rect x="{x + s * 0.14:.2f}" y="{y + s * 0.09:.2f}" width="{s * 0.72:.2f}" height="{s * 0.2:.2f}" '
        f'rx="{s * 0.1:.2f}" fill="#ffffff" opacity="0.45"/>'
    )


def scatter_tetrominoes(cols, rows, seed):
    """
    Scatter non-overlapping tetromino silhouettes on a cols x rows grid (deterministic for a
    given seed), as (grid_x, grid_y, tetromino_index) spots for blue_background().
    """
    rng = random.Random(seed)
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


def blue_background(width, height, cell, spots, glow_y=0.42, colors=None):
    """
    Radial blue glow, a fine dot texture and faint tetromino silhouettes. [spots] lists
    (grid_x, grid_y, tetromino_index) on a grid of [cell] units. [colors] overrides the
    (center, middle, edge) colors of the glow, e.g. for hue variants.
    """
    center, middle, edge = colors or BLUE_GLOW
    tile = cell * 0.82
    tiles = "".join(
        f'<rect x="{(gx + dx) * cell + (cell - tile) / 2:.1f}" y="{(gy + dy) * cell + (cell - tile) / 2:.1f}" '
        f'width="{tile:.1f}" height="{tile:.1f}" rx="{cell * 0.12:.1f}" fill="#ffffff" opacity="0.07"/>'
        for gx, gy, k in spots for dx, dy in TETROMINOES[k]
    )
    dot = cell * 0.25
    defs = (
        f'<radialGradient id="bg" cx="0.5" cy="{glow_y}" r="0.7">'
        f'<stop offset="0" stop-color="{center}"/><stop offset="0.55" stop-color="{middle}"/>'
        f'<stop offset="1" stop-color="{edge}"/></radialGradient>'
        f'<pattern id="dots" width="{dot:.2f}" height="{dot:.2f}" patternUnits="userSpaceOnUse">'
        f'<circle cx="{dot / 2:.2f}" cy="{dot / 2:.2f}" r="{dot * 0.18:.2f}" fill="#000000" opacity="0.18"/></pattern>'
    )
    return svg(
        width, height,
        f'<rect width="{width}" height="{height}" fill="url(#bg)"/>'
        f'<rect width="{width}" height="{height}" fill="url(#dots)"/>' + tiles,
        defs,
    )


def render(svg_path, png_path, width, height=None):
    """Rasterize an SVG with headless Chrome on a transparent page."""
    height = height or width
    with tempfile.TemporaryDirectory() as tmp:
        page = pathlib.Path(tmp) / "page.html"
        page.write_text(
            "<html><body style='margin:0;background:transparent'>"
            f"<img src='{pathlib.Path(svg_path).resolve().as_uri()}' "
            f"style='display:block;width:{width}px;height:{height}px'></body></html>"
        )
        subprocess.run(
            [CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--allow-file-access-from-files",
             "--default-background-color=00000000", f"--window-size={width},{height}",
             f"--screenshot={pathlib.Path(png_path).resolve()}", page.as_uri()],
            check=True, capture_output=True,
        )


def magick(*args):
    subprocess.run(["magick", *map(str, args)], check=True)


def downscale(src, dst, width, height=None, opaque=False):
    """High-quality resize to the final asset size, 8 bits per channel, metadata stripped."""
    args = [src, "-filter", "Lanczos", "-resize", f"{width}x{height or width}!"]
    if opaque:
        args += ["-background", "black", "-alpha", "remove", "-alpha", "off"]
    magick(*args, "-strip", "-define", "png:bit-depth=8", dst)
