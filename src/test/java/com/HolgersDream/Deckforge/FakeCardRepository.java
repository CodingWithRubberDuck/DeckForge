package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.domain.Card;
import com.HolgersDream.Deckforge.domain.Rarity;
import com.HolgersDream.Deckforge.domain.SuperType;
import com.HolgersDream.Deckforge.domain.Type;
import com.HolgersDream.Deckforge.domain.interfaces.ICardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeCardRepository implements ICardRepository {

    private List<Card> cards = new ArrayList<>() {
        {
            add(new Card(1, 0, 0, 1, 0, 0, 2, "Gus", null, Type.CREATURE, null, "Gus", false, "Gus.png", "Unglued", "This creature enters with a +1/+1 counter on it for each Magic game you have lost to one of your opponents since you last won a game against them.", 2, 2, Rarity.COMMON));
            add(new Card(2, 0, 0, 1, 0, 0, 2, "Shroofus Sproutsire", SuperType.LEGENDARY, Type.CREATURE, null, "Saproling", true, "Shroofus.png", "Foundations Jumpstart", "Trample    Whenever a Saproling you control deals combat damage to a player, create that many 1/1 green Saproling creature tokens.", 1, 1, Rarity.RARE));
        }
    };

    @Override
    public List<Card> getAllCards() {
        return cards;
    }

    @Override
    public Optional<Card> getCardById(int cardId) {
        for (Card card : cards){
            if (card.getCardId() == cardId){
                return Optional.of(card);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Card> findCardsByName(String name) {
        // Bliver ikke som sådan brugt i service så den returnere bare hele listen
        return cards;
    }
}
