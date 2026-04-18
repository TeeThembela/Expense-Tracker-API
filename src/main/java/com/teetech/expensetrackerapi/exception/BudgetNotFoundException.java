package com.teetech.expensetrackerapi.exception;

public class BudgetNotFoundException extends ExpenseServiceException {
    public BudgetNotFoundException(String id) {
        super("Budget not found with ID:  " + id, "BUDGET_NOT_FOUND",
                "Budget not found");
    }

    public BudgetNotFoundException(String message, String id){
        super(message + ":" + id, "BUDGET_NOT_FOUND",
                "Budget not found");
    }


}
