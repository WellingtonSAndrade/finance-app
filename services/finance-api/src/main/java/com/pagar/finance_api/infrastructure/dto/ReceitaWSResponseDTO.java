package com.pagar.finance_api.infrastructure.dto;

import java.util.List;

public record ReceitaWSResponseDTO(String cnpj,
                                   String nome,
                                   String fantasia,
                                   List<AtividadePrincipalDTO> atividade_principal) {
    public record AtividadePrincipalDTO(String code,
                                        String text) {
    }
}

