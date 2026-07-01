package com.workflowsimulator.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return new PaymentResponse(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.amount(),
                request.currency(),
                "CREATED",
                Instant.now());
    }

    record CreatePaymentRequest(
            @NotBlank String customerId,
            @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
            @NotBlank String currency) {
    }

    record PaymentResponse(
            String paymentId,
            String customerId,
            BigDecimal amount,
            String currency,
            String status,
            Instant createdAt) {
    }
}

