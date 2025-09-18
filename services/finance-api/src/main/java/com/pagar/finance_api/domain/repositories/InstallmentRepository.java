package com.pagar.finance_api.domain.repositories;

import com.pagar.finance_api.domain.entities.Installment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
}
