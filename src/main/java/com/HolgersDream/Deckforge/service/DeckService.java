package com.HolgersDream.Deckforge.service;

import com.HolgersDream.Deckforge.controller.DeckRequest;
import com.HolgersDream.Deckforge.domain.Card;
import com.HolgersDream.Deckforge.domain.Deck;
import com.HolgersDream.Deckforge.domain.DeckCard;
import com.HolgersDream.Deckforge.domain.Format;
import com.HolgersDream.Deckforge.domain.interfaces.ICardRepository;
import com.HolgersDream.Deckforge.domain.interfaces.IDeckRepository;
import com.HolgersDream.Deckforge.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeckService {

    private final IDeckRepository deckRepository;
    private final ICardRepository cardRepository;

    public DeckService(IDeckRepository deckRepository, ICardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
    }

    public List<Deck> getUserDecks(int userId){
        return deckRepository.findDecksById(userId);
    }

    public void checkAddDeck(DeckRequest deckRequest){
        Deck newDeck;
        try {
            newDeck = new Deck(0, deckRequest.getUserId(), 0, deckRequest.getDeckName(), deckRequest.getFormat());
        } catch (IllegalArgumentException iae){
            throw new DeckValidationException(iae.getMessage());
        }
        deckRepository.addDeckToUser(newDeck);
    }


    public Deck getPersonalDeckAndCards(int deckId, int userId){
        Deck personalDeck = handleGetSpecificDeck(deckId);
        checkDeckAccess(personalDeck, userId);
        return personalDeck;
    }

    public Deck getOnlyPersonalDeck(int deckId, int userId){
        Deck personalOnlyDeck = handleGetOnlyDeck(deckId);
        checkDeckAccess(personalOnlyDeck, userId);
        return personalOnlyDeck;
    }

    public Card getCardForDeck(int cardId){
        Optional<Card> card = cardRepository.getCardById(cardId);
        if (card.isPresent()) {
            return card.get();
        }
        throw new AddNonexistentDeckCardException("Kortet til deck blev ikke fundet");
    }

    public List<Card> getAllCardsForDeck(){
        return cardRepository.getAllCards();
    }

    public List<Card> getCardsForDeckByName(String name){
        return cardRepository.findCardsByName(name);
    }

    public Card checkAddCommanderToDeck(int deckId, int userId, int cardId){
        //Henter det tilhørende deck med kort i forhold til tjek af isCommander (f.eks på grund af format)
        Deck specificDeck = handleGetSpecificDeck(deckId);
        //Tjekker at brugeren har tilladelse til at ændre/se deck
        checkDeckAccess(specificDeck, userId);
        //Henter det nye kort
        Card newCard = getCardForDeck(cardId);
        //Tjekker om det nye kort kan være Commander i forhold til deck og kortet
        if (!checkAllowedToBeCommander(specificDeck, newCard)){
            throw new DeckCardValidationException("En commander kan ikke tilføjes til dette deck");
        }
        //Tjekker om der er max kort i deck
        checkMaxCards(specificDeck);
        deckRepository.addCardToDeck(specificDeck, newCard, true);
        return newCard;
    }

    public Card checkAddGenericToDeck(int deckId, int userId, int cardId){
        //Henter det tilhørende deck (f.eks på grund af format) (og kun deck da kort ikke er nødvendige)
        Deck specificDeck = handleGetOnlyDeck(deckId);
        //Tjekker at brugeren har tilladelse til at ændre/se deck
        checkDeckAccess(specificDeck, userId);
        //Henter det nye kort
        Card newCard = getCardForDeck(cardId);
        checkMaxCards(specificDeck);
        deckRepository.addCardToDeck(specificDeck, newCard, false);
        return newCard;
    }


    public DeckCard getDeckCard(int deckContainId, int deckId){
        DeckCard deckCard = handleGetSpecificDeckCard(deckContainId);
        checkDeckCardAccess(deckCard, deckId);
        return deckCard;
    }

    public DeckCard checkDeleteCardFromDeck(int deckId, int userId, int deckContainId){
        //Henter det tilhørende deck med kort i forhold til tjek om kortet eksisterer i listen
        Deck specificDeck = handleGetSpecificDeck(deckId);
        //Tjekker at brugeren har tilladelse til at ændre/se deck
        checkDeckAccess(specificDeck, userId);
        //Tjekker om deck-kortet eksisterer
        DeckCard deckCardForRemoval = getDeckCard(deckContainId, deckId);
        //Tjekker om deck-kortet er i listen

        // Efter flere test har vi imidlertid ikke fundet scenarier hvor "DeckcardRemoveException" kastes,
        // da den ellers dækkes ind af andre tjek.
        // Teoretisk kunne der være en edge-case, hvor anden data ikke længere er gyldig
        // og kunne redes af dette tjek, men yderst usandsynligt.
        if (!checkDeckCardExistsInList(specificDeck.getCards(), deckContainId)){
            throw new DeckCardRemoveException("Kortet der ville slettes blev ikke fundet");
        }
        deckRepository.deleteCardFromDeck(deckContainId);
        return deckCardForRemoval;
    }



    public boolean checkAllowedToBeCommander(Deck deck, Card newCard){
        if (deck.getFormat() != Format.COMMANDER){
            return false;
        }
        if (!newCard.isCanBeCommander()){
            return false;
        }
        //Tjekker om der allerede er en commander i decket
        for (DeckCard card : deck.getCards()){
            if (card.isCommander()){
                return false;
            }
        }

        return true;
    }

    private DeckCard handleGetSpecificDeckCard(int deckContainId){
        Optional<DeckCard> deckCardResult = deckRepository.findDeckCardById(deckContainId);
        if (deckCardResult.isEmpty()){
            throw new DeckCardNotFoundException("Der kunne ikke findes detaljer om dette kort i deck");
        } else {
            return deckCardResult.get();
        }
    }


    private Deck handleGetSpecificDeck(int deckId){
        Deck specificDeck = handleGetOnlyDeck(deckId);
        specificDeck.addAllCards(deckRepository.findDeckCards(deckId));
        return specificDeck;
    }

    private Deck handleGetOnlyDeck(int deckId){
        Optional<Deck> deckResult = deckRepository.findDeckById(deckId);
        if (deckResult.isEmpty()){
            throw new NoDeckFoundException("Deck'et du forsøgte at se detaljer om eksisterer ikke");
        } else {
            return deckResult.get();
        }
    }

    private void checkDeckAccess(Deck testDeck, int userId){
        if (testDeck.getUserId() != userId){
            throw new DeckAccessException("Du har ikke adgang til dette deck");
        }
    }

    private void checkDeckCardAccess(DeckCard deckCard, int deckId){
        if (deckCard.getDeckId() != deckId){
            throw new DeckAccessException("Du har ikke adgang til dette deck");
        }
    }

    private boolean checkDeckCardExistsInList(List<DeckCard> deckCards, int deckContainId){
        for (DeckCard deckCard : deckCards){
            if (deckCard.getDeckContainId() == deckContainId){
                return true;
            }
        }
        return false;
    }

    private void checkMaxCards(Deck testDeck){
        final int COMMANDER_MAX_CARDS = 100;
        final int ABSOLUTE_MAX_CARDS = 2000;
        Format testDeckFormat = testDeck.getFormat();
        int cardAmount = testDeck.getCardAmount();
        if (cardAmount >= ABSOLUTE_MAX_CARDS){
            throw new DeckCardValidationException("Et deck burde nok ikke have mere end " + ABSOLUTE_MAX_CARDS + " kort");
        }
        if (testDeckFormat == Format.COMMANDER && cardAmount >= COMMANDER_MAX_CARDS){
            throw new DeckCardValidationException("Et commander deck kan ikke have mere end " + COMMANDER_MAX_CARDS + " kort");
        }

    }


}
