package com.workflowsimulator.payment;

import org.springframework.http.HttpStatus;

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
