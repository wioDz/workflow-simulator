package com.workflowsimulator.payment.cache;

import com.workflowsimulator.payment.domain.Payment;
import java.util.Optional;

public interface PaymentCache {

    Payment put(Payment payment);

    Optional<Payment> findById(String paymentId);
}
