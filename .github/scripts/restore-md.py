#!/usr/bin/env python3
"""
Remove SVG image references from markdown files (restore original plantuml blocks).
Used to clean up files before re-running the render pipeline.
"""

import re
from pathlib import Path

def remove_svg_references(md_file: Path):
    """Remove <!-- path/to/file.svg --> and ![...](path) added after plantuml blocks."""
    content = md_file.read_text(encoding="utf-8")
    original = content

    # Remove: <!-- path/to/file.svg -->
    content = re.sub(r'\n<!-- [^)]*\.svg -->\n', '\n', content)
    # Remove: ![diagram-name](path/to/file.svg)
    content = re.sub(r'\n!\[.*?\]\([^)]*\.svg\)\n', '\n', content)
    # Also remove if no newline before
    content = re.sub(r'!\[.*?\]\([^)]*\.svg\)', '', content)

    if content != original:
        md_file.write_text(content, encoding="utf-8")
        return True
    return False

def main():
    md_files = sorted(Path("docs").rglob("*.md"))
    cleaned = 0
    for md_file in md_files:
        if remove_svg_references(md_file):
            print(f"✓ Restored: {md_file}")
            cleaned += 1
    print(f"\nRestored {cleaned} file(s)")

if __name__ == "__main__":
    main()