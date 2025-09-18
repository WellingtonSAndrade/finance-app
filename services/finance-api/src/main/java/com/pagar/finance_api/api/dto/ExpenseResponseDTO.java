package com.pagar.finance_api.api.dto;

import com.pagar.finance_api.domain.entities.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ExpenseResponseDTO(UUID id, BigDecimal amount, String currency, LocalDate date, String paymentMethod, String card,
                                 String establishment, String category, List<InstallmentDTO> installments) {
    private record InstallmentDTO(Integer total, Integer number, BigDecimal amount, LocalDate dueDate) {
    }

    public static ExpenseResponseDTO fromEntity(Expense expense) {
        return new ExpenseResponseDTO(expense.getId(), expense.getAmount(), expense.getCurrency(), expense.getDate(),
                expense.getPaymentMethod(), expense.getCard().getNickname(), expense.getEstablishment().getName(),
                expense.getCategory().getName(), expense.getInstallments().stream().map(installment -> new InstallmentDTO(
                        installment.getTotal(),
                        installment.getNumber(),
                        installment.getAmount(),
                        installment.getDueDate()
                )
            ).toList());
    }
}
