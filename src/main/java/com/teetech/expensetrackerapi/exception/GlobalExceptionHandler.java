package com.teetech.expensetrackerapi.exception;

import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    // Exceptions that should return 404 instead of 400
    private static final Set<String> NOT_FOUND_CODES = Set.of(
            "USER_NOT_FOUND",
            "USER_PROFILE_NOT_FOUND",
            "EXPENSE_NOT_FOUND",
            "CATEGORY_NOT_FOUND",
            "BUDGET_NOT_FOUND"
    );

    private static final Set<String> FORBIDDEN_CODES = Set.of(
            "SYSTEM_CATEGORY_IMMUTABLE"
    );

    // Handles business/domain validation errors (from validator layer)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(ValidationException ex,
                                                       HttpServletRequest request){

        log.warn("Validation exception: {} error(s): {}", ex.getErrors().size(), ex.getErrors());

        List<String> errorList = ex.getErrors();
        Map<String, String> details = new HashMap<>();

        for (int i = 0; i < errorList.size(); i++) {
            details.put(String.valueOf(i + 1), errorList.get(i));
        }

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "The data provided is invalid",
                request.getRequestURI(),
                LocalDateTime.now(),
                details
        );

        return ResponseEntity.badRequest().body(responseDTO);
    }

    // Handles Jakarta @Valid annotation violations (DTO-level constraints)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgValidationException(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request){

        log.warn("DTO validation failed: {} field error(s)", ex.getBindingResult().getFieldErrorCount());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),
                error.getDefaultMessage()));

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "The data provided is invalid",
                request.getRequestURI(),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(responseDTO);
    }

    // Handles all custom service exceptions (not found, duplicates, etc.)
    @ExceptionHandler(ExpenseServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpenseServiceException(
            ExpenseServiceException ex, HttpServletRequest request){

        log.warn("Service exception: code={}, message={}", ex.getErrorCode(), ex.getMessage());

        Map<String, String> details = new HashMap<>();
        details.put(ex.getErrorCode(), ex.getMessage());

        HttpStatus status;
        if (NOT_FOUND_CODES.contains(ex.getErrorCode())) {
            status = HttpStatus.NOT_FOUND;
        } else if (FORBIDDEN_CODES.contains(ex.getErrorCode())) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }


        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                status.getReasonPhrase(),
                status.value(),
                ex.getErrorLabel(),
                request.getRequestURI(),
                LocalDateTime.now(),
                details
        );

        return ResponseEntity.status(status).body(responseDTO);
    }

    // Safety net — catches anything not handled above
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }

    // Handle illegal argument exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentExceptioin(
            IllegalArgumentException ex, HttpServletRequest request){

        log.error("Invalid argument error: {}", ex.getMessage());

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                "Invalid Argument",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.badRequest().body(responseDTO);
    }

}
