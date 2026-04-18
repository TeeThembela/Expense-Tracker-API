package com.teetech.expensetrackerapi.exception;

public class DuplicateBudgetException extends ExpenseServiceException {

  public DuplicateBudgetException(String categoryName) {
    super("A budget for category '" + categoryName + "' already exists within this date range.",
            "BUDGET_OVERLAP", "Budget overlap detected");
  }

  public DuplicateBudgetException(String message, String categoryName) {
    super(message + ": " + categoryName, "BUDGET_OVERLAP", "Budget overlap detected");
  }
}
