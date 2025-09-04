package com.pagar.finance_api.core.services;

import com.pagar.finance_api.core.exceptions.ExternalApiException;
import com.pagar.finance_api.domain.entities.Category;
import com.pagar.finance_api.domain.entities.Establishment;
import com.pagar.finance_api.domain.repositories.CategoryRepository;
import com.pagar.finance_api.domain.repositories.EstablishmentRepository;
import com.pagar.finance_api.infrastructure.client.ReceitaWSClient;
import com.pagar.finance_api.infrastructure.dto.ReceitaWSResponseDTO;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final ReceitaWSClient receitaWSClient;
    private final CategoryRepository categoryRepository;

    public EstablishmentService(EstablishmentRepository establishmentRepository, ReceitaWSClient receitaWSClient,
                                CategoryRepository categoryRepository) {
        this.establishmentRepository = establishmentRepository;
        this.receitaWSClient = receitaWSClient;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Establishment findOrCreateByCnpj(String cnpj) {
        return establishmentRepository.findByCnpj(cnpj)
                .orElseGet(() -> createEstablishment(cnpj));
    }

    private Establishment createEstablishment(String cnpj) {
        try {
            ReceitaWSResponseDTO response = receitaWSClient.getCompanyByCnpj(cnpj);

            if (response == null) {
                throw new ExternalApiException("Establishment not found in the external API for the CNPJ: " + cnpj);
            }

            if (response.atividade_principal() == null || response.atividade_principal().isEmpty()) {
                throw new ExternalApiException("Main activity not found in ReceitaWS response for the CNPJ: " + cnpj);
            }

            Category category = categoryRepository.findByCnae(response.atividade_principal().get(0).code())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setCnae(response.atividade_principal().get(0).code());
                        newCategory.setName(response.atividade_principal().get(0).text());
                        return categoryRepository.save(newCategory);
                    });

            Establishment establishment = new Establishment();
            establishment.setCnpj(response.cnpj());
            establishment.setName(response.nome());
            establishment.setFantasy(response.fantasia());
            establishment.setMainActivity(category);

            return establishmentRepository.save(establishment);
        } catch (FeignException e) {
            throw new ExternalApiException("Error when querying ReceitaWS for CNPJ:" + cnpj, e);
            }
    }
}
