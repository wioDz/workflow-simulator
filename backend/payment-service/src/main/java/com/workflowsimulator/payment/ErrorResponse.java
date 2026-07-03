package com.workflowsimulator.payment;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<FieldError> fieldErrors) {

    public record FieldError(String field, String rejectedValue, String message) {
    }
}
