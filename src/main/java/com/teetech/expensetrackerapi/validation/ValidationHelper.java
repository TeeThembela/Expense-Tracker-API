package com.teetech.expensetrackerapi.validation;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationHelper {

    private static final Pattern CATEGORY_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s'-]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    // Check if date is more than X days in the future
    public static boolean isFurtherThan(LocalDate date, int days){
        if (date == null) return false;

        LocalDate threshold = LocalDate.now().plusDays(days);
        return date.isAfter(threshold);
    }

    // Check if date is more than X in the past
    public static boolean isOlderThan(LocalDate date, int days){
        if (date == null) return false;

        LocalDate threshold = LocalDate.now().minusDays(days);
        return date.isBefore(threshold);
    }

    // Validate category name format
    public static boolean isValidCategoryName(String name){
        String trimmed = name.trim();
        return CATEGORY_NAME_PATTERN.matcher(trimmed).matches();
    }

    // Validate if string is not just whitespace
    public static boolean hasContent(String text){
        return text != null && !text.trim().isEmpty();
    }

    // Validate name
    public static boolean isValidName(String name){
        if (name == null || name.isBlank()) return false;

        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    // Validate phone number
    public static boolean isValidPhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.isBlank()) return false;

        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    // Check if phone number is reasonable (not all same digits, not sequential)
    public static boolean isReasonablePhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.trim().length() != 10) return false;

        // Check if all digits are the same
        if(phoneNumber.chars().distinct().count() == 1) return false;

        // Check if sequential (0123456789)
        if (phoneNumber.equals("0123456789")) return false;

        return true;
    }
}
