# Payment Service Design

## Context

The payment service owns payment creation and payment status tracking. Sprint 1 starts with a synchronous REST API so developers can practice requirements, validation, testing, pull requests, and CI before adding persistence and messaging.

## API

`POST /api/v1/payments`

Request:

```json
{
  "customerId": "CUS-1001",
  "amount": 42.50,
  "currency": "USD"
}
```

Response:

```json
{
  "paymentId": "generated-id",
  "customerId": "CUS-1001",
  "amount": 42.50,
  "currency": "USD",
  "status": "CREATED",
  "createdAt": "2026-07-01T00:00:00Z"
}
```

## Next Iterations

- Add PostgreSQL persistence.
- Add idempotency key support.
- Publish payment events to Kafka.
- Add audit logging.

