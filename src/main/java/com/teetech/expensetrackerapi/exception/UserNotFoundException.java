package com.teetech.expensetrackerapi.exception;

public class UserNotFoundException extends ExpenseServiceException {

    public UserNotFoundException(String id) {
        super("User not found with ID: " + id, "USER_NOT_FOUND", "User not found");
    }

    public UserNotFoundException(String message, String id){
        super(message + ":" + id, "USER_NOT_FOUND", "User not found");
    }

}
