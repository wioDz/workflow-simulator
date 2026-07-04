package com.workflowsimulator.payment.api;

import io.swagger.v3.oas.annotations.media.Schema;
import com.workflowsimulator.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        @Schema(description = "Generated unique payment ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                String paymentId,
        @Schema(description = "Customer identifier", example = "CUS-1001") String customerId,
        @Schema(description = "Payment amount", example = "42.50") BigDecimal amount,
        @Schema(description = "Currency code", example = "USD") String currency,
        @Schema(description = "Payment status", example = "CREATED") String status,
        @Schema(description = "Creation timestamp", example = "2026-07-04T10:30:00Z") Instant createdAt) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.paymentId(),
                payment.customerId(),
                payment.amount(),
                payment.currency(),
                payment.status().name(),
                payment.createdAt());
    }
}
