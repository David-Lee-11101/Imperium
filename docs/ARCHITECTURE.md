# Architecture

Imperium separates governance decisions from execution. The API owns durable workflow state; the worker owns event delivery and external CLI dispatch; the browser is an operator surface rather than an authority source.

## Components

### Domain

`imperium-domain` defines docket states, operating modes, institutional roles, transitions, persistence entities, and mapper contracts. `DocketStateMachine` keeps transition rules explicit and rejects terminal-state mutation.

### API

`imperium-api` exposes docket, senate, decision, execution, audit, and archive resources. State changes are persisted with docket events and outbox records so downstream work can be recovered independently of the initiating request.

### Worker

`imperium-worker` scans pending outbox records, routes domain events, dispatches execution tasks through an OpenClaw CLI adapter, and returns progress or results to authenticated internal endpoints. A recovery job returns stale dispatches to a retryable state before asking the dispatcher to reconsider them.

### Frontend

The React application provides docket lists and detail views, an audit queue, and archive views. It consumes the API through a configurable base URL and contains no workflow authority of its own.

## Delivery paths

Two event paths exist:

1. The Docker profile sends outbox events through RocketMQ.
2. The local profile can route the same envelope directly inside the worker for focused development.

The local path reduces infrastructure requirements but does not reproduce broker durability or redelivery behavior.

## Failure boundaries

- Invalid state transitions are rejected by the domain state machine.
- Outbox publication failures increment retry state and remain eligible until the configured retry limit.
- Stale dispatching or failed tasks can be returned to `PENDING` by the recovery job.
- CLI startup validation checks the configured executable and callback target before worker operation.
- Internal callbacks and orchestration requests require separate configured secrets.

## Evidence and gaps

Unit tests cover domain transitions, audit/archive behavior, execution-task behavior, domain event consumption, and CLI dispatch decisions. Backend compilation and frontend production builds are reproducible in CI.

The repository does not yet include a hermetic service-level integration test. MySQL, RocketMQ, Docker networking, and a real OpenClaw installation remain integration dependencies.
