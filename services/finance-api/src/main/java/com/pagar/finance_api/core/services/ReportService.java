package com.pagar.finance_api.core.services;

import com.pagar.finance_api.api.dto.CategoryRankingDTO;
import com.pagar.finance_api.infrastructure.persistence.ReportRepository;
import com.pagar.finance_api.infrastructure.projections.CategoryRankingProjection;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<CategoryRankingDTO> categoryRanking(YearMonth yearMonth) {
        int year = yearMonth.getYear();
        int month = yearMonth.getMonthValue();

        List<CategoryRankingProjection> rankingProjections = reportRepository.getCategoryRanking(month, year);

        List<CategoryRankingDTO> rankingDTOS = new ArrayList<>();
        for (CategoryRankingProjection r : rankingProjections) {
            CategoryRankingDTO categoryRankingDTO = new CategoryRankingDTO(r.getName(), r.getTotalAmount());
            rankingDTOS.add(categoryRankingDTO);
        }

        return rankingDTOS;
    }
}
