package com.teetech.expensetrackerapi.exception;

public class CategoryNotFoundException extends ExpenseServiceException {
    public CategoryNotFoundException(String id) {
        super("Category not found with ID:  " + id, "CATEGORY_NOT_FOUND",
                "Category not found");
    }

    public CategoryNotFoundException(String message, String id){
        super(message + ":" + id, "CATEGORY_NOT_FOUND",
                "Category not found");
    }
}
