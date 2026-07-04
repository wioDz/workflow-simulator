# PAY-1002 Query Payment API

## Type

Story

## Status

Ready for Review

## Business Requirement

As a customer-facing system, I need to query a payment by payment ID so that
callers can check the payment status after creation.

## Acceptance Criteria

- Given an existing payment ID, the API returns HTTP 200.
- The response includes payment ID, customer ID, amount, currency, status, and creation timestamp.
- Given an unknown payment ID, the API returns HTTP 404.
- The not-found response includes `PAYMENT_NOT_FOUND`.
- The not-found exception is logged with trace ID, path, error code, and message.
- The lookup must use payment ID as a key and must not scan the entire payment store.
- Tests cover success and not-found paths.

## Technical Tasks

- Add `GET /api/v1/payments/{paymentId}`.
- Move create-payment business logic into `PaymentService`.
- Add `PaymentRepository` abstraction.
- Add in-memory repository implementation for Sprint 1.
- Add payment domain model and status enum.
- Add query-payment tests and logging assertions.
- Document the no-full-scan repository contract.

## Definition of Done

- Tests pass locally with `mvn verify`.
- Query API is documented.
- Controller, service, repository, and domain responsibilities are separated.
- Not-found errors have stable response and log contracts.
- Query design avoids full-store scans to protect runtime and loading time.
