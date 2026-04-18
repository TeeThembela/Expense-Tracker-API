package com.teetech.expensetrackerapi.exception;

public class ExpenseNotFoundException extends ExpenseServiceException {
    public ExpenseNotFoundException(String id) {
        super("Expense not found with ID:  " + id, "EXPENSE_NOT_FOUND", "Expense not found");
    }

    public ExpenseNotFoundException(String message, String id) {
        super(message + ":" + id, "EXPENSE_NOT_FOUND", "Expense not found");
    }
}
