package com.teetech.expensetrackerapi.dto;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public record ExpenseFilterCriteria(
        LocalDate startDate,      // For date range
        LocalDate endDate,        // For date range
        UUID categoryId,          // For category filter
        String period             // "PAST_WEEK", "PAST_MONTH", "LAST_3_MONTHS", "CUSTOM"
) {
    public ExpenseFilterCriteria{
        if ((startDate == null && endDate != null) || (startDate != null && endDate == null)){
            log.warn("Both dates are required if one is provided. StartDate: {}, EndDate: {}",
                    startDate, endDate);
            throw new IllegalArgumentException("Both dates are required if one is provided.");
        }

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                log.warn("Start date cannot be after end date. StartDate: {}, EndDate: {}", startDate,
                        endDate);
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }
        }
    }
}
