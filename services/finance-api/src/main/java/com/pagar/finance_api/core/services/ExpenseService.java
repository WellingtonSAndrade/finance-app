package com.pagar.finance_api.core.services;

import com.pagar.finance_api.api.dto.ExpenseFilterDTO;
import com.pagar.finance_api.api.dto.ExpenseRequestDTO;
import com.pagar.finance_api.api.dto.ExpenseResponseDTO;
import com.pagar.finance_api.core.dto.ReceiptSchemaDTO;
import com.pagar.finance_api.core.exceptions.ResourceNotFoundException;
import com.pagar.finance_api.core.security.user.LoggedUserService;
import com.pagar.finance_api.domain.entities.*;
import com.pagar.finance_api.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    private final LoggedUserService loggedUserService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final EstablishmentService establishmentService;
    private final OcrAnalyzerRepository ocrAnalyzerRepository;
    private final CardService cardService;

    public ExpenseService(LoggedUserService loggedUserService, CategoryRepository categoryRepository, UserRepository userRepository, ExpenseRepository expenseRepository, EstablishmentService establishmentService,
                          OcrAnalyzerRepository ocrAnalyzerRepository, CardService cardService) {
        this.loggedUserService = loggedUserService;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.establishmentService = establishmentService;
        this.ocrAnalyzerRepository = ocrAnalyzerRepository;
        this.cardService = cardService;
    }

    @Transactional
    public ExpenseResponseDTO insert(ExpenseRequestDTO dto) {
        UUID userId = loggedUserService.getLoggedUserId();

        Card card = cardService.findOrCreateByLastDigits(dto.cardLastDigits(), userId);
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

    public void createExpenseFromSchema(String taskId, ReceiptSchemaDTO schema) {
        OcrAnalyzer analyzer = ocrAnalyzerRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        UUID userId = analyzer.getUser().getId();

        Card card = cardService.findOrCreateByLastDigits(schema.cardLastDigits(), userId);

        Establishment establishment = establishmentService.findOrCreateByCnpj(schema.cnpj());

        Category category = establishment.getMainActivity();

        Expense expense = new Expense();
        expense.setUser(userRepository.getReferenceById(userId));
        expense.setCard(card);
        expense.setEstablishment(establishment);
        expense.setCategory(category);
        expense.setAmount(schema.amount());
        expense.setCurrency(schema.currency());
        expense.setDate(schema.date());
        expense.setPaymentMethod(schema.paymentMethod());

        expenseRepository.save(expense);
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

        if (dto.cardLastDigits() != null) {
            Card card = cardService.findOrCreateByLastDigits(dto.cardLastDigits(), userId);
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
}

