package com.pagar.finance_api.core.services;

import com.pagar.finance_api.core.dto.ReceiptSchemaDTO;
import com.pagar.finance_api.core.exceptions.*;
import com.pagar.finance_api.core.security.user.LoggedUserService;
import com.pagar.finance_api.domain.entities.OcrAnalyzer;
import com.pagar.finance_api.domain.repositories.OcrAnalyzerRepository;
import com.pagar.finance_api.domain.repositories.UserRepository;
import com.pagar.finance_api.infrastructure.client.rabbit.OcrRequestPublisher;
import com.pagar.finance_api.infrastructure.client.storage.StorageClient;
import com.pagar.finance_api.infrastructure.dto.OcrMessageDTO;
import com.pagar.finance_api.infrastructure.dto.OcrResultMessage;
import com.pagar.finance_api.infrastructure.utils.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OcrService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpg", "image/png", "image/jpeg");
    private static final long MAX_SIZE = 5 << 20;

    private final StorageClient storageClient;
    private final OcrAnalyzerRepository ocrAnalyzerRepository;
    private final OcrRequestPublisher ocrRequestPublisher;
    private final LoggedUserService loggedUserService;
    private final UserRepository userRepository;

    public OcrService(StorageClient storageClient, OcrAnalyzerRepository ocrAnalyzerRepository, OcrRequestPublisher ocrRequestPublisher, LoggedUserService loggedUserService,
                      UserRepository userRepository) {
        this.storageClient = storageClient;
        this.ocrAnalyzerRepository = ocrAnalyzerRepository;
        this.ocrRequestPublisher = ocrRequestPublisher;
        this.loggedUserService = loggedUserService;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createTask(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFileTypeException(file.getContentType());
        }
        if (file.getSize() > MAX_SIZE) {
            throw new FileTooLargeException(file.getSize(), MAX_SIZE);
        }

        UUID userId = loggedUserService.getLoggedUserId();

        String taskId = String.valueOf(UUID.randomUUID());
        String imageUrl = storageClient.upload(file);

        OcrAnalyzer entity = new OcrAnalyzer();
        entity.setTaskId(taskId);
        entity.setImageUrl(imageUrl);
        entity.setStatus(OcrAnalyzer.Status.PENDING);
        entity.setUser(userRepository.getReferenceById(userId));
        ocrAnalyzerRepository.save(entity);

        ocrRequestPublisher.send(new OcrMessageDTO(taskId, imageUrl, userId));

        return taskId;
    }

    @Transactional
    public ReceiptSchemaDTO processOcrResponse(OcrResultMessage message) {
        OcrAnalyzer analyzer = ocrAnalyzerRepository.findById(message.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + message.taskId()));

        if (!analyzer.getUser().getId().equals(UUID.fromString(message.userId()))) {
            throw new UserMismatchException("User mismatch for task " + message.taskId());
        }

        String schemaJson = message.receiptSchema().toString();
        analyzer.setReceiptSchema(schemaJson);

        try {
            if (JsonUtil.hasNullFields(message.receiptSchema())) {
                analyzer.setStatus(OcrAnalyzer.Status.ERROR);
                analyzer.setStatusError("[" + LocalDateTime.now() + "] Null fields: " + JsonUtil.getNullFieldsAsString(message.receiptSchema()));
            } else {
                analyzer.setStatus(OcrAnalyzer.Status.DONE);
                analyzer.setStatusError(null);
            }

            ocrAnalyzerRepository.save(analyzer);
            return message.receiptSchema();
        } catch (Exception e) {
            analyzer.setStatus(OcrAnalyzer.Status.ERROR);
            analyzer.setStatusError("[" + LocalDateTime.now() + "] Invalid Json: " + e.getMessage());
            ocrAnalyzerRepository.save(analyzer);
            throw new OcrProcessingException("Failed to process OCR response", e);
        }
    }
}
