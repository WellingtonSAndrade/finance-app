package com.pagar.finance_api.api.dto;

import com.pagar.finance_api.core.dto.InstallmentSchemaDTO;
import com.pagar.finance_api.domain.entities.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequestDTO(UUID id, BigDecimal amount, String currency, LocalDate date, String paymentMethod, String cardLastDigits, String establishmentCnpj,
                                UUID categoryId, boolean hasInstallments, InstallmentSchemaDTO installments) {
    public Expense toEntity() {
        Expense expense = new Expense();
        expense.setAmount(this.amount);
        expense.setCurrency(this.currency);
        expense.setDate(this.date);
        expense.setPaymentMethod(this.paymentMethod);
        return expense;
    }
}
