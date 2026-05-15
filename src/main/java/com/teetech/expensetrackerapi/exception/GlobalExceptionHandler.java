package com.teetech.expensetrackerapi.exception;

import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Set<String> NOT_FOUND_CODES = Set.of(
            "USER_NOT_FOUND",
            "USER_PROFILE_NOT_FOUND",
            "EXPENSE_NOT_FOUND",
            "CATEGORY_NOT_FOUND",
            "BUDGET_NOT_FOUND"
    );

    private static final Set<String> CONFLICT_CODES = Set.of(
            "EMAIL_ALREADY_TAKEN",
            "USER_PROFILE_FOUND",
            "CATEGORY_ALREADY_EXIST",
            "BUDGET_OVERLAP"
    );

    private static final Set<String> FORBIDDEN_CODES = Set.of(
            "SYSTEM_CATEGORY_IMMUTABLE"
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("DTO validation failed: {} field error(s)",
                ex.getBindingResult().getFieldErrorCount());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "The data provided is invalid",
                request.getRequestURI(),
                LocalDateTime.now(),
                errors
        ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            ValidationException ex, HttpServletRequest request) {

        log.warn("Validation exception: {} error(s): {}",
                ex.getErrors().size(), ex.getErrors());

        Map<String, String> details = new HashMap<>();
        List<String> errorList = ex.getErrors();
        for (int i = 0; i < errorList.size(); i++) {
            details.put(String.valueOf(i + 1), errorList.get(i));
        }

        return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "The data provided is invalid",
                request.getRequestURI(),
                LocalDateTime.now(),
                details
        ));
    }

    @ExceptionHandler(ExpenseServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpenseServiceException(
            ExpenseServiceException ex, HttpServletRequest request) {

        log.warn("Service exception: code={}, message={}",
                ex.getErrorCode(), ex.getMessage());

        Map<String, String> details = new HashMap<>();
        details.put(ex.getErrorCode(), ex.getMessage());

        HttpStatus status;
        if (NOT_FOUND_CODES.contains(ex.getErrorCode())) {
            status = HttpStatus.NOT_FOUND;
        } else if (CONFLICT_CODES.contains(ex.getErrorCode())) {
            status = HttpStatus.CONFLICT;
        } else if (FORBIDDEN_CODES.contains(ex.getErrorCode())) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(new ErrorResponseDTO(
                status.getReasonPhrase(),
                status.value(),
                ex.getErrorLabel(),
                request.getRequestURI(),
                LocalDateTime.now(),
                details
        ));
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleJwtException(
            JwtException ex, HttpServletRequest request) {

        log.warn("JWT exception at {}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid or expired token",
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.warn("Authentication failed at {}: {}",
                request.getRequestURI(), ex.getClass().getSimpleName());

        String message = switch (ex) {
            case BadCredentialsException e -> "Invalid email or password";
            case DisabledException e -> "Account is disabled";
            case LockedException e -> "Account is locked";
            case AccountExpiredException e -> "Account has expired";
            case CredentialsExpiredException e -> "Credentials have expired";
            default -> "Authentication failed";
        };

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {

        log.warn("Invalid token at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex, HttpServletRequest request) {

        log.warn("Email already exists at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(
                "Conflict",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingCookie(
            MissingRequestCookieException ex, HttpServletRequest request) {

        log.warn("Missing cookie at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                "Refresh token missing",
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.warn("Invalid argument at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                "Invalid Argument",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception at {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                LocalDateTime.now(),
                null
        ));
    }
}
