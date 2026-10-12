# Senate-Agent-Orchestration: proposed framework

Status: framework proposal for Senate-Agent-Orchestration, built on the incomplete workflow MVP historically named Imperium. The accepted project identity does not imply a completed multiagent framework; historical tests and internal module names remain intact.

## Research question

Can an explicit governance protocol make multiagent decisions inspectable without confusing discussion, delegated execution, and human authority? Roman Senate terminology is an organizational metaphor, not a claim about historical accuracy or an agent's real-world authority.

## Proposed roles

| Role | Responsibility | Boundary |
| --- | --- | --- |
| Convenor | Open a bounded docket and record its scope | Cannot expand authority from task text |
| Senators | Produce independently attributable proposals and evidence | Deliberation alone cannot authorize side effects |
| Reviewer | Challenge assumptions and raise reasoned objections | Objections remain records, not hidden state overrides |
| Human decision maker | Accept scope and authorize actions requiring approval | Credentials and external authority stay with the operator |
| Executor | Perform the accepted bounded mandate | Must report failure, conflicts, and evidence |
| Auditor | Compare results against the mandate and archive provenance | Cannot turn an unverified claim into success |

The existing domain state machine, API, worker, and operator console implement portions of a governance lifecycle. The API owns durable workflow state; the worker dispatches tasks; the browser displays and requests actions. Proposed roles above must be mapped to actual authorization checks before they are considered implemented.

## Intended protocol

Docket scope → independent deliberation → explicit objections → recorded human decision → bounded delegation → execution evidence → independent audit → archive.

## Acceptance gates before a framework claim

1. Map each role to enforceable permissions and test unauthorized transitions.
2. Exercise API, MySQL, outbox delivery, worker and real CLI callbacks in an isolated service-level test.
3. Prove duplicate delivery, stale dispatch, timeout, cancellation and recovery behavior with durable evidence.
4. Verify callback authentication, credential isolation, and redacted logs across the topology.
5. Specify how budget, agent isolation and conflicting proposals affect decisions; test those policies before exposing them as features.
6. Retain every existing regression test and record exact environment and commit for new results.

## Current boundaries

There is no demonstrated general-purpose multiagent consensus engine, hermetic full-stack recovery test, production deployment, or benchmark result. Local dispatch cannot establish broker delivery guarantees. Java package and module names retain Imperium as implementation provenance; any runtime-name migration requires compatibility evidence. Historical product and technical documents remain as provenance and may describe planned functionality rather than accepted runtime evidence.
