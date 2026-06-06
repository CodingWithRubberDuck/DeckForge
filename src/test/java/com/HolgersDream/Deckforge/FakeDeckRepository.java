package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.domain.*;
import com.HolgersDream.Deckforge.domain.interfaces.IDeckRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeDeckRepository implements IDeckRepository {



    private List<Deck> decks = new ArrayList<>();

    private List<DeckCard> deckCards = new ArrayList<>();




    @Override
    public List<Deck> findDecksById(int userId) {
        return decks;
    }

    @Override
    public void addDeckToUser(Deck newDeck) {
        decks.add(newDeck);
    }

    @Override
    public Optional<Deck> findDeckById(int deckId) {
        for (Deck deck : decks){
            if (deck.getDeckId() == deckId){
                return Optional.of(deck);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<DeckCard> findDeckCards(int deckId) {
        return deckCards;
    }

    @Override
    public void addCardToDeck(Deck deck, Card card, boolean isCommander) {
        deckCards.add(new DeckCard(card.getCardId(), card.getBlackMana(), card.getBlueMana(),
                card.getGreenMana(), card.getRedMana(), card.getWhiteMana(), card.getNeutralMana(), card.getName(),
                card.getSuperType(), card.getType(), card.getMultiType(), card.getSubType(), card.isCanBeCommander(),
                card.getPicture(), card.getSetName(), card.getRuleText(), card.getPower(), card.getToughness(),
                card.getRarity(), deck.getDeckId(), deckCards.size()+1, isCommander));
    }



    @Override
    public Optional<DeckCard> findDeckCardById(int deckContainId) {
        for (DeckCard card : deckCards){
            if (card.getDeckContainId() == deckContainId){
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteCardFromDeck(int deckContainId) {
        DeckCard deckCard = findDeckCard(deckContainId);
        if (deckCard != null){
            deckCards.remove(deckCard);
        } else {
            throw new RuntimeException("Somethings wrong");
        }
    }


    private DeckCard findDeckCard(int deckContainId){
        for (DeckCard card : deckCards){
            if (card.getDeckContainId() == deckContainId){
                return card;
            }
        }
        return null;
    }
}
