#!/usr/bin/env python3
"""Check rendered Lottie sample coverage, visible content, and GIF motion.

Run compose-preview show --module :remotecompose:lottie first. Requires Pillow.
An optional --contact-sheet path writes a visual review sheet of capture frames.
"""

import argparse
import hashlib
import json
from pathlib import Path
import re

from PIL import Image, ImageDraw, ImageSequence


def has_animation(value):
    if isinstance(value, dict):
        return value.get("a") == 1 or any(has_animation(v) for v in value.values())
    if isinstance(value, list):
        return any(has_animation(v) for v in value)
    return False


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--contact-sheet", type=Path)
    args = parser.parse_args()
    module = Path(__file__).resolve().parents[1]
    output = module / "build/compose-previews"
    manifest = json.loads((output / "previews.json").read_text())
    samples = {
        p.stem: has_animation(json.loads(p.read_text()))
        for p in (module / "src/debug/res/raw").glob("*.json")
    }
    coverage = {name: set() for name in samples}
    errors = []
    tiles = []
    gif_count = 0
    moving_count = 0
    for preview in manifest["previews"]:
        name = preview["functionName"]
        source = (module / preview["sourceFile"]).read_text()
        body = re.search(r"fun\s+" + re.escape(name) + r"\(\)\s*\{(.*?)\n\}", source, re.S)
        sample = re.search(r"R\.raw\.(\w+)", body[1]) if body else None
        if not sample or sample[1] not in samples:
            errors.append(f"{name}: cannot resolve bundled sample")
            continue
        for capture in preview["captures"]:
            path = output / capture["renderOutput"]
            animation = capture.get("animation")
            coverage[sample[1]].add("animated" if animation else "still")
            if not path.is_file():
                errors.append(f"{name}: missing {path}")
                continue
            with Image.open(path) as image:
                image_format = image.format
                frames = [frame.convert("RGBA") for frame in ImageSequence.Iterator(image)]
            unique = len({hashlib.sha256(frame.tobytes()).digest() for frame in frames})
            if not any(len(frame.getcolors(frame.width * frame.height)) > 1 for frame in frames):
                errors.append(f"{name}: every frame is a solid color")
            if animation:
                gif_count += 1
                moving_count += unique > 1
                expected = animation["durationMs"] // animation["frameIntervalMs"] + 1
                if image_format != "GIF" or len(frames) != expected:
                    errors.append(f"{name}: expected GIF with {expected} frames, got {image_format}/{len(frames)}")
                if samples[sample[1]] and unique < 2:
                    errors.append(f"{name}: animated sample has no changing frames")
            elif image_format != "PNG":
                errors.append(f"{name}: expected PNG, got {image_format}")
            print(f"{name}: {len(frames)} frames, {unique} distinct")
            if args.contact_sheet:
                tile = Image.new("RGB", (400, 174), "#cccccc")
                ImageDraw.Draw(tile).text((8, 5), name.removeprefix("Lottie"), fill="black")
                indices = [0, len(frames) // 2, len(frames) - 1] if animation else [0]
                for column, index in enumerate(indices):
                    thumb = frames[index].copy()
                    thumb.thumbnail((124, 136))
                    tile.paste(thumb, (column * 132 + 4, 30), thumb)
                tiles.append(tile)
    for sample, kinds in coverage.items():
        if kinds != {"still", "animated"}:
            errors.append(f"{sample}: missing preview kinds { {'still', 'animated'} - kinds }")
    if args.contact_sheet and tiles:
        sheet = Image.new("RGB", (1200, 174 * ((len(tiles) + 2) // 3)), "white")
        for index, tile in enumerate(tiles):
            sheet.paste(tile, ((index % 3) * 400, (index // 3) * 174))
        sheet.save(args.contact_sheet)
    print(f"{len(samples)} samples; {len(manifest['previews'])} previews; {gif_count} GIFs; {moving_count} moving GIFs")
    if errors:
        raise SystemExit("\n".join(errors))
    print("PASS: coverage, visible content, GIF format/frame counts, and expected motion")


if __name__ == "__main__":
    main()
