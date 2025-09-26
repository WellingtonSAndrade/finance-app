package com.pagar.finance_api.api.dto;

import java.math.BigDecimal;

public record CategoryRankingDTO(String name, BigDecimal totalAmount) {
}
