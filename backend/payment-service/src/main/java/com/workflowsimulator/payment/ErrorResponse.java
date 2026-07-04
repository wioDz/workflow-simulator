package com.workflowsimulator.payment;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        String traceId,
        String path,
        int status,
        String error,
        String errorCode,
        String message,
        List<FieldError> fieldErrors) {

    public record FieldError(String field, String rejectedValue, String message) {
    }
}
