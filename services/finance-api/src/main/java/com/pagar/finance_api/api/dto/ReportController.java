package com.pagar.finance_api.api.dto;

import com.pagar.finance_api.core.services.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public List<CategoryRankingDTO> getCategoryRanking(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return reportService.categoryRanking(yearMonth);
    }
}
