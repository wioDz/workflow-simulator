package com.workflowsimulator.payment.application;

import com.workflowsimulator.payment.api.CreatePaymentRequest;
import com.workflowsimulator.payment.cache.PaymentCache;
import com.workflowsimulator.payment.domain.Payment;
import com.workflowsimulator.payment.domain.PaymentStatus;
import com.workflowsimulator.payment.error.PaymentDomainException;
import com.workflowsimulator.payment.repository.PaymentRepository;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD");

    private final PaymentCache paymentCache;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentCache paymentCache, PaymentRepository paymentRepository) {
        this.paymentCache = paymentCache;
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

        Payment savedPayment = paymentRepository.save(payment);
        paymentCache.put(savedPayment);
        return savedPayment;
    }

    public Payment getPayment(String paymentId) {
        return paymentCache.findById(paymentId)
                .orElseGet(() -> loadPaymentFromRepository(paymentId));
    }

    private Payment loadPaymentFromRepository(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentDomainException(
                        "PAYMENT_NOT_FOUND",
                        "Payment was not found: " + paymentId,
                        HttpStatus.NOT_FOUND));
        paymentCache.put(payment);
        return payment;
    }
}
