package com.workflowsimulator.payment;

import java.util.Optional;

interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);
}
