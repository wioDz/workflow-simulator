package com.workflowsimulator.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

record CreatePaymentRequest(
        @NotBlank @Schema(description = "Customer identifier", example = "CUS-1001") String customerId,
        @NotNull @DecimalMin(value = "0.01") @Schema(description = "Payment amount", example = "42.50")
                BigDecimal amount,
        @NotBlank @Schema(description = "Currency code (ISO-4217)", example = "USD") String currency) {
}
