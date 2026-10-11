# Security and configuration

Imperium is an experimental system that can dispatch external CLI work. Treat its integration endpoints and runtime environment as privileged.

## Required secrets

- `MYSQL_PASSWORD` authenticates the application database user.
- `AGENT_CALLBACK_SECRET` authenticates progress and result callbacks.
- `IMPERIUM_INTERNAL_SECRET` authenticates worker-to-API orchestration requests.

Use distinct random callback and internal secrets. The application has no fallback values for them. `.env.example` contains placeholders only and `.env` is ignored by Git.

## Local environment

Copy `.env.example` to `.env`, replace every placeholder, and keep the file on the development machine. The Docker Compose file refuses to interpolate required passwords or secrets when they are absent.

OpenClaw paths are mounted read-only where practical. Review the selected binary and state directory before starting the worker because dispatched tasks execute outside the Java process.

## Exposure

The default CORS configuration is for local development. Place authentication, TLS termination, and a restrictive network boundary in front of any remotely reachable deployment. Do not expose MySQL, RocketMQ, or internal callback endpoints to untrusted networks.

## Reporting

This repository does not currently publish a security policy or support window. Report suspected credential exposure privately to the repository owner rather than opening an issue containing secret material.
