package com.HolgersDream.Deckforge.domain.interfaces;

import com.HolgersDream.Deckforge.domain.Card;
import com.HolgersDream.Deckforge.domain.Deck;
import com.HolgersDream.Deckforge.domain.DeckCard;

import java.util.List;
import java.util.Optional;

public interface IDeckRepository {
    List<Deck> findDecksById(int userId);
    void addDeckToUser(Deck newDeck);
    Optional<Deck> findDeckById(int deckId);
    List<DeckCard> findDeckCards(int deckId);
    //Har nu et Deck og Card som parameter/argumenter i stedet for bare deres id.
    void addCardToDeck(Deck deck, Card card, boolean isCommander);
    Optional<DeckCard> findDeckCardById(int deckContainId);
    void deleteCardFromDeck(int deckContainId);
}
