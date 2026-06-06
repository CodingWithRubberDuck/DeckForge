package com.HolgersDream.Deckforge.domain;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private int deckId;
    private int userId;
    private int cardAmount;
    private String deckName;
    private Format format;
    private List<DeckCard> cards;



    public Deck(int deckId, int userId, int cardAmount, String deckName, Format format) {
        if (deckId < 0){
            throw new IllegalArgumentException("Ikke et gyldigt id for et deck");
        }
        this.deckId = deckId;
        if (userId < 0){
            throw new IllegalArgumentException("Ikke et gyldigt bruger id for et deck");
        }
        this.userId = userId;
        if (cardAmount < 0){
            throw new IllegalArgumentException("Et deck kan ikke indeholde et negativt antal kort");
        }
        this.cardAmount = cardAmount;

        final int MAX_DECK_NAME = 100;
        if (deckName == null || deckName.isBlank()){
            throw new IllegalArgumentException("Et deck skal indeholde et navn");
        } else if (deckName.length() > MAX_DECK_NAME) {
            throw new IllegalArgumentException("Et decks navn kan maksimalt være " + MAX_DECK_NAME + " tegn langt");
        }
        this.deckName = deckName;
        if (format == null){
            throw new IllegalArgumentException("Et deck skal have tildelt et format");
        }
        this.format = format;

        this.cards = new ArrayList<>();
    }

    /// Getters
    public int getDeckId() {
        return deckId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCardAmount() {
        return cardAmount;
    }

    public String getDeckName(){
        return deckName;
    }

    public Format getFormat() {
        return format;
    }

    public List<DeckCard> getCards() {
        return cards;
    }

    // Tilføjet en specifik metode til at tilføje kort til et deck
    /// Others
    public void addAllCards(List<DeckCard> newCards){
        this.cards.addAll(newCards);
    }

}
