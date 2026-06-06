package com.HolgersDream.Deckforge.controller;

import com.HolgersDream.Deckforge.domain.*;
import com.HolgersDream.Deckforge.service.CollectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CollectionController {

    private final CollectionService service;
    private final SessionObjectRetriever sessionObjectRetriever;

    public CollectionController(CollectionService service, SessionObjectRetriever sessionObjectRetriever) {
        this.service = service;
        this.sessionObjectRetriever = sessionObjectRetriever;
    }


    @GetMapping("/collection/search")
    //@RequestParam(required = false) betyder at den ikke SKAL bruge name, det er kun til at søge
    public String showAndSearchCollection(@RequestParam(required = false) String name, HttpSession session, Model model) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        int userId = currentUser.getUserId();

        List<OwnedCard> cards;

        if (name == null || name.isBlank()) {
            // Vis hele samlingen
            cards = service.getUserCollection(userId);
        } else {
            // Søg i samlingen
            cards = service.findOwnedCardByName(userId, name);
        }

        model.addAttribute("cards", cards);

        return "/collection/search";
    }

    @GetMapping("/collection/add")
    public String showAddableCards(HttpSession session, Model model) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        List<Card> cards = service.getAllCards();

        model.addAttribute("cards", cards);

        return "collection/add";
    }

    @GetMapping("/collection/card/{cardId}")
    public String showCardDetails(@PathVariable int cardId, HttpSession session, Model model) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        Card card = service.getCardById(cardId); // henter kortet fra card_list
        model.addAttribute("card", card);
        return "/collection/detail-view-all";
    }

    @PostMapping("/collection/addCard")
    public String addCardToCollection(@RequestParam int cardId, HttpSession session) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        service.addCardToCollection(currentUser.getUserId(), cardId);

        return "redirect:/collection/search";
    }


    @GetMapping("/collection/owned/{ownedCardId}")
    public String showOwnedCardDetails(@PathVariable int ownedCardId, HttpSession session, Model model) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        OwnedCard ownedCard = service.getOwnedCardById(ownedCardId);
        model.addAttribute("ownedCard", ownedCard);

        return "/collection/detail-view-owned";
    }


    @PostMapping("/collection/remove/{ownedCardId}")
    public String removeOwnedCard(@PathVariable int ownedCardId, HttpSession session) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        service.removeCardFromCollection(ownedCardId, currentUser.getUserId());

        return "redirect:/collection/search";
    }

    @GetMapping("/collection/add/search")
    public String searchAllCards(@RequestParam(required = false) String name, HttpSession session, Model model) {
        AuthSessionUser currentUser = sessionObjectRetriever.getSessionUser(session);
        if (currentUser == null) {
            return "redirect:/authentication/login";
        }

        List<Card> cards;

        if (name == null || name.isBlank()) {
            // Vis hele samlingen
            cards = service.getAllCards();
        } else {
            // Søg i kort
            cards = service.findCardsByName(name);
        }

        model.addAttribute("cards", cards);

        return "/collection/add";
    }
}
