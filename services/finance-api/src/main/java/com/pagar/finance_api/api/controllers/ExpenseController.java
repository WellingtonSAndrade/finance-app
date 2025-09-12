package com.pagar.finance_api.api.controllers;

import com.pagar.finance_api.api.dto.ExpenseFilterDTO;
import com.pagar.finance_api.api.dto.ExpenseRequestDTO;
import com.pagar.finance_api.api.dto.ExpenseResponseDTO;
import com.pagar.finance_api.core.services.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> insert(@RequestBody ExpenseRequestDTO dto) {
        ExpenseResponseDTO response = expenseService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> findByFilters(@ModelAttribute ExpenseFilterDTO dto) {
        List<ExpenseResponseDTO> expenses = expenseService.findByFilters(dto);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ExpenseResponseDTO> update(@PathVariable UUID id, @RequestBody ExpenseRequestDTO dto) {
        return ResponseEntity.ok(expenseService.update(id, dto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
