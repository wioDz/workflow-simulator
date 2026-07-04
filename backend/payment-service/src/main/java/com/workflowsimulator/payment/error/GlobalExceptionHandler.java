package com.workflowsimulator.payment.error;

import com.workflowsimulator.payment.domain.PaymentDomainException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Keep validation failures searchable by support teams through a stable log marker and trace ID.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .collect(Collectors.toList());
        String traceId = traceId(request);

        LOGGER.warn(
                "PAYMENT_VALIDATION_FAILED traceId={} path={} message=\"{}\" fieldErrors={}",
                traceId,
                request.getRequestURI(),
                "Validation failed for request payload",
                fieldErrors);

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                traceId,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "PAYMENT_VALIDATION_FAILED",
                "Validation failed for request payload",
                fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    // Expected business exceptions are logged without stack traces but still carry a stable error code.
    @ExceptionHandler(PaymentDomainException.class)
    ResponseEntity<ErrorResponse> handleDomainException(
            PaymentDomainException ex,
            HttpServletRequest request) {
        String traceId = traceId(request);

        LOGGER.warn(
                "PAYMENT_DOMAIN_EXCEPTION traceId={} path={} errorCode={} message=\"{}\"",
                traceId,
                request.getRequestURI(),
                ex.errorCode(),
                ex.getMessage());

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                traceId,
                request.getRequestURI(),
                ex.httpStatus().value(),
                ex.httpStatus().getReasonPhrase(),
                ex.errorCode(),
                ex.getMessage(),
                Collections.emptyList());

        return ResponseEntity.status(ex.httpStatus()).body(body);
    }

    private ErrorResponse.FieldError toFieldError(FieldError error) {
        String rejected = error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null";
        return new ErrorResponse.FieldError(error.getField(), rejected, error.getDefaultMessage());
    }

    private String traceId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            return "not-provided";
        }
        return correlationId;
    }
}
