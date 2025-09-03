package com.pagar.finance_api.api.dto;

import com.pagar.finance_api.domain.entities.Expense;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ExpenseResponseDTO(UUID id, BigDecimal amount, String currency, Date date, String paymentMethod, String card, String establishment,
                                 String category) {
    public static ExpenseResponseDTO fromEntity(Expense expense) {
        return new ExpenseResponseDTO(expense.getId(), expense.getAmount(), expense.getCurrency(), expense.getDate(), expense.getPaymentMethod(),
                expense.getCard().getNickname(), expense.getEstablishment().getName(), expense.getCategory().getName());
    }
}
