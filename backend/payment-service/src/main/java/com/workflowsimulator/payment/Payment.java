package com.workflowsimulator.payment;

import java.math.BigDecimal;
import java.time.Instant;

record Payment(
        String paymentId,
        String customerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt) {
}
