package com.HolgersDream.Deckforge.exceptions;

//Tilføjet en ny slags exception for at være mere specifik
public class DeckCardNotFoundException extends RuntimeException {
    public DeckCardNotFoundException(String message) {
        super(message);
    }
}
