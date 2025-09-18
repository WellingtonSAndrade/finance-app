package com.pagar.finance_api.core.services;

import com.pagar.finance_api.core.exceptions.InvalidInstallmentException;
import com.pagar.finance_api.domain.entities.Expense;
import com.pagar.finance_api.domain.entities.Installment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstallmentService {

    public List<Installment> generateInstallments(int totalInstallments, BigDecimal baseAmount, Expense expense) {
        BigDecimal totalAmount = baseAmount.multiply(BigDecimal.valueOf(totalInstallments));

        if (expense.getAmount().compareTo(totalAmount) != 0) {
            throw new InvalidInstallmentException("Total value of installments does not correspond to the value of the expense.");
        }

        List<Installment> installments = new ArrayList<>(totalInstallments);

        for (int i = 0; i < totalInstallments; i++) {
            Installment installment = new Installment();
            installment.setExpense(expense);
            installment.setTotal(totalInstallments);
            installment.setNumber(i + 1);
            installment.setAmount(baseAmount);
            installment.setDueDate(expense.getDate().plusMonths(i));

            installments.add(installment);
        }

        return installments;
    }
}
