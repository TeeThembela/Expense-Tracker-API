package com.teetech.expensetrackerapi.enums;

public enum PredefinedCategory {
    GROCERIES("Groceries", "Food and grocery shopping"),
    LEISURE("Leisure", "Entertainment and recreation"),
    ELECTRONICS("Electronics", "Electronic devices and gadgets"),
    UTILITIES("Utilities", "Bills, electricity, water, internet"),
    CLOTHING("Clothing", "Clothes and accessories"),
    HEALTH("Health", "Medical expenses and healthcare"),
    OTHERS("Others", "Miscellaneous expenses");

    private final String displayName;
    private final String description;

    PredefinedCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
