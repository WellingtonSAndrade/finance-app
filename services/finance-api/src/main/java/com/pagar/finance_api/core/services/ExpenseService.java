package com.pagar.finance_api.core.services;

import com.pagar.finance_api.api.dto.ExpenseFilterDTO;
import com.pagar.finance_api.api.dto.ExpenseRequestDTO;
import com.pagar.finance_api.api.dto.ExpenseResponseDTO;
import com.pagar.finance_api.api.dto.OcrRequestDTO;
import com.pagar.finance_api.core.exceptions.EmptyFileException;
import com.pagar.finance_api.core.exceptions.FileTooLargeException;
import com.pagar.finance_api.core.exceptions.InvalidFileTypeException;
import com.pagar.finance_api.core.exceptions.ResourceNotFoundException;
import com.pagar.finance_api.core.security.user.LoggedUserService;
import com.pagar.finance_api.domain.entities.Card;
import com.pagar.finance_api.domain.entities.Category;
import com.pagar.finance_api.domain.entities.Establishment;
import com.pagar.finance_api.domain.entities.Expense;
import com.pagar.finance_api.domain.repositories.CardRepository;
import com.pagar.finance_api.domain.repositories.CategoryRepository;
import com.pagar.finance_api.domain.repositories.ExpenseRepository;
import com.pagar.finance_api.domain.repositories.UserRepository;
import com.pagar.finance_api.infrastructure.client.rabbit.OcrRequestPublisher;
import com.pagar.finance_api.infrastructure.client.storage.StorageClient;
import com.pagar.finance_api.infrastructure.dto.OcrMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpg", "image/png");
    private static final long MAX_SIZE = 5 << 20;

    private final LoggedUserService loggedUserService;
    private final CardRepository cardRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final EstablishmentService establishmentService;
    private final StorageClient storageClient;
    private final OcrRequestPublisher ocrRequestPublisher;

    public ExpenseService(LoggedUserService loggedUserService, CardRepository cardRepository, CategoryRepository categoryRepository, UserRepository userRepository, ExpenseRepository expenseRepository, EstablishmentService establishmentService, StorageClient storageClient, OcrRequestPublisher ocrRequestPublisher) {
        this.loggedUserService = loggedUserService;
        this.cardRepository = cardRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.establishmentService = establishmentService;
        this.storageClient = storageClient;
        this.ocrRequestPublisher = ocrRequestPublisher;
    }

    @Transactional
    public ExpenseResponseDTO insert(ExpenseRequestDTO dto) {
        UUID userId = loggedUserService.getLoggedUserId();

        Card card = getCard(dto.cardId(), userId);
        Establishment establishment = establishmentService.findOrCreateByCnpj(dto.establishmentCnpj());

        Category category;
        if (dto.categoryId() != null) {
            category = getCategory(dto.categoryId());
        } else {
            category = establishment.getMainActivity();
        }
        Expense expense = dto.toEntity();
        expense.setUser(userRepository.getReferenceById(userId));
        expense.setCard(card);
        expense.setEstablishment(establishment);
        expense.setCategory(category);

        return ExpenseResponseDTO.fromEntity(expenseRepository.save(expense));
    }

    public OcrRequestDTO insertFromUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFileTypeException(file.getContentType());
        }
        if (file.getSize() > MAX_SIZE) {
            throw new FileTooLargeException(file.getSize(), MAX_SIZE);
        }

        String taskId = String.valueOf(UUID.randomUUID());
        String imageUrl = storageClient.upload(file);

        ocrRequestPublisher.send(new OcrMessageDTO(taskId, imageUrl));

        return new OcrRequestDTO(taskId, imageUrl);
    }

    public List<ExpenseResponseDTO> findByFilters(ExpenseFilterDTO filter) {
        UUID userId = loggedUserService.getLoggedUserId();

        return expenseRepository.findByFilter(userId,
                filter.startDate(),
                filter.endDate(),
                filter.card(),
                filter.category(),
                filter.establishment());
    }

    @Transactional
    public ExpenseResponseDTO update(UUID expenseId, ExpenseRequestDTO dto) {
        UUID userId = loggedUserService.getLoggedUserId();

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId).orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        if (dto.amount() != null) expense.setAmount(dto.amount());
        if (dto.currency() != null) expense.setCurrency(dto.currency());
        if (dto.date() != null) expense.setDate(dto.date());
        if (dto.paymentMethod() != null) expense.setPaymentMethod(dto.paymentMethod());

        if (dto.cardId() != null) {
            Card card = getCard(dto.cardId(), userId);
            expense.setCard(card);
        }
        if (dto.establishmentCnpj() != null) {
            Establishment establishment = establishmentService.findOrCreateByCnpj(dto.establishmentCnpj());
            expense.setEstablishment(establishment);
        }
        if (dto.categoryId() != null) {
            Category category = getCategory(dto.categoryId());
            expense.setCategory(category);
        }

        return ExpenseResponseDTO.fromEntity(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(UUID expenseId) {
        UUID userId = loggedUserService.getLoggedUserId();

        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        expenseRepository.delete(expense);
    }

    private Category getCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private Card getCard(UUID cardId, UUID userId) {
        return cardRepository.findByIdAndUserId(cardId, userId).orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));
    }
}

