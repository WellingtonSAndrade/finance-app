package com.pagar.finance_api.core.services;

import com.pagar.finance_api.api.dto.ExpenseFilterDTO;
import com.pagar.finance_api.api.dto.ExpenseRequestDTO;
import com.pagar.finance_api.api.dto.ExpenseResponseDTO;
import com.pagar.finance_api.core.exceptions.ResourceNotFoundException;
import com.pagar.finance_api.core.security.user.LoggedUserService;
import com.pagar.finance_api.domain.entities.Card;
import com.pagar.finance_api.domain.entities.Category;
import com.pagar.finance_api.domain.entities.Establishment;
import com.pagar.finance_api.domain.entities.Expense;
import com.pagar.finance_api.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    private final LoggedUserService loggedUserService;
    private final CardRepository cardRepository;
    private final EstablishmentRepository establishmentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public ExpenseService(LoggedUserService loggedUserService, CardRepository cardRepository, EstablishmentRepository establishmentRepository, CategoryRepository categoryRepository, UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.loggedUserService = loggedUserService;
        this.cardRepository = cardRepository;
        this.establishmentRepository = establishmentRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional
    public ExpenseResponseDTO insert(ExpenseRequestDTO dto) {
        UUID userId = loggedUserService.getLoggedUserId();

        Card card = getCard(dto.cardId(), userId);
        Establishment establishment = getEstablishment(dto.establishmentId());
        Category category = getCategory(dto.categoryId());

        Expense expense = dto.toEntity();
        expense.setUser(userRepository.getReferenceById(userId));
        expense.setCard(card);
        expense.setEstablishment(establishment);
        expense.setCategory(category);

        return ExpenseResponseDTO.fromEntity(expenseRepository.save(expense));
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
        if (dto.establishmentId() != null) {
            Establishment establishment = getEstablishment(dto.establishmentId());
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

    private Establishment getEstablishment(UUID establishmentId) {
        return establishmentRepository.findById(establishmentId).orElseThrow(() -> new ResourceNotFoundException("Establishment not found with ID: " + establishmentId));
    }

    private Card getCard(UUID cardId, UUID userId) {
        return cardRepository.findByIdAndUserId(cardId, userId).orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));
    }
}

