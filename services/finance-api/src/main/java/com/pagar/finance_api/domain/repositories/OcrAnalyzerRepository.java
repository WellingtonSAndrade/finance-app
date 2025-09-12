package com.pagar.finance_api.domain.repositories;

import com.pagar.finance_api.domain.entities.OcrAnalyzer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrAnalyzerRepository extends JpaRepository<OcrAnalyzer, String> {
}
