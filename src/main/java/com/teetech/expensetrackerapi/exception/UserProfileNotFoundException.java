package com.teetech.expensetrackerapi.exception;

public class UserProfileNotFoundException extends ExpenseServiceException {

    public UserProfileNotFoundException(String id) {
        super("User Profile not found with ID: " + id, "USER_PROFILE_NOT_FOUND",
                "User Profile not found");
    }

    public UserProfileNotFoundException(String message, String id ){
        super(message + ":" + id, "USER_PROFILE_NOT_FOUND", "User Profile not found");
    }

}
