package com.workflowsimulator.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.workflowsimulator.payment.api.CreatePaymentRequest;
import com.workflowsimulator.payment.cache.PaymentCache;
import com.workflowsimulator.payment.domain.Payment;
import com.workflowsimulator.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {

    @Test
    void getPaymentUsesCacheAfterFirstRepositoryLookup() {
        CountingPaymentRepository repository = new CountingPaymentRepository();
        MapPaymentCache cache = new MapPaymentCache();
        PaymentService service = new PaymentService(cache, repository);

        Payment created = service.createPayment(new CreatePaymentRequest(
                "CUS-3001",
                new BigDecimal("29.99"),
                "USD"));

        cache.clear();

        Payment firstLookup = service.getPayment(created.paymentId());
        Payment secondLookup = service.getPayment(created.paymentId());

        assertThat(firstLookup.paymentId()).isEqualTo(created.paymentId());
        assertThat(secondLookup.paymentId()).isEqualTo(created.paymentId());
        assertThat(repository.findByIdCalls()).isEqualTo(1);
    }

    private static class MapPaymentCache implements PaymentCache {

        private final Map<String, Payment> payments = new HashMap<>();

        @Override
        public Payment put(Payment payment) {
            payments.put(payment.paymentId(), payment);
            return payment;
        }

        @Override
        public Optional<Payment> findById(String paymentId) {
            return Optional.ofNullable(payments.get(paymentId));
        }

        void clear() {
            payments.clear();
        }
    }

    private static class CountingPaymentRepository implements PaymentRepository {

        private final Map<String, Payment> payments = new HashMap<>();
        private int findByIdCalls;

        @Override
        public Payment save(Payment payment) {
            payments.put(payment.paymentId(), payment);
            return payment;
        }

        @Override
        public Optional<Payment> findById(String paymentId) {
            findByIdCalls++;
            return Optional.ofNullable(payments.get(paymentId));
        }

        int findByIdCalls() {
            return findByIdCalls;
        }
    }
}
