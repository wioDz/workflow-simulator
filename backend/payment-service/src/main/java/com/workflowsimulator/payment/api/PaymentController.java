package com.workflowsimulator.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.workflowsimulator.payment.application.PaymentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

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
    public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return PaymentResponse.from(paymentService.createPayment(request));
    }

    @GetMapping("/{paymentId}")
    @Operation(
            summary = "Query a payment",
            description = "Returns a previously created payment by payment ID.")
    @ApiResponse(
            responseCode = "200",
            description = "Payment found",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Payment not found",
            content = @Content)
    public PaymentResponse getPayment(@PathVariable String paymentId) {
        return PaymentResponse.from(paymentService.getPayment(paymentId));
    }

    @DeleteMapping("/{paymentId}")
    @Operation(
            summary = "Cancel a payment",
            description = "Cancels an existing payment when it is still eligible for cancellation.")
    @ApiResponse(
            responseCode = "200",
            description = "Payment cancelled",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Payment not found",
            content = @Content)
    @ApiResponse(
            responseCode = "409",
            description = "Payment cannot be cancelled",
            content = @Content)
    public PaymentResponse cancelPayment(@PathVariable String paymentId) {
        return PaymentResponse.from(paymentService.cancelPayment(paymentId));
    }
}

