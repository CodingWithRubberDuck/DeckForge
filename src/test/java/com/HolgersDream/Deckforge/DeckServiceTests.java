package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.domain.*;
import com.HolgersDream.Deckforge.domain.interfaces.ICardRepository;
import com.HolgersDream.Deckforge.domain.interfaces.IDeckRepository;
import com.HolgersDream.Deckforge.exceptions.*;
import com.HolgersDream.Deckforge.service.DeckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeckServiceTests {
    private DeckService service;
    private IDeckRepository deckRepository;
    private ICardRepository cardRepository;

    @BeforeEach
    void setUp(){
        deckRepository = new FakeDeckRepository();
        cardRepository = new FakeCardRepository();
        service = new DeckService(deckRepository, cardRepository);
    }

    @Test
    void checkAddCommanderToDeck_success(){
        int deckId = 1;
        int userId = 1;
        // cardId = 2 er et gyldigt commander kort
        int cardId = 2;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder", Format.COMMANDER));


        assertDoesNotThrow(()->service.checkAddCommanderToDeck(deckId, userId, cardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenNoDeckFound(){
        int deckId = 1;
        int nonexistentDeckId = 1093094;
        int userId = 1;
        // cardId = 2 er et gyldigt commander kort
        int cardId = 2;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder", Format.COMMANDER));

        assertThrows(NoDeckFoundException.class , ()->service.checkAddCommanderToDeck(nonexistentDeckId, userId, cardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenDeckNotBelong(){
        int deckId = 1;
        int userId = 1;
        int differentUserId = 234;
        // cardId = 2 er et gyldigt commander kort
        int cardId = 2;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder", Format.COMMANDER));

        assertThrows(DeckAccessException.class , ()->service.checkAddCommanderToDeck(deckId, differentUserId, cardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenNoCardFound(){
        int deckId = 1;
        int userId = 1;
        int nonexistentCardId = 9345439;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder", Format.COMMANDER));

        assertThrows(AddNonexistentDeckCardException.class , ()->service.checkAddCommanderToDeck(deckId, userId, nonexistentCardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenNotCommanderDeck(){
        int deckId = 1;
        int userId = 1;
        // cardId = 2 er et gyldigt commander kort
        int cardId = 2;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD));

        assertThrows(DeckCardValidationException.class , ()->service.checkAddCommanderToDeck(deckId, userId, cardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenCardNotAllowedToBeCommander(){
        int deckId = 1;
        int userId = 1;
        // cardId = 1 er IKKE et gyldigt commander kort
        int cardId = 1;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder",
                Format.COMMANDER));

        assertThrows(DeckCardValidationException.class , ()->service.checkAddCommanderToDeck(deckId, userId, cardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenDeckAlreadyHasCommander(){
        int deckId = 1;
        int userId = 1;
        // cardId = 2 er et gyldigt commander kort
        int cardId = 2;

        Deck deck = new Deck(deckId, userId,
                0, "Placeholder",
                Format.COMMANDER);

        //Commander Card
        Card commanderCard = new Card(2, 0, 0, 1, 0, 0, 2, "Shroofus Sproutsire", SuperType.LEGENDARY, Type.CREATURE, null, "Saproling", true, "Shroofus.png", "Foundations Jumpstart", "Trample    Whenever a Saproling you control deals combat damage to a player, create that many 1/1 green Saproling creature tokens.", 1, 1, Rarity.RARE);

        deckRepository.addDeckToUser(deck);
        //Tilføjer en commander til deck direkte.
        deckRepository.addCardToDeck(deck, commanderCard, true);

        assertThrows(DeckCardValidationException.class , ()->service.checkAddCommanderToDeck(deckId, userId, cardId));
    }

    @Test
    void checkAddCommanderToDeck_shouldThrowWhenDeckHaveMaxCardsCommander(){
        int deckId = 1;
        int userId = 1;
        // cardId = 2 er et gyldigt commander kort
        int cardId = 2;

        // Et Commander deck kan max have 100 kort
        deckRepository.addDeckToUser(new Deck(deckId, userId,
                100, "Placeholder",
                Format.COMMANDER));

        assertThrows(DeckCardValidationException.class , ()->service.checkAddCommanderToDeck(deckId, userId, cardId));
    }



    @Test
    void checkAddGenericToDeck_success(){
        int deckId = 1;
        int userId = 1;
        int cardId = 1;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD));

        assertDoesNotThrow(()->service.checkAddGenericToDeck(deckId, userId, cardId));
    }

    @Test
    void checkAddGenericToDeck_shouldThrowWhenNoDeckFound(){
        int deckId = 1;
        int nonexistentDeckId = 1093094;
        int userId = 1;
        int cardId = 1;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD));

        assertThrows(NoDeckFoundException.class , ()->service.checkAddGenericToDeck(nonexistentDeckId, userId, cardId));
    }

    @Test
    void checkAddGenericToDeck_shouldThrowWhenDeckNotBelong(){
        int deckId = 1;
        int userId = 1;
        int differentUser = 203;
        int cardId = 1;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD));

        assertThrows(DeckAccessException.class , ()->service.checkAddGenericToDeck(deckId, differentUser, cardId));
    }

    @Test
    void checkAddGenericToDeck_shouldThrowWhenNoCardFound(){
        int deckId = 1;
        int userId = 1;

        int nonexistentCardId = 2024902;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD));

        assertThrows(AddNonexistentDeckCardException.class , ()->service.checkAddGenericToDeck(deckId, userId, nonexistentCardId));
    }

    @Test
    void checkAddGenericToDeck_shouldThrowWhenHaveMaxCardsStandard(){
        int deckId = 1;
        int userId = 1;
        int cardId = 1;

        final int ABSOLUTE_MAX_CARDS = 2000;

        deckRepository.addDeckToUser(new Deck(deckId, userId,
                ABSOLUTE_MAX_CARDS, "Placeholder",
                Format.STANDARD));

        assertThrows(DeckCardValidationException.class , ()->service.checkAddGenericToDeck(deckId, userId, cardId));
    }



    @Test
    void checkDeleteCardFromDeck_success(){
        int deckId = 1;
        int userId = 1;
        int cardId = 1;

        Deck deck = new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD);

        deckRepository.addDeckToUser(deck);

        Card card;
        Optional<Card> result = cardRepository.getCardById(cardId);

        if (result.isPresent()){
            card = result.get();
        } else {
            throw new IllegalArgumentException("Det indtastede cardId eksisterer ikke");
        }

        deckRepository.addCardToDeck(deck,
                card, false);

        //Et nyt tilføjet kort til et deck i det falske repository
        // vil have et deckContainId på 1
        int deckContainId = 1;

        assertDoesNotThrow(()->service.checkDeleteCardFromDeck(deckId, userId, deckContainId));
    }

    @Test
    void checkDeleteCardFromDeck_shouldThrowWhenNoDeckFound(){
        int deckId = 1;
        int nonexistentDeckId = 1093094;
        int userId = 1;
        int cardId = 1;

        Deck deck = new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD);

        deckRepository.addDeckToUser(deck);

        Card card;
        Optional<Card> result = cardRepository.getCardById(cardId);

        if (result.isPresent()){
            card = result.get();
        } else {
            throw new IllegalArgumentException("Det indtastede cardId eksisterer ikke");
        }

        deckRepository.addCardToDeck(deck,
                card, false);

        //Et nyt tilføjet kort til et deck i det falske repository
        // vil have et deckContainId på 1
        int deckContainId = 1;

        assertThrows(NoDeckFoundException.class , ()->service.checkDeleteCardFromDeck(nonexistentDeckId, userId, deckContainId));
    }

    @Test
    void checkDeleteCardFromDeck_shouldThrowWhenDeckNotBelong(){
        int deckId = 1;
        int userId = 1;
        int differentUserId = 2353;
        int cardId = 1;

        Deck deck = new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD);

        deckRepository.addDeckToUser(deck);

        Card card;
        Optional<Card> result = cardRepository.getCardById(cardId);

        if (result.isPresent()){
            card = result.get();
        } else {
            throw new IllegalArgumentException("Det indtastede cardId eksisterer ikke");
        }

        deckRepository.addCardToDeck(deck,
                card, false);

        //Et nyt tilføjet kort til et deck i det falske repository
        // vil have et deckContainId på 1
        int deckContainId = 1;

        assertThrows(DeckAccessException.class , ()->service.checkDeleteCardFromDeck(deckId, differentUserId, deckContainId));
    }

    @Test
    void checkDeleteCardFromDeck_shouldThrowWhenDeckCardNotFound(){
        int deckId = 1;
        int userId = 1;
        int cardId = 1;

        Deck deck = new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD);

        deckRepository.addDeckToUser(deck);

        Card card;
        Optional<Card> result = cardRepository.getCardById(cardId);

        if (result.isPresent()){
            card = result.get();
        } else {
            throw new IllegalArgumentException("Det indtastede cardId eksisterer ikke");
        }

        deckRepository.addCardToDeck(deck,
                card, false);

        //Et nyt tilføjet kort til et deck i det falske repository
        // vil have et deckContainId på 1
        int nonexistentDeckContainId = 2345;

        assertThrows(DeckCardNotFoundException.class , ()->service.checkDeleteCardFromDeck(
                deckId, userId, nonexistentDeckContainId));
    }

    @Test
    void checkDeleteCardFromDeck_shouldThrowWhenDeckCardNotBelong(){
        int deckId = 1;
        int differentDeckId = 7;
        int userId = 1;
        int cardId = 1;

        // Deck som reelt har kortet
        Deck deck = new Deck(deckId, userId,
                0, "Placeholder",
                Format.STANDARD);

        deckRepository.addDeckToUser(deck);

        // Et reelt deck men ikke det deck som indeholder kortet
        deckRepository.addDeckToUser(new Deck(differentDeckId, userId,
                0, "Lorem Ipsum",
                Format.STANDARD));

        Card card;
        Optional<Card> result = cardRepository.getCardById(cardId);

        if (result.isPresent()){
            card = result.get();
        } else {
            throw new IllegalArgumentException("Det indtastede cardId eksisterer ikke");
        }

        deckRepository.addCardToDeck(deck,
                card, false);

        //Et nyt tilføjet kort til et deck i det falske repository
        // vil have et deckContainId på 1
        int deckContainId = 1;

        assertThrows(DeckAccessException.class , ()->service.checkDeleteCardFromDeck(
                differentDeckId, userId, deckContainId));
    }

}
