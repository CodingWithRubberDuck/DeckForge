package com.HolgersDream.Deckforge.exceptions;

//Ændret navn til et lidt mere passende navn på denne exception
public class AddNonexistentDeckCardException extends RuntimeException {
    public AddNonexistentDeckCardException(String message) {
        super(message);
    }
}
