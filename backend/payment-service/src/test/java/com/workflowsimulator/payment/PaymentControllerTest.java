package com.workflowsimulator.payment;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
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
}

