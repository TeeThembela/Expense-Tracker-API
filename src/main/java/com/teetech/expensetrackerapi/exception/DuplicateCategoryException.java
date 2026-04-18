package com.teetech.expensetrackerapi.exception;

public class DuplicateCategoryException extends ExpenseServiceException {
    public DuplicateCategoryException(String name) {
        super("Category with this name: " + name + " already exists.", "CATEGORY_ALREADY_EXIST",
                "Category already exists");
    }

    public DuplicateCategoryException(String message, String name){
        super(message + ":" + name, "CATEGORY_ALREADY_EXIST",
                "Category already exists");
    }
}
