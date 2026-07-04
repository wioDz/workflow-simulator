package com.workflowsimulator.payment.repository;

import com.workflowsimulator.payment.domain.Payment;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);
}
