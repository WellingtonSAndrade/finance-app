package com.pagar.finance_api.domain.repositories;

import com.pagar.finance_api.domain.entities.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, UUID> {
    Optional<Establishment> findByCnpj(String establishmentCnpj);
}