package com.pagar.finance_api.api.controllers;

import com.pagar.finance_api.api.dto.CardDTO;
import com.pagar.finance_api.core.services.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardDTO> insert(@RequestBody CardDTO dto) {
        dto = cardService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CardDTO> getById(@PathVariable UUID id) {
        CardDTO dto = cardService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CardDTO>> getUserCards() {
        List<CardDTO> cards = cardService.getByUserId();
        return ResponseEntity.ok(cards);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CardDTO> update(@PathVariable UUID id, @RequestBody CardDTO dto) {
        return ResponseEntity.ok(cardService.update(id, dto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
