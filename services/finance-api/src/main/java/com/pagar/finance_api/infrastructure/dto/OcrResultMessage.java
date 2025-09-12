package com.pagar.finance_api.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pagar.finance_api.core.dto.ReceiptSchemaDTO;

public record OcrResultMessage(@JsonProperty("task_id") String taskId,
                               @JsonProperty("user_id") String userId,
                               @JsonProperty("receipt_schema") ReceiptSchemaDTO receiptSchema) {
}
