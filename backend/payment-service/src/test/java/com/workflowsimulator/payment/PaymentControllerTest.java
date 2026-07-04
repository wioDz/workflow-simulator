package com.workflowsimulator.payment;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPaymentReturnsCreatedPayment() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-1001",
                                  "amount": 42.50,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId", notNullValue()))
                .andExpect(jsonPath("$.customerId").value("CUS-1001"))
                .andExpect(jsonPath("$.amount").value(42.50))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void createPaymentRejectsInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-1001",
                                  "amount": 0,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentRejectsNullAmount() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-1001",
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentRejectsBlankCustomerId() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "",
                                  "amount": 42.50,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentRejectsNullCustomerId() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 42.50,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentRejectsBlankCurrency() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-1001",
                                  "amount": 42.50,
                                  "currency": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentRejectsNullCurrency() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-1001",
                                  "amount": 42.50
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentReturnsStructuredErrorOnValidationFailure() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .header("X-Correlation-Id", "test-trace-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "",
                                  "amount": 0,
                                  "currency": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.traceId").value("test-trace-1001"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value("PAYMENT_VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validation failed for request payload"))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[0].field").exists())
                .andExpect(jsonPath("$.fieldErrors[0].rejectedValue").exists())
                .andExpect(jsonPath("$.fieldErrors[0].message").exists());
    }

    @Test
    void createPaymentLogsStructuredValidationError(CapturedOutput output) throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .header("X-Correlation-Id", "test-trace-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "",
                                  "amount": 0,
                                  "currency": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        org.assertj.core.api.Assertions.assertThat(output)
                .contains("PAYMENT_VALIDATION_FAILED")
                .contains("traceId=test-trace-1001")
                .contains("Validation failed for request payload");
    }

    @Test
    void createPaymentRejectsUnsupportedCurrencyAndLogsMessage(CapturedOutput output)
            throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .header("X-Correlation-Id", "test-trace-1002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-1001",
                                  "amount": 42.50,
                                  "currency": "EUR"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.traceId").value("test-trace-1002"))
                .andExpect(jsonPath("$.errorCode").value("PAYMENT_UNSUPPORTED_CURRENCY"))
                .andExpect(jsonPath("$.message")
                        .value("Currency is not supported for payment creation: EUR"));

        org.assertj.core.api.Assertions.assertThat(output)
                .contains("PAYMENT_DOMAIN_EXCEPTION")
                .contains("errorCode=PAYMENT_UNSUPPORTED_CURRENCY")
                .contains("Currency is not supported for payment creation: EUR");
    }

    @Test
    void getPaymentReturnsPreviouslyCreatedPayment() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "CUS-2001",
                                  "amount": 19.99,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String paymentId = com.jayway.jsonpath.JsonPath.read(response, "$.paymentId");

        mockMvc.perform(get("/api/v1/payments/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.customerId").value("CUS-2001"))
                .andExpect(jsonPath("$.amount").value(19.99))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void getPaymentReturnsNotFoundForUnknownPaymentAndLogsMessage(CapturedOutput output)
            throws Exception {
        mockMvc.perform(get("/api/v1/payments/{paymentId}", "PAY-DOES-NOT-EXIST")
                        .header("X-Correlation-Id", "test-trace-2002"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.traceId").value("test-trace-2002"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments/PAY-DOES-NOT-EXIST"))
                .andExpect(jsonPath("$.errorCode").value("PAYMENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Payment was not found: PAY-DOES-NOT-EXIST"));

        org.assertj.core.api.Assertions.assertThat(output)
                .contains("PAYMENT_DOMAIN_EXCEPTION")
                .contains("errorCode=PAYMENT_NOT_FOUND")
                .contains("Payment was not found: PAY-DOES-NOT-EXIST");
    }

    @Test
    void cancelPaymentReturnsCancelledPayment() throws Exception {
        String paymentId = createPayment("CUS-3001", "35.00");

        mockMvc.perform(delete("/api/v1/payments/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.customerId").value("CUS-3001"))
                .andExpect(jsonPath("$.amount").value(35.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void getPaymentReturnsCancelledStatusAfterCancel() throws Exception {
        String paymentId = createPayment("CUS-3002", "49.00");

        mockMvc.perform(delete("/api/v1/payments/{paymentId}", paymentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/payments/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelPaymentReturnsNotFoundForUnknownPaymentAndLogsMessage(CapturedOutput output)
            throws Exception {
        mockMvc.perform(delete("/api/v1/payments/{paymentId}", "PAY-CANCEL-MISSING")
                        .header("X-Correlation-Id", "test-trace-3003"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.traceId").value("test-trace-3003"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments/PAY-CANCEL-MISSING"))
                .andExpect(jsonPath("$.errorCode").value("PAYMENT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Payment was not found: PAY-CANCEL-MISSING"));

        org.assertj.core.api.Assertions.assertThat(output)
                .contains("PAYMENT_DOMAIN_EXCEPTION")
                .contains("errorCode=PAYMENT_NOT_FOUND")
                .contains("Payment was not found: PAY-CANCEL-MISSING");
    }

    @Test
    void cancelPaymentRejectsAlreadyCancelledPaymentAndLogsMessage(CapturedOutput output)
            throws Exception {
        String paymentId = createPayment("CUS-3004", "59.00");

        mockMvc.perform(delete("/api/v1/payments/{paymentId}", paymentId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/payments/{paymentId}", paymentId)
                        .header("X-Correlation-Id", "test-trace-3004"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.traceId").value("test-trace-3004"))
                .andExpect(jsonPath("$.errorCode").value("PAYMENT_ALREADY_CANCELLED"))
                .andExpect(jsonPath("$.message").value("Payment is already cancelled: " + paymentId));

        org.assertj.core.api.Assertions.assertThat(output)
                .contains("PAYMENT_DOMAIN_EXCEPTION")
                .contains("errorCode=PAYMENT_ALREADY_CANCELLED")
                .contains("Payment is already cancelled: " + paymentId);
    }

    private String createPayment(String customerId, String amount) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "amount": %s,
                                  "currency": "USD"
                                }
                                """.formatted(customerId, amount)))
                .andExpect(status().isCreated())
                .andReturn();

        return com.jayway.jsonpath.JsonPath.read(
                createResult.getResponse().getContentAsString(),
                "$.paymentId");
    }
}

