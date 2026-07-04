# PAY-1003 Cancel Payment API

## Type

Story

## Status

Ready for Review

## Business Requirement

As a customer-facing system, I need to cancel a payment before it is processed
so that invalid or unwanted payment requests can be stopped.

## Acceptance Criteria

- Given an existing `CREATED` payment ID, the API returns HTTP 200.
- The response status is `CANCELLED`.
- Querying the same payment after cancellation returns `CANCELLED`.
- Given an unknown payment ID, the API returns HTTP 404 with `PAYMENT_NOT_FOUND`.
- Given an already cancelled payment, the API returns HTTP 409 with `PAYMENT_ALREADY_CANCELLED`.
- Cancellation updates repository storage and cache so subsequent reads are consistent.
- Domain errors are logged with trace ID, path, error code, and message.

## Technical Tasks

- Add `DELETE /api/v1/payments/{paymentId}`.
- Add `CANCELLED` payment status.
- Add domain transition from `CREATED` to `CANCELLED`.
- Update service to persist and cache cancelled payment state.
- Add controller tests for success, post-cancel query, not found, and already cancelled.
- Update design document and release notes.

## Definition of Done

- Tests pass locally with `mvn verify`.
- API contract is documented.
- Cancel operation does not scan the full payment store.
- Cache and repository stay consistent after cancellation.
- Error response and logs are stable for support troubleshooting.

