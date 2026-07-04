# PAY-1001 Payment Service Design

## 1. Context

The payment service owns payment creation and payment status tracking.
Sprint 1 starts with a synchronous REST API so contributors can practice
requirements, technical design, validation, testing, pull requests, CI, and
production-style error handling before adding persistence and messaging.

PAY-1001 delivers the first production-shaped API in the project:

- `POST /api/v1/payments`
- Request validation
- Domain validation
- Structured error response
- Correlation-aware exception logging
- Automated tests and CI verification

PAY-1002 adds payment lookup and introduces controller/service/repository/domain
separation while persistence is still in-memory for Sprint 1.

## 2. Goals

- Create a payment request for a customer.
- Return a generated payment ID and initial `CREATED` status.
- Reject invalid payloads with clear field-level error messages.
- Reject unsupported business inputs with a dedicated domain error code.
- Write enough structured log data for support engineers to locate failures.
- Keep the implementation simple until persistence is introduced in PAY-1002+.

## 3. Non-Goals

- No database persistence in PAY-1001.
- No external payment processor integration.
- No Kafka event publication.
- No authentication or authorization.
- No idempotency key support yet.

## 4. API Contract

### Create Payment

`POST /api/v1/payments`

### Request

```json
{
  "customerId": "CUS-1001",
  "amount": 42.50,
  "currency": "USD"
}
```

### Success Response

HTTP `201 Created`

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

### Query Payment

`GET /api/v1/payments/{paymentId}`

Success response:

HTTP `200 OK`

```json
{
  "paymentId": "generated-id",
  "customerId": "CUS-1001",
  "amount": 42.50,
  "currency": "USD",
  "status": "CREATED",
  "createdAt": "2026-07-04T00:00:00Z"
}
```

## 5. Validation Rules

| Field | Rule | Failure Type |
| --- | --- | --- |
| `customerId` | Required and not blank | Bean validation |
| `amount` | Required and greater than or equal to `0.01` | Bean validation |
| `currency` | Required and not blank | Bean validation |
| `currency` | Must be supported by payment service | Domain validation |

PAY-1001 supports only `USD`. Unsupported currencies are rejected with
`PAYMENT_UNSUPPORTED_CURRENCY`.

## 6. Error Response Contract

All client-facing errors use one response shape:

```json
{
  "timestamp": "2026-07-04T00:00:00Z",
  "traceId": "client-or-gateway-correlation-id",
  "path": "/api/v1/payments",
  "status": 400,
  "error": "Bad Request",
  "errorCode": "PAYMENT_VALIDATION_FAILED",
  "message": "Validation failed for request payload",
  "fieldErrors": [
    {
      "field": "amount",
      "rejectedValue": "0",
      "message": "must be greater than or equal to 0.01"
    }
  ]
}
```

### Error Codes

| Code | HTTP Status | Meaning | Owner Action |
| --- | --- | --- | --- |
| `PAYMENT_VALIDATION_FAILED` | 400 | Request schema or field validation failed | Fix request payload |
| `PAYMENT_UNSUPPORTED_CURRENCY` | 400 | Currency is not currently supported | Use supported currency |
| `PAYMENT_NOT_FOUND` | 404 | Payment ID was not found | Check payment ID or create payment first |

## 7. Exception and Logging Design

`GlobalExceptionHandler` is the API boundary for exception translation.

| Exception | Response Code | Log Level | Log Marker |
| --- | --- | --- | --- |
| `MethodArgumentNotValidException` | 400 | WARN | `PAYMENT_VALIDATION_FAILED` |
| `PaymentDomainException` | 400 | WARN | `PAYMENT_DOMAIN_EXCEPTION` |
| `PaymentDomainException` | 404 | WARN | `PAYMENT_DOMAIN_EXCEPTION` |

Log entries include:

- `traceId`
- request path
- stable `errorCode`
- support-readable message
- validation field errors when applicable

The API reads `X-Correlation-Id` into `traceId`. If callers do not send the
header, the service returns and logs `not-provided`. This makes the behavior
explicit during local development while leaving room for an API gateway or
filter to generate correlation IDs later.

Example validation log:

```text
PAYMENT_VALIDATION_FAILED traceId=test-trace-1001 path=/api/v1/payments
message="Validation failed for request payload"
```

Example domain log:

```text
PAYMENT_DOMAIN_EXCEPTION traceId=test-trace-1002 path=/api/v1/payments
errorCode=PAYMENT_UNSUPPORTED_CURRENCY
message="Currency is not supported for payment creation: EUR"
```

## 8. Component Design

Package structure:

```text
com.workflowsimulator.payment
├── api
├── application
├── domain
├── repository
└── error
```

| Component | Responsibility |
| --- | --- |
| `api.PaymentController` | Owns REST API request and response mapping |
| `api.CreatePaymentRequest` | Defines create-payment request contract |
| `api.PaymentResponse` | Defines payment response contract |
| `application.PaymentService` | Owns payment creation, lookup, and business rules |
| `repository.PaymentRepository` | Defines persistence contract for payment storage |
| `repository.InMemoryPaymentRepository` | Stores payments in memory for Sprint 1 workflow practice |
| `domain.Payment` | Represents payment domain state |
| `domain.PaymentStatus` | Defines supported payment states |
| `domain.PaymentDomainException` | Represents expected payment-domain failures |
| `error.GlobalExceptionHandler` | Converts framework and domain exceptions to API errors |
| `error.ErrorResponse` | Stable client-facing error schema |
| `PaymentControllerTest` | Verifies success, validation, domain errors, and logs |

## 9. Test Strategy

Automated tests cover:

- Successful payment creation.
- Required fields.
- Minimum amount.
- Structured validation response.
- Validation log marker and message.
- Unsupported currency domain exception.
- Domain exception log marker, error code, and message.
- Query existing payment.
- Query unknown payment and verify `PAYMENT_NOT_FOUND` response and log message.

CI runs `mvn verify`, which includes unit/integration tests, Checkstyle, and
JaCoCo verification.

## 10. Operational Checks

Support engineers can troubleshoot PAY-1001 failures by:

1. Asking the caller for `X-Correlation-Id`.
2. Searching application logs for `traceId=<value>`.
3. Checking `errorCode` to identify validation vs domain failure.
4. Comparing the API response `message` with the logged exception message.

## 11. Next Iterations

- Add PostgreSQL persistence.
- Add idempotency key support.
- Publish payment events to Kafka.
- Add audit logging.
- Add automatic correlation ID generation.
- Add Problem Details compatibility if the API standard changes.

