# PAY-1001 Create Payment API

## Type

Story

## Status

Ready for Review

## Business Requirement

As a customer-facing system, I need to create a payment request so that downstream payment processing can begin.

## Acceptance Criteria

- Given a valid customer ID, amount, and currency, the API returns HTTP 201.
- The response includes a generated payment ID.
- The response status is `CREATED`.
- Invalid amounts are rejected with HTTP 400.
- Unit or integration tests cover success and validation failure paths.

## Technical Tasks

- Create Spring Boot payment service module.
- Add `POST /api/v1/payments`.
- Add request validation.
- Add tests with MockMvc.
- Add CI pipeline.

## Definition of Done

- Tests pass locally with `mvn verify`.
- CI workflow runs `mvn verify`.
- API design document is updated.
- Structured validation errors are returned.
- Domain exceptions include stable error codes.
- Error logs include trace ID, path, error code, and support-readable message.

