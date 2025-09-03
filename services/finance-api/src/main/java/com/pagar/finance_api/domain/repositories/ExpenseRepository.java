package com.pagar.finance_api.domain.repositories;

import com.pagar.finance_api.api.dto.ExpenseResponseDTO;
import com.pagar.finance_api.domain.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("""
            SELECT new com.pagar.finance_api.api.dto.ExpenseResponseDTO(
             e.id, e.amount, e.currency, e.date, e.paymentMethod,
             e.card.nickname, e.establishment.name, e.category.name
            )
            FROM Expense e
                WHERE e.user.id = :userId
                AND (:startDate IS NULL OR e.date >= :startDate)
                AND (:endDate IS NULL OR e.date <= :endDate)
                AND (:card IS NULL OR e.card.id = :card)
                AND (:category IS NULL OR e.category.id = :category)
                AND (:establishment IS NULL OR e.establishment.id = :establishment)
            """)
    List<ExpenseResponseDTO> findByFilter(UUID userId, Date startDate, Date endDate, UUID card, UUID category, UUID establishment);

    Optional<Expense> findByIdAndUserId(UUID expenseId, UUID userId);
}
