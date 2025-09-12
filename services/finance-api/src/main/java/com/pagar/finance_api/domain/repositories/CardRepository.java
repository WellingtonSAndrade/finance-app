package com.pagar.finance_api.domain.repositories;

import com.pagar.finance_api.domain.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> findByUserId(UUID userId);

    Optional<Card> findByIdAndUserId(UUID uuid, UUID userId);

    Optional<Card> findByLastDigitsAndUserId(String lastDigits, UUID userId);
}
