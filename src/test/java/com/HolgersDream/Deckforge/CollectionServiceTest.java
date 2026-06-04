package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.domain.Card;
import com.HolgersDream.Deckforge.exceptions.NoCollectionCardFoundException;
import com.HolgersDream.Deckforge.service.CollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionServiceTest {

    private CollectionService service;

    @BeforeEach
    void setUp() {
        //CollectionRepository bliver ikke brugt i test, men konstruktøren skal bruge 2 parametre
        service = new CollectionService(
                null,
                new FakeCardRepository()
        );
    }

    @Test
    void getCardById_shouldThrowWhenCardNotFound() {

        assertThrows(
                NoCollectionCardFoundException.class,
                () -> service.getCardById(1)
        );
    }

    @Test
    void getCardById_shouldReturnCardWhenCardExists() {

        Card card = service.getCardById(100);

        assertNotNull(card);
    }

    @Test
    void getCardById_success() {
        assertDoesNotThrow(() -> service.getCardById(100));
    }
}