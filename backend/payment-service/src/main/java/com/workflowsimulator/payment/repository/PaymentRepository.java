package com.workflowsimulator.payment.repository;

import com.workflowsimulator.payment.domain.Payment;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    // Contract: implementations must use keyed lookup and avoid scanning the full payment store.
    Optional<Payment> findById(String paymentId);
}
