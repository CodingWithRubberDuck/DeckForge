package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.domain.Card;
import com.HolgersDream.Deckforge.domain.interfaces.ICardRepository;

import java.util.List;
import java.util.Optional;

class FakeCardRepository implements ICardRepository {

    @Override
    public Optional<Card> getCardById(int cardId) {

        if (cardId == 100) {
            return Optional.of(new Card());
        }

        return Optional.empty();
    }

    @Override
    public List<Card> getAllCards() {
        return List.of();
    }

    @Override
    public List<Card> findCardsByName(String name) {
        return List.of();
    }
}