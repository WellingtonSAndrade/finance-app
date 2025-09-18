package com.pagar.finance_api.domain.repositories;

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

    Optional<Expense> findByIdAndUserId(UUID expenseId, UUID userId);

    @Query("""
            SELECT e FROM Expense e
            JOIN FETCH e.card
            JOIN FETCH e.category
            JOIN FETCH e.establishment
            JOIN FETCH e.installments
            WHERE e.user.id = :userId
            AND (:startDate IS NULL OR e.date >= :startDate)
            AND (:endDate IS NULL OR e.date <= :endDate)
            AND (:card IS NULL OR e.card.id = :card)
            AND (:category IS NULL OR e.category.id = :category)
            AND (:establishment IS NULL OR e.establishment.id = :establishment)
            """)
    List<Expense> findByFilter(UUID userId, Date startDate, Date endDate, UUID card, UUID category, UUID establishment);
}
