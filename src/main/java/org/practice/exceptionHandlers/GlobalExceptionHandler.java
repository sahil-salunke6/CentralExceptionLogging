package org.practice.exceptionHandlers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Object> buildError(HttpStatus status, String message, List<String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("details", details);
        logger.error("[{} {}] - {} | Details: {}", status.value(), status, message, details);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        logger.warn("Validation failed: {}", errors);
        return buildError(HttpStatus.BAD_REQUEST, "Validation Failed", errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        logger.error("Entity not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, "Entity Not Found", List.of(ex.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDatabaseError(DataAccessException ex) {
        String message = Optional.of(ex.getRootCause())
                .map(Throwable::getMessage)
                .orElse("Database error occurred");

        logger.error("Database access error: {}", message, ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Database error", List.of(message));
    }

    @ExceptionHandler(MakerCheckerException.class)
    public ResponseEntity<Object> handleMakerCheckerError(MakerCheckerException ex) {
        logger.error("Maker-Checker business rule violation: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Maker-Checker Error", List.of(ex.getMessage()));
    }

// --- Swagger related exceptions ---

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("Parameter '%s' should be of type %s", ex.getName(), ex.getRequiredType().getSimpleName());
        logger.warn("Type mismatch: {}", msg);
        return buildError(HttpStatus.BAD_REQUEST, "Type Mismatch", List.of(msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleUnreadable(HttpMessageNotReadableException ex) {
        String msg = "Malformed JSON request or invalid input format";
        logger.warn("Unreadable message: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", List.of(msg));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = String.format("Missing required parameter: %s", ex.getParameterName());
        logger.warn("Missing parameter: {}", msg);
        return buildError(HttpStatus.BAD_REQUEST, "Missing Parameter", List.of(msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        logger.warn("Constraint violations: {}", errors);
        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", errors);
    }

// --------


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex) {
        logger.error("Uncaught exception: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", List.of(ex.getMessage()));
    }
}
