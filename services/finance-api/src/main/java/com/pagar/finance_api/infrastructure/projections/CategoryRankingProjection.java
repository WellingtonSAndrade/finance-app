package com.pagar.finance_api.infrastructure.projections;

import java.math.BigDecimal;

public interface CategoryRankingProjection {
    String getName();
    BigDecimal getTotalAmount();
}
