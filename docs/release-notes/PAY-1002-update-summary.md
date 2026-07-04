# PAY-1002 Update Summary

## Story

`PAY-1002 Query Payment API`

PAY-1002 adds the ability to retrieve a payment after it is created. It also
refactors the payment service into clearer enterprise-style responsibilities:
controller, service, repository, and domain model.

## Code Updates

| File | Update | Purpose |
| --- | --- | --- |
| `PaymentController.java` | Added `GET /api/v1/payments/{paymentId}` and moved business logic out of controller | Keep HTTP layer focused on request and response handling |
| `CreatePaymentRequest.java` | Extracted create-payment request DTO | Keep request contract in its own file |
| `PaymentResponse.java` | Extracted payment response DTO and mapper from domain model | Keep API response contract stable |
| `Payment.java` | Added payment domain record | Represent internal payment state |
| `PaymentStatus.java` | Added `CREATED` payment status enum | Avoid raw status strings in domain logic |
| `PaymentService.java` | Added create and query business operations | Own payment use cases and domain validation |
| `PaymentRepository.java` | Added repository abstraction | Prepare for future PostgreSQL/JPA persistence |
| `InMemoryPaymentRepository.java` | Added thread-safe in-memory repository | Support Sprint 1 query workflow without a database |
| `GlobalExceptionHandler.java` | Reused domain exception handling for `PAYMENT_NOT_FOUND` | Keep not-found errors structured and logged |
| `PaymentControllerTest.java` | Added query success and not-found tests | Verify API behavior and log message contract |

## API Added

### Query Payment

```http
GET /api/v1/payments/{paymentId}
```

Success response:

```json
{
  "paymentId": "generated-id",
  "customerId": "CUS-2001",
  "amount": 19.99,
  "currency": "USD",
  "status": "CREATED",
  "createdAt": "2026-07-04T00:00:00Z"
}
```

Not-found response:

```json
{
  "traceId": "test-trace-2002",
  "path": "/api/v1/payments/PAY-DOES-NOT-EXIST",
  "status": 404,
  "error": "Not Found",
  "errorCode": "PAYMENT_NOT_FOUND",
  "message": "Payment was not found: PAY-DOES-NOT-EXIST",
  "fieldErrors": []
}
```

## Special Exception Logging

Unknown payment IDs log a searchable domain exception:

```text
PAYMENT_DOMAIN_EXCEPTION traceId=test-trace-2002 path=/api/v1/payments/PAY-DOES-NOT-EXIST errorCode=PAYMENT_NOT_FOUND message="Payment was not found: PAY-DOES-NOT-EXIST"
```

## Verification

Command:

```bash
mvn verify
```

Result:

```text
BUILD SUCCESS
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
```

