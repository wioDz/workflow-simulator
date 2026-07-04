package com.workflowsimulator.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Payment(
        String paymentId,
        String customerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt) {

    public Payment cancel() {
        return new Payment(
                paymentId,
                customerId,
                amount,
                currency,
                PaymentStatus.CANCELLED,
                createdAt);
    }
}
