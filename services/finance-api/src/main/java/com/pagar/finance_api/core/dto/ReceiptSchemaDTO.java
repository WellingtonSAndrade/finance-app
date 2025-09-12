package com.pagar.finance_api.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceiptSchemaDTO(String cnpj, BigDecimal amount, String currency, LocalDate date,
                               @JsonProperty("payment_method") String paymentMethod,
                               @JsonProperty("card_last_digits") String cardLastDigits, Installments installments) {

    public record Installments(boolean isInstallment, int numberOfInstallments, BigDecimal installmentValue) {
    }
}
