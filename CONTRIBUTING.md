# Contributing

Imperium is an experimental architecture project. Keep changes focused, source-grounded, and explicit about their validation limits.

## Before opening a change

1. Search existing issues and pull requests for related work.
2. Open an issue for changes to workflow semantics, persistence contracts, event delivery, or the OpenClaw integration.
3. Avoid unrelated dependency, generated-file, or formatting changes.

## Development workflow

1. Create a branch from the default branch.
2. Add or update tests for behavior changes, especially state transitions and recovery paths.
3. Run `mvn --batch-mode --no-transfer-progress verify` from the repository root.
4. Run `npm ci && npm run build` in `frontend`.
5. Run `python3 scripts/validate_repository.py`.
6. Update the architecture or security documentation when boundaries or configuration change.

Do not commit credentials, `.env`, private data, build outputs, or IDE metadata.

## Security reports

Do not post credentials, exploit details, or sensitive data in a public issue. Follow [SECURITY.md](SECURITY.md) and contact the repository owner privately.
