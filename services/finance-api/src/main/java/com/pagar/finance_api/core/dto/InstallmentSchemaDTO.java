package com.pagar.finance_api.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record InstallmentSchemaDTO(@JsonProperty("total_installments") int totalInstallments, BigDecimal amount) {
}
