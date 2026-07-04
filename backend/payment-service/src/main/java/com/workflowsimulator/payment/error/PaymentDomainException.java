package com.workflowsimulator.payment.error;

import org.springframework.http.HttpStatus;

// Represents expected payment-domain failures that should be visible in API responses and logs.
public class PaymentDomainException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public PaymentDomainException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String errorCode() {
        return errorCode;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
