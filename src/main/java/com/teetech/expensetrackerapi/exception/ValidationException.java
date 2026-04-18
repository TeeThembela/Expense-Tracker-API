package com.teetech.expensetrackerapi.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("One or more validation errors occurred");
        this.errors = errors;
    }

    public List<String> getErrors(){
        return errors;
    }
}
