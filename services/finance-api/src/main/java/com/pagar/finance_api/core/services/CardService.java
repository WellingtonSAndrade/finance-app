package com.pagar.finance_api.core.services;

import com.pagar.finance_api.api.dto.CardDTO;
import com.pagar.finance_api.core.exceptions.ResourceNotFoundException;
import com.pagar.finance_api.core.security.user.LoggedUserService;
import com.pagar.finance_api.domain.entities.Card;
import com.pagar.finance_api.domain.entities.User;
import com.pagar.finance_api.domain.repositories.CardRepository;
import com.pagar.finance_api.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final LoggedUserService loggedUserService;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, LoggedUserService loggedUserService,
                       UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.loggedUserService = loggedUserService;
        this.userRepository = userRepository;
    }

    @Transactional
    public CardDTO insert(CardDTO dto) {
        UUID userId = loggedUserService.getLoggedUserId();

        Card card = dto.toEntity();

        User userRef = userRepository.getReferenceById(userId);
        card.setUser(userRef);

        return CardDTO.fromEntity(cardRepository.save(card));
    }

    public CardDTO getById(UUID cardId){
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(loggedUserService.getLoggedUserId())) {
            throw new AccessDeniedException("You cannot access this card");
        }

        return CardDTO.fromEntity(card);
    }

    public List<CardDTO> getByUserId() {
        UUID userId = loggedUserService.getLoggedUserId();
        List<Card> cards = cardRepository.findByUserId(userId);

        if (cards.isEmpty()) {
            throw new EntityNotFoundException("No card found for user.");
        }

        return cards.stream()
                .map(CardDTO::fromEntity)
                .toList();
    }

    @Transactional
    public CardDTO update(UUID cardId, CardDTO dto) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(loggedUserService.getLoggedUserId())) {
            throw new AccessDeniedException("You cannot update this card");
        }

        card.setNickname(dto.nickname());
        card.setLastDigits(dto.lastDigits());
        card.setBrand(dto.brand());

        return CardDTO.fromEntity(cardRepository.save(card));
    }

    @Transactional
    public void delete(UUID cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(loggedUserService.getLoggedUserId())) {
            throw new AccessDeniedException("You do not have permission to delete this card");
        }

        cardRepository.delete(card);
    }
}
