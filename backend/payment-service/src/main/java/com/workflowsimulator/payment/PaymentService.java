package com.workflowsimulator.payment;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
class PaymentService {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD");

    private final PaymentRepository paymentRepository;

    PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    Payment createPayment(CreatePaymentRequest request) {
        // PAY-1001 keeps currency support explicit until configuration-backed rules are introduced.
        if (!SUPPORTED_CURRENCIES.contains(request.currency())) {
            throw new PaymentDomainException(
                    "PAYMENT_UNSUPPORTED_CURRENCY",
                    "Currency is not supported for payment creation: " + request.currency(),
                    HttpStatus.BAD_REQUEST);
        }

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.amount(),
                request.currency(),
                PaymentStatus.CREATED,
                Instant.now());

        return paymentRepository.save(payment);
    }

    Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentDomainException(
                        "PAYMENT_NOT_FOUND",
                        "Payment was not found: " + paymentId,
                        HttpStatus.NOT_FOUND));
    }
}
