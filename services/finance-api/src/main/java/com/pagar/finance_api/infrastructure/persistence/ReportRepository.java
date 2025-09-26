package com.pagar.finance_api.infrastructure.persistence;

import com.pagar.finance_api.infrastructure.projections.CategoryRankingProjection;
import com.pagar.finance_api.domain.entities.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends CrudRepository<Expense, UUID> {

    @Query(nativeQuery = true, value = """
                SELECT
                    tc."name" AS name,
                    SUM(te.amount) AS totalAmount
                FROM tb_expense te
                INNER JOIN tb_category tc
                    ON te.category_id = tc.id
                WHERE EXTRACT(MONTH FROM te.date) = :month
                  AND EXTRACT(YEAR FROM te.date) = :year
                GROUP BY tc."name"
                ORDER BY SUM(te.amount) DESC;
            """)
    List<CategoryRankingProjection> getCategoryRanking(int month, int year);
}
