package org.practice.exceptionHandlers;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    public HttpServletRequest httpServletRequest;

    /**
     * generate a correlationId for a unique custom error response
     *
     * @return unique id
     */
    private String getCorrelationId() {
        String guId = httpServletRequest.getHeader("correlationId");
        String traceId = MDC.get("traceId");

        if (StringUtils.isNotBlank(guId)) {
            return guId;
        } else {
            if (StringUtils.isNotEmpty(traceId)) {
                return traceId;
            }
            return UUID.randomUUID().toString();
        }
    }

    private ResponseEntity<Object> buildError(HttpStatus status, String message, List<String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("requestId", getCorrelationId());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("details", details);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[{}] | {}", status, errors);
        return buildError(status, "Validation Failed", errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        log.error("[{}] | {}", status, ex.getMessage());
        return buildError(status, "Entity Not Found", List.of(ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDatabaseError(DataAccessException ex) {
        String message = Optional.of(ex.getRootCause())
                .map(Throwable::getMessage)
                .orElse("Database error occurred");

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("[{}] | {}", status, message, ex);
        return buildError(status, "Database error", List.of(message));
    }

    @ExceptionHandler(MakerCheckerException.class)
    public ResponseEntity<Object> handleMakerCheckerError(MakerCheckerException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[{}] | {}", status, ex.getMessage());
        return buildError(status, "Maker-Checker Error", List.of(ex.getMessage()));
    }

// --- Swagger related exceptions ---

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("Parameter '%s' should be of type %s", ex.getName(), ex.getRequiredType().getSimpleName());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[{}] | {}", status, msg);
        return buildError(status, "Type Mismatch", List.of(msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadable(HttpMessageNotReadableException ex) {
        String msg = "Malformed JSON request or invalid input format";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[{}] | {}", status, ex.getMessage());
        return buildError(status, "Bad Request", List.of(msg));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = String.format("Missing required parameter: %s", ex.getParameterName());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[{}] | {}", status, msg);
        return buildError(HttpStatus.BAD_REQUEST, "Missing Parameter", List.of(msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[{}] | {}", status, errors);
        return buildError(status, "Validation Error", errors);
    }

// --------


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("[{}] | {}", status, ex.getMessage());
        return buildError(status, "Internal Server Error", List.of(ex.getMessage()));
    }
}