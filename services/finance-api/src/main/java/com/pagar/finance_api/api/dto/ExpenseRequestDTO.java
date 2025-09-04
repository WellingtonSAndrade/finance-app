package com.pagar.finance_api.api.dto;

import com.pagar.finance_api.domain.entities.Expense;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ExpenseRequestDTO(UUID id, BigDecimal amount, String currency, Date date, String paymentMethod, UUID cardId, String establishmentCnpj,
                         UUID categoryId) {
    public Expense toEntity() {
        Expense expense = new Expense();
        expense.setAmount(this.amount);
        expense.setCurrency(this.currency);
        expense.setDate(this.date);
        expense.setPaymentMethod(this.paymentMethod);
        return expense;
    }
}
