package com.workflowsimulator.payment.application;

import com.workflowsimulator.payment.api.CreatePaymentRequest;
import com.workflowsimulator.payment.domain.Payment;
import com.workflowsimulator.payment.domain.PaymentDomainException;
import com.workflowsimulator.payment.domain.PaymentStatus;
import com.workflowsimulator.payment.repository.PaymentRepository;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD");

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(CreatePaymentRequest request) {
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

    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentDomainException(
                        "PAYMENT_NOT_FOUND",
                        "Payment was not found: " + paymentId,
                        HttpStatus.NOT_FOUND));
    }
}
