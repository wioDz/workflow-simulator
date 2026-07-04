package com.workflowsimulator.payment;

import org.springframework.http.HttpStatus;

// Represents expected payment-domain failures that should be visible in API responses and logs.
class PaymentDomainException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    PaymentDomainException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    String errorCode() {
        return errorCode;
    }

    HttpStatus httpStatus() {
        return httpStatus;
    }
}
