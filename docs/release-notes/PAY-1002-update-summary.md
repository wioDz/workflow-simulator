# PAY-1002 Update Summary

## Story

`PAY-1002 Query Payment API`

PAY-1002 adds the ability to retrieve a payment after it is created. It also
refactors the payment service into clearer enterprise-style packages:
controller/API, service/application, cache, repository, domain model, and error
handling.

## Package Layout

```text
com.workflowsimulator.payment
├── api
│   ├── PaymentController.java
│   ├── CreatePaymentRequest.java
│   └── PaymentResponse.java
├── application
│   └── PaymentService.java
├── cache
│   ├── PaymentCache.java
│   └── InMemoryPaymentCache.java
├── domain
│   ├── Payment.java
│   ├── PaymentStatus.java
│   └── PaymentDomainException.java
├── repository
│   ├── PaymentRepository.java
│   └── InMemoryPaymentRepository.java
└── error
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
```

## Code Updates

| File | Update | Purpose |
| --- | --- | --- |
| `api/PaymentController.java` | Added `GET /api/v1/payments/{paymentId}` and moved business logic out of controller | Keep HTTP layer focused on request and response handling |
| `api/CreatePaymentRequest.java` | Extracted create-payment request DTO | Keep request contract in its own file |
| `api/PaymentResponse.java` | Extracted payment response DTO and mapper from domain model | Keep API response contract stable |
| `domain/Payment.java` | Added payment domain record | Represent internal payment state |
| `domain/PaymentStatus.java` | Added `CREATED` payment status enum | Avoid raw status strings in domain logic |
| `application/PaymentService.java` | Added create and query business operations | Own payment use cases and domain validation |
| `cache/PaymentCache.java` | Added keyed cache contract | Prepare Redis-backed quick reads and writes |
| `cache/InMemoryPaymentCache.java` | Added Sprint 1 cache implementation | Verify cache-aside behavior without external Redis dependency |
| `repository/PaymentRepository.java` | Added repository abstraction | Prepare for future PostgreSQL/JPA persistence |
| `repository/InMemoryPaymentRepository.java` | Added thread-safe in-memory repository | Support Sprint 1 query workflow without a database |
| `error/GlobalExceptionHandler.java` | Reused domain exception handling for `PAYMENT_NOT_FOUND` | Keep not-found errors structured and logged |
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

## Query Performance Design

PAY-1002 avoids checking the entire payment store:

- The service checks `PaymentCache` before repository storage.
- The repository contract uses `findById(paymentId)`.
- The in-memory cache uses `ConcurrentHashMap#get`.
- The in-memory implementation uses `ConcurrentHashMap#get`.
- The service does not call `findAll()` or filter all payments in memory.
- Future Redis work can replace `InMemoryPaymentCache` with a Redis-backed implementation.
- Future PostgreSQL/JPA work must use an indexed `payment_id` lookup.
- This protects runtime, loading time, and database pressure as records grow.

Cache-aside flow:

```text
Read:  cache -> repository on miss -> cache warm-up
Write: repository save -> cache put
```

## Verification

Command:

```bash
mvn verify
```

Result:

```text
BUILD SUCCESS
Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
```
