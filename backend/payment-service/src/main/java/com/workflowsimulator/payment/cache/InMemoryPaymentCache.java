package com.workflowsimulator.payment.cache;

import com.workflowsimulator.payment.domain.Payment;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPaymentCache implements PaymentCache {

    // Redis can replace this map later while preserving the PaymentCache contract.
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    @Override
    public Payment put(Payment payment) {
        payments.put(payment.paymentId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }
}
