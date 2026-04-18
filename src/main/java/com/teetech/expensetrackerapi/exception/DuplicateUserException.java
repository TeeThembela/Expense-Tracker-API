package com.teetech.expensetrackerapi.exception;

public class DuplicateUserException extends ExpenseServiceException {

    public DuplicateUserException(String email) {
        super("An account with this email: " + email + " already exists.", "EMAIL_ALREADY_TAKEN",
                "User already exists");
    }

    public DuplicateUserException(String message, String id){
        super(message + ":" + id, "EMAIL_ALREADY_TAKEN", "User already exists");
    }

}
