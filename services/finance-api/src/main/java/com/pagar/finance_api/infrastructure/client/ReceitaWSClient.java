package com.pagar.finance_api.infrastructure.client;

import com.pagar.finance_api.infrastructure.dto.ReceitaWSResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ReceitaWS",
        url = "https://receitaws.com.br"
)
public interface ReceitaWSClient {

    @GetMapping(value = "/v1/cnpj/{cnpj}")
    ReceitaWSResponseDTO getCompanyByCnpj(@PathVariable String cnpj);
}
