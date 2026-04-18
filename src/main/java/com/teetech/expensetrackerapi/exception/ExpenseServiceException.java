package com.teetech.expensetrackerapi.exception;

public class ExpenseServiceException extends RuntimeException {
    private final String errorCode;
    private final String errorLabel;

    public ExpenseServiceException(String message, String errorCode, String errorLabel) {
        super(message);
        this.errorCode = errorCode;
        this.errorLabel = errorLabel;
    }

    public String getErrorCode(){
        return errorCode;
    }

    public String getErrorLabel() {
        return errorLabel;
    }
}
