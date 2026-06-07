package com.HolgersDream.Deckforge.service;

import com.HolgersDream.Deckforge.controller.EventRequest;
import com.HolgersDream.Deckforge.domain.AuthSessionUser;
import com.HolgersDream.Deckforge.domain.Event;
import com.HolgersDream.Deckforge.domain.Role;
import com.HolgersDream.Deckforge.domain.User;
import com.HolgersDream.Deckforge.domain.interfaces.IEventRepository;
import com.HolgersDream.Deckforge.exceptions.EventValidationException;
import com.HolgersDream.Deckforge.exceptions.NoEventFoundException;
import com.HolgersDream.Deckforge.exceptions.NotAuthorizedException;
import com.HolgersDream.Deckforge.exceptions.ParticipateEventException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final IEventRepository repository;

    public EventService(IEventRepository repository){
        this.repository = repository;
    }

    public List<Event> findTheComingEvents(){
        return repository.findComingEvents();
    }

    public List<Event> findTheRegisteredEvents(int userId){
        return repository.findRegisteredEvents(userId);
    }

    public void checkAddEvent(EventRequest eventRequest, AuthSessionUser sessionUser){
        //Tjek/validering af tilladelse ved rolle
        Role role = sessionUser.getRole();
        if (role != Role.ORGANIZER && role != Role.ADMIN) {
            throw new NotAuthorizedException("Du har ikke tilladelse til at udføre denne handling");
        }

        // Tjek af domæneregler og et simpelt tjek om eventet oprettes til fortiden.
        Event newEvent;
        try {
            newEvent = new Event(0, sessionUser.getUserId(), eventRequest.getEventName(), eventRequest.getMaxSlots(), eventRequest.getMaxSlots(), eventRequest.getLocation(), eventRequest.getStartTime(), eventRequest.getDate());
        } catch (IllegalArgumentException iae){
            throw new EventValidationException(iae.getMessage());
        }
        if (newEvent.getDate().isBefore(LocalDate.now())){
            throw new EventValidationException("Et event kan ikke oprettes i fortiden");
        }
        repository.addNewEvent(newEvent);
    }

    public Event getSpecificEvent(int eventId){
        return handleGetSpecificEvent(eventId);
    }

    public Event checkJoinEvent(int userId, int eventId){
        Event retrievedEvent = handleGetSpecificEvent(eventId);
        //To tjek i forhold til om der er ledige pladser og om de allerede er tilmeldt.
        if (retrievedEvent.getAvailableSlots() <= 0){
            throw new ParticipateEventException("Der er desværre ikke ledige pladser tilbage til eventet " + retrievedEvent.getEventName());
        }
        if (checkAlreadyParticipant(userId, retrievedEvent.getParticipants())){
            throw new ParticipateEventException("Du er allerede tilmeldt eventet " + retrievedEvent.getEventName());
        }
        //Kald af en hjælpemetode til at tjekke eventet ikke allerede er holdt.
        checkDateOfEvent(retrievedEvent);
        repository.addUserToEvent(userId, eventId);
        return retrievedEvent;
    }


    public Event checkLeaveEvent(int userId, int eventId){
        Event retrievedEvent = handleGetSpecificEvent(eventId);
        //Et tjek af om de faktisk er tilmeldt dette event
        if (!checkAlreadyParticipant(userId, retrievedEvent.getParticipants())){
            throw new ParticipateEventException("Du er ikke tilmeldt eventet " + retrievedEvent.getEventName() + " og kan derfor ikke afmelde dig");
        }
        //Kald af en hjælpemetode til at tjekke eventet ikke allerede er holdt.
        checkDateOfEvent(retrievedEvent);
        repository.removeUserFromEvent(userId, eventId);
        return retrievedEvent;
    }


    public boolean checkAlreadyParticipant(int userId, List<User> participants){
        for (User participant : participants){
            if (participant.getUserId() == userId){
                return true;
            }
        }
        return false;
    }

    //En hjælpemetode til at finde et specifikt event og tjekke om det eksisterer,
    // samt at få fat på deltagerne
    private Event handleGetSpecificEvent(int eventId){
        Event specificEvent;
        Optional<Event> eventResult = repository.findEventById(eventId);
        if (eventResult.isEmpty()){
            throw new NoEventFoundException("Eventet du forsøgte at se detaljer om eksisterer ikke");
        } else {
            specificEvent = eventResult.get();
        }
        specificEvent.addAllParticipants(repository.findEventParticipants(eventId));
        return specificEvent;
    }

    //Meget simpel hjælpemetode til tjek af om et events dato er i fortiden.
    private void checkDateOfEvent(Event event){
        if (event.getDate().isBefore(LocalDate.now())){
            throw new ParticipateEventException("Eventet du tilgår er allerede forbi");
        }
    }
}
