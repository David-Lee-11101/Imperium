#!/usr/bin/env python3
"""Dependency-free repository policy checks used locally and in CI."""

import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
README = ROOT / "README.md"
RUNTIME_FILES = [
    ROOT / "docker-compose.yml",
    ROOT / ".env.example",
    *ROOT.glob("backend/*/src/main/resources/application*.yml"),
    *ROOT.glob("backend/*/src/main/java/**/*.java"),
]
PUBLIC_UI_FILES = [
    ROOT / "frontend" / "index.html",
    *ROOT.glob("frontend/src/**/*.tsx"),
]
PUBLIC_API_FILES = [
    *ROOT.glob("backend/imperium-api/src/main/java/com/imperium/api/exception/*.java"),
    *ROOT.glob("backend/imperium-api/src/main/java/com/imperium/api/model/*.java"),
    *ROOT.glob("backend/imperium-api/src/main/java/com/imperium/api/service/*.java"),
]
BANNED_RUNTIME_TEXT = (
    "dev-secret-change-in-prod",
    "${MYSQL_PASSWORD:root}",
    "AGENT_CALLBACK_SECRET=dev-",
    "IMPERIUM_INTERNAL_SECRET=dev-",
)


def validate() -> list[str]:
    errors: list[str] = []
    readme = README.read_text(encoding="utf-8")
    if re.search(r"[\u4e00-\u9fff]", readme):
        errors.append("README.md must remain English")
    for target in re.findall(r"\[[^]]+\]\(([^)]+)\)", readme):
        if target.startswith(("https://", "http://", "#")):
            continue
        local = (ROOT / target.split("#", 1)[0]).resolve()
        if not local.exists():
            errors.append(f"README local link does not exist: {target}")
    for path in RUNTIME_FILES:
        text = path.read_text(encoding="utf-8")
        for banned in BANNED_RUNTIME_TEXT:
            if banned in text:
                errors.append(f"unsafe configuration fallback in {path.relative_to(ROOT)}: {banned}")
    for path in PUBLIC_UI_FILES:
        text = path.read_text(encoding="utf-8")
        if re.search(r"[\u3400-\u4dbf\u4e00-\u9fff]", text):
            errors.append(f"promoted UI must remain English: {path.relative_to(ROOT)}")
    if 'lang="en"' not in (ROOT / "frontend" / "index.html").read_text(encoding="utf-8"):
        errors.append("frontend/index.html must declare English document language")
    java_string_with_cjk = re.compile(
        r'"(?:\\.|[^"\\])*[\u3400-\u4dbf\u4e00-\u9fff](?:\\.|[^"\\])*"'
    )
    for path in PUBLIC_API_FILES:
        for line_number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
            if java_string_with_cjk.search(line) and not re.search(r"\blog\.\w+\(", line):
                errors.append(
                    "public API runtime text must remain English: "
                    f"{path.relative_to(ROOT)}:{line_number}"
                )
    return errors


if __name__ == "__main__":
    failures = validate()
    if failures:
        print("\n".join(f"ERROR: {item}" for item in failures), file=sys.stderr)
        raise SystemExit(1)
    print("repository policy checks passed")
