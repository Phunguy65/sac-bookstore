#!/usr/bin/env python3
"""
Extract PlantUML blocks from docs/*.md, render to SVG, and patch markdown files.

Workflow:
1. Scan all docs/**/*.md files for ```plantuml blocks
2. Render each block to SVG via PlantUML JAR (stdin→stdout)
3. Patch markdown: add ![](images/...) after each ```plantuml block
4. Delete images no longer referenced in any .md file
"""

import re
import os
import sys
import subprocess
import hashlib
from pathlib import Path

# ── Configuration ───────────────────────────────────────────────────────────────

PLANTUML_JAR = Path(__file__).parent / "plantuml.jar"
SVG_BASE_DIR = Path("docs/images")

CATEGORY_MAP = {
    "db.md": ("db", "db"),
    "arch.md": ("arch", "arch"),
    "deployment.md": ("deploy", "deploy"),
}

# ── Helpers ───────────────────────────────────────────────────────────────────

def clean_puml(puml: str) -> str:
    """Strip !include lines from PlantUML content (Q5 requirement)."""
    lines = puml.splitlines()
    cleaned = [line for line in lines if not line.strip().startswith("!include")]
    return "\n".join(cleaned)


def render_svg(puml_content: str) -> str | None:
    """Render PlantUML content to SVG via PlantUML Docker container stdin→stdout."""
    cmd = [
        "docker", "run", "--rm", "-i",
        "plantuml/plantuml:latest",
        "-tsvg", "-pipe",
    ]
    try:
        result = subprocess.run(
            cmd,
            input=puml_content.encode("utf-8"),
            capture_output=True,
            timeout=60,
            check=False,  # Don't raise on rc=200 (PlantUML uses it for syntax errors)
        )
        # PlantUML Docker exits rc=200 even when it produces valid SVG with warnings.
        # Check stdout for actual SVG content — that's the real success signal.
        stdout = result.stdout.decode("utf-8", errors="replace")
        if stdout.strip().startswith(("<?xml", "<?plantuml", "<svg")):
            return stdout
        # No valid SVG: print error and return None
        stderr = result.stderr.decode("utf-8", errors="replace")
        if stderr.strip():
            print(f"  ⚠ PlantUML: {stderr.strip()[:200]}", file=sys.stderr)
        return None
    except subprocess.TimeoutExpired:
        print(f"  ⚠ Timeout rendering diagram", file=sys.stderr)
        return None
    except Exception as e:
        print(f"  ⚠ Unexpected error: {e}", file=sys.stderr)
        return None


def slugify(name: str) -> str:
    """Convert diagram name to safe lowercase slug."""
    # Take last segment after @startuml
    name = name.strip()
    # Remove @startuml prefix
    name = re.sub(r"^@startuml\s*", "", name, flags=re.IGNORECASE)
    # Remove surrounding quotes if present
    name = re.sub(r'^["\'](.+)["\']\s*$', r"\1", name)
    # Replace spaces/dashes with underscore, lowercase
    slug = re.sub(r"[^a-z0-9_]", "_", name.lower())
    slug = re.sub(r"_+", "_", slug).strip("_")
    return slug or "diagram"


def get_output_path(md_file: str, block_index: int, diagram_name: str) -> Path:
    """Build SVG output path based on markdown file and block index."""
    md_basename = Path(md_file).name

    if md_basename in CATEGORY_MAP:
        category, prefix = CATEGORY_MAP[md_basename]
        filename = f"{prefix}-{block_index:02d}.svg"
    elif md_file.startswith("docs/usecase/"):
        category = "usecase"
        uc_num = re.match(r"(\d+)-", Path(md_file).stem)
        idx = int(uc_num.group(1)) if uc_num else block_index
        filename = f"uc-{idx:02d}.svg"
    else:
        category = "misc"
        filename = f"misc-{block_index:02d}.svg"

    return SVG_BASE_DIR / category / filename


def extract_plantuml_blocks(md_file: Path) -> list[dict]:
    """Extract all ```plantuml blocks from a markdown file."""
    content = md_file.read_text(encoding="utf-8")

    # Find all fenced plantuml blocks
    pattern = re.compile(
        r"```plantuml\s*\n(.*?)\n```",
        re.DOTALL | re.IGNORECASE,
    )

    blocks = []
    for i, match in enumerate(pattern.finditer(content)):
        puml_content = match.group(1)
        start_pos = match.start()
        end_pos = match.end()

        # Extract @startuml name (same line, anywhere in content)
        name_match = re.search(r"@startuml\s+(\S+)", puml_content, re.IGNORECASE)
        if name_match:
            diagram_name = name_match.group(1)
        else:
            # @startuml without name → use sequential name
            diagram_name = f"diagram-{i+1}"

        blocks.append({
            "content": puml_content,
            "name": diagram_name,
            "index": i + 1,
            "start": start_pos,
            "end": end_pos,
        })

    return blocks


def patch_markdown(md_file: Path, blocks: list[dict]) -> tuple[str, set[str]]:
    """
    Patch markdown file: insert SVG image reference after each plantuml block.

    Returns:
        (new_content, set of referenced image paths)
    """
    content = md_file.read_text(encoding="utf-8")
    referenced_images: set[str] = set()

    # Process blocks in reverse order so positions don't shift
    for block in reversed(blocks):
        svg_path = get_output_path(str(md_file), block["index"], block["name"])
        rel_path = svg_path.as_posix()  # e.g. "docs/images/db/db-01.svg"

        referenced_images.add(rel_path)

        # Build the marker comment + image markdown
        marker = f"\n<!-- {rel_path} -->\n![{block['name']}]({rel_path})\n"

        # Find the block position and insert after it
        # The block spans from block["start"] to block["end"]
        insert_pos = block["end"]
        content = content[:insert_pos] + marker + content[insert_pos:]

    return content, referenced_images


# ── Main ──────────────────────────────────────────────────────────────────────

def main():
    print("=" * 60)
    print("PlantUML Diagram Renderer (Docker)")
    print("=" * 60)

    svg_base = SVG_BASE_DIR
    svg_base.mkdir(parents=True, exist_ok=True)
    for sub in ["db", "arch", "deploy", "usecase"]:
        (svg_base / sub).mkdir(parents=True, exist_ok=True)

    # Collect all referenced images across all markdown files
    all_referenced: set[str] = set()

    # Process all markdown files
    md_files = sorted(Path("docs").rglob("*.md"))
    print(f"\nScanning {len(md_files)} markdown file(s)...\n")

    rendered_count = 0
    error_count = 0

    for md_file in md_files:
        blocks = extract_plantuml_blocks(md_file)
        if not blocks:
            continue

        print(f"📄 {md_file}")
        print(f"   Found {len(blocks)} diagram(s)")

        new_content, referenced = patch_markdown(md_file, blocks)
        all_referenced.update(referenced)

        # Render each block
        for block in blocks:
            cleaned_puml = clean_puml(block["content"])
            svg_path = get_output_path(str(md_file), block["index"], block["name"])

            print(f"   → Rendering: {block['name']} → {svg_path}", end=" ... ")

            svg_content = render_svg(cleaned_puml)
            if svg_content:
                svg_path.parent.mkdir(parents=True, exist_ok=True)
                svg_path.write_text(svg_content, encoding="utf-8")
                print(f"✓ ({len(svg_content):,} bytes)")
                rendered_count += 1
            else:
                print(f"✗ FAILED")
                error_count += 1

        # Write patched markdown
        md_file.write_text(new_content, encoding="utf-8")
        print(f"   ✓ Patched {md_file.name}")

    # Cleanup: delete images not referenced in any markdown
    print(f"\n🧹 Cleaning unreferenced images...")
    known_refs = {Path(p) for p in all_referenced}

    for sub in ["db", "arch", "deploy", "usecase"]:
        img_dir = svg_base / sub
        if not img_dir.exists():
            continue

        for svg_file in img_dir.glob("*.svg"):
            if svg_file not in known_refs:
                print(f"   🗑 Removing {svg_file}")
                svg_file.unlink()

    # Summary
    print(f"\n{'=' * 60}")
    print(f"✓ Rendered: {rendered_count}")
    if error_count > 0:
        print(f"✗ Errors:   {error_count}")
    print(f"📁 Images:   {SVG_BASE_DIR}")
    print(f"{'=' * 60}")

    if error_count > 0:
        sys.exit(1)


if __name__ == "__main__":
    main()