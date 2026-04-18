package com.teetech.expensetrackerapi.exception;

public class DuplicateUserProfileException extends ExpenseServiceException {

    public DuplicateUserProfileException(String userId) {
        super("This user already has a profile. User ID: " + userId, "USER_PROFILE_FOUND",
                "User Profile already exists");
    }

    public DuplicateUserProfileException(String message, String userId){
        super(message + ":" + userId, "USER_PROFILE_FOUND", "User Profile already exists");
    }

}
