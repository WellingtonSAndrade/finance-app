package com.pagar.finance_api.api.dto;

import com.pagar.finance_api.domain.entities.Card;

import java.util.UUID;

public record CardDTO(UUID id, String nickname, String lastDigits, String brand) {
    public static CardDTO fromEntity(Card card) {
        return new CardDTO(card.getId(), card.getNickname(), card.getLastDigits(), card.getBrand());
    }

    public Card toEntity() {
        Card card = new Card();
        card.setNickname(this.nickname);
        card.setLastDigits(this.lastDigits);
        card.setBrand(this.brand);
        return card;
    }
}
