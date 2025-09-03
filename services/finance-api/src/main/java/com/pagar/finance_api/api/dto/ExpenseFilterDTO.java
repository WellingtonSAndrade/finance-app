package com.pagar.finance_api.api.dto;

import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public record ExpenseFilterDTO(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
                               UUID card,
                               UUID category,
                               UUID establishment) {
    public ExpenseFilterDTO {
        if (endDate != null) {
            endDate = Date.from(endDate.toInstant().plus(1, ChronoUnit.DAYS).minusMillis(1));
        }
    }

    @AssertTrue(message = "Start date cannot be after end date")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.after(endDate);
    }
}
