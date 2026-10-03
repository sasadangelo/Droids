#!/usr/bin/env python3
# Copyright (c) 2016-2026 Salvatore D'Angelo
# Licensed under the MIT License. See the LICENSE file in the project root.
"""
Generate the art of the home screen and the menus:

  assets/button_play.png    the big glossy green button (the label is drawn by the game)
  assets/button_blue.png    same, blue, for secondary actions (e.g. "Home" in the pause menu)
  assets/icon_trophy.png    high scores icon
  assets/icon_gear.png      settings icon
  assets/icon_power.png     quit icon
  assets/icon_back.png      back arrow of the menu pages

The SVG sources are written next to this script. Requirements: see ../artkit.py.

Usage: python3 app/src/main/assetstemplate/home/generate_home.py
"""
import pathlib
import sys
import tempfile

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE.parent))
from artkit import downscale, render, svg  # noqa: E402

ASSETS = HERE.parent.parent / "assets"
SUPERSAMPLE = 3

BUTTON_W, BUTTON_H = 420, 136
ICON = 72
ICON_COLOR = "#ffffff"
ICON_SHADOW = "#050a30"


# (top, middle, bottom, edge) colors of each glossy button.
BUTTONS = {
    "button_play": ("#8af06a", "#4fd336", "#36b524", "#1f7a14"),
    "button_blue": ("#9cc0ff", "#5b8cff", "#3f6cf0", "#2340a8"),
}


def glossy_button(top, middle, bottom, edge):
    w, h, r = BUTTON_W, BUTTON_H, 30
    face_h = h - 12
    defs = (
        '<linearGradient id="face" x1="0" y1="0" x2="0" y2="1">'
        f'<stop offset="0" stop-color="{top}"/><stop offset="0.5" stop-color="{middle}"/>'
        f'<stop offset="1" stop-color="{bottom}"/></linearGradient>'
    )
    return svg(w, h, (
        # drop shadow and darker bottom edge give it some thickness
        f'<rect x="4" y="10" width="{w - 8}" height="{face_h}" rx="{r}" fill="#050a30" opacity="0.4"/>'
        f'<rect x="4" y="4" width="{w - 8}" height="{face_h + 2}" rx="{r}" fill="{edge}"/>'
        f'<rect x="4" y="2" width="{w - 8}" height="{face_h - 4}" rx="{r}" fill="url(#face)"/>'
        f'<rect x="24" y="10" width="{w - 48}" height="{face_h * 0.28:.1f}" rx="{face_h * 0.14:.1f}" '
        f'fill="#ffffff" opacity="0.28"/>'
        f'<rect x="5.5" y="3.5" width="{w - 11}" height="{face_h - 7}" rx="{r - 1.5}" fill="none" '
        f'stroke="#ffffff" stroke-width="3" opacity="0.85"/>'
    ), defs)


def icon(body):
    """A white glyph with a hard drop shadow, on a transparent ICON x ICON canvas."""
    return svg(ICON, ICON, (
        f'<g transform="translate(0 3)" fill="{ICON_SHADOW}" stroke="{ICON_SHADOW}" opacity="0.6">{body}</g>'
        f'<g fill="{ICON_COLOR}" stroke="{ICON_COLOR}">{body}</g>'
    ))


def trophy():
    return icon(
        # cup, handles, stem and base
        '<path d="M20 10 H52 V28 A16 16 0 0 1 20 28 Z" stroke="none"/>'
        '<path d="M20 15 H11 V22 A9 9 0 0 0 21 31" fill="none" stroke-width="5" stroke-linecap="round"/>'
        '<path d="M52 15 H61 V22 A9 9 0 0 1 51 31" fill="none" stroke-width="5" stroke-linecap="round"/>'
        '<rect x="32" y="42" width="8" height="10" stroke="none"/>'
        '<rect x="22" y="52" width="28" height="9" rx="3" stroke="none"/>'
    )


def gear():
    teeth = "".join(
        f'<rect x="31" y="6" width="10" height="14" rx="2" transform="rotate({a} 36 36)"/>'
        for a in range(0, 360, 45)
    )
    mask = ('<mask id="hole"><rect width="72" height="72" fill="#ffffff"/>'
            '<circle cx="36" cy="36" r="9" fill="#000000"/></mask>')
    return svg(ICON, ICON, (
        f'<defs>{mask}</defs>'
        f'<g mask="url(#hole)" transform="translate(0 3)" fill="{ICON_SHADOW}" opacity="0.6">'
        f'{teeth}<circle cx="36" cy="36" r="21"/></g>'
        f'<g mask="url(#hole)" fill="{ICON_COLOR}">{teeth}<circle cx="36" cy="36" r="21"/></g>'
    ))


def power():
    return icon(
        '<path d="M24 20 A20 20 0 1 0 48 20" fill="none" stroke-width="7" stroke-linecap="round"/>'
        '<line x1="36" y1="10" x2="36" y2="34" stroke-width="7" stroke-linecap="round"/>'
    )


def back():
    return icon('<path d="M44 14 L24 36 L44 58" fill="none" stroke-width="9" '
                'stroke-linecap="round" stroke-linejoin="round"/>')


def main():
    with tempfile.TemporaryDirectory() as tmp:
        tmp = pathlib.Path(tmp)

        def export(name, content, width, height):
            src = HERE / f"{name}.svg"
            src.write_text(content)
            master = tmp / f"{name}.png"
            render(src, master, width * SUPERSAMPLE, height * SUPERSAMPLE)
            downscale(master, ASSETS / f"{name}.png", width, height)

        for name, colors in BUTTONS.items():
            export(name, glossy_button(*colors), BUTTON_W, BUTTON_H)
        export("icon_trophy", trophy(), ICON, ICON)
        export("icon_gear", gear(), ICON, ICON)
        export("icon_power", power(), ICON, ICON)
        export("icon_back", back(), ICON, ICON)


if __name__ == "__main__":
    main()
