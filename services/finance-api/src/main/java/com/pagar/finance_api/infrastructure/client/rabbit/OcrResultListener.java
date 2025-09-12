package com.pagar.finance_api.infrastructure.client.rabbit;

import com.pagar.finance_api.core.dto.ReceiptSchemaDTO;
import com.pagar.finance_api.core.exceptions.ResourceNotFoundException;
import com.pagar.finance_api.core.services.ExpenseService;
import com.pagar.finance_api.core.services.OcrService;
import com.pagar.finance_api.domain.entities.OcrAnalyzer;
import com.pagar.finance_api.domain.repositories.OcrAnalyzerRepository;
import com.pagar.finance_api.infrastructure.dto.OcrResultMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OcrResultListener {

    private final ExpenseService expenseService;
    private final OcrService ocrService;
    private final OcrAnalyzerRepository ocrAnalyzerRepository;

    public OcrResultListener(ExpenseService expenseService, OcrService ocrService,
                             OcrAnalyzerRepository ocrAnalyzerRepository) {
        this.expenseService = expenseService;
        this.ocrService = ocrService;
        this.ocrAnalyzerRepository = ocrAnalyzerRepository;
    }

    @RabbitListener(queues = "${rabbitmq.response-queue.name}")
    public void ocrResult(OcrResultMessage message) {
        ReceiptSchemaDTO schema = ocrService.processOcrResponse(message);

        OcrAnalyzer analyzer = ocrAnalyzerRepository.findById(message.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + message.taskId()));

        if (analyzer.getStatus() == OcrAnalyzer.Status.DONE) {
            expenseService.createExpenseFromSchema(message.taskId(), schema);
        }
    }
}
