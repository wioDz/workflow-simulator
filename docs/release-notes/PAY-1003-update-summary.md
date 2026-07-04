# PAY-1003 Update Summary

## Story

`PAY-1003 Cancel Payment API`

PAY-1003 adds cancellation support and introduces the first explicit payment
state transition.

## Code Updates

| File | Update | Purpose |
| --- | --- | --- |
| `api/PaymentController.java` | Added `DELETE /api/v1/payments/{paymentId}` | Expose payment cancellation API |
| `application/PaymentService.java` | Added `cancelPayment` business operation | Validate state and update repository/cache |
| `domain/Payment.java` | Added `cancel()` transition helper | Keep status transition near domain state |
| `domain/PaymentStatus.java` | Added `CANCELLED` | Represent cancelled payment lifecycle state |
| `PaymentControllerTest.java` | Added cancellation tests | Verify success, query-after-cancel, not-found, and already-cancelled errors |
| `docs/jira/PAY-1003-cancel-payment.md` | Added Jira-style story | Track requirements and Definition of Done |
| `docs/architecture/payment-service-design.md` | Added cancel API and performance/cache notes | Keep design aligned with implementation |

## API Added

```http
DELETE /api/v1/payments/{paymentId}
```

Success response:

```json
{
  "paymentId": "generated-id",
  "customerId": "CUS-3001",
  "amount": 35.00,
  "currency": "USD",
  "status": "CANCELLED",
  "createdAt": "2026-07-04T00:00:00Z"
}
```

## Error Codes

| Error Code | HTTP Status | Meaning |
| --- | --- | --- |
| `PAYMENT_NOT_FOUND` | 404 | Payment ID does not exist |
| `PAYMENT_ALREADY_CANCELLED` | 409 | Payment was already cancelled |

## Cache And Runtime Design

Cancellation uses keyed lookup and write-through update:

```text
get payment by ID -> validate status -> save cancelled payment -> cache cancelled payment
```

The service does not scan all payments. Only the target payment ID is read and
updated.

## Verification

Command:

```bash
mvn verify
```

Result:

```text
BUILD SUCCESS
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
```
