package com.workflowsimulator.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Payment management APIs")
class PaymentController {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD");

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new payment",
            description = "Creates a payment request for a customer with specified amount and currency.")
    @ApiResponse(
            responseCode = "201",
            description = "Payment created successfully",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content)
    PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        // PAY-1001 keeps currency support explicit until configuration-backed rules are introduced.
        if (!SUPPORTED_CURRENCIES.contains(request.currency())) {
            throw new PaymentDomainException(
                    "PAYMENT_UNSUPPORTED_CURRENCY",
                    "Currency is not supported for payment creation: " + request.currency(),
                    HttpStatus.BAD_REQUEST);
        }

        return new PaymentResponse(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.amount(),
                request.currency(),
                "CREATED",
                Instant.now());
    }

    record CreatePaymentRequest(
            @NotBlank @Schema(description = "Customer identifier", example = "CUS-1001") String customerId,
            @NotNull @DecimalMin(value = "0.01") @Schema(description = "Payment amount", example = "42.50")
                    BigDecimal amount,
            @NotBlank @Schema(description = "Currency code (ISO-4217)", example = "USD") String currency) {
    }

    record PaymentResponse(
            @Schema(description = "Generated unique payment ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
                    String paymentId,
            @Schema(description = "Customer identifier", example = "CUS-1001") String customerId,
            @Schema(description = "Payment amount", example = "42.50") BigDecimal amount,
            @Schema(description = "Currency code", example = "USD") String currency,
            @Schema(description = "Payment status", example = "CREATED") String status,
            @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00Z") Instant createdAt) {
    }
}

