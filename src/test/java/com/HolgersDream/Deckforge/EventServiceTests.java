package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.controller.EventRequest;
import com.HolgersDream.Deckforge.domain.AuthSessionUser;
import com.HolgersDream.Deckforge.domain.Event;
import com.HolgersDream.Deckforge.domain.Role;
import com.HolgersDream.Deckforge.domain.interfaces.IEventRepository;
import com.HolgersDream.Deckforge.exceptions.EventValidationException;
import com.HolgersDream.Deckforge.exceptions.NoEventFoundException;
import com.HolgersDream.Deckforge.exceptions.NotAuthorizedException;
import com.HolgersDream.Deckforge.exceptions.ParticipateEventException;
import com.HolgersDream.Deckforge.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventServiceTests {
    private EventService service;
    private IEventRepository repository;

    @BeforeEach
    void setUp(){
        repository = new FakeEventRepository();
        service = new EventService(repository);
    }

    @Test
    void checkAddEvent_success(){
        EventRequest eventRequest = getValidEventRequest();

        AuthSessionUser sessionUser = new AuthSessionUser(1,
                "Placeholder", "Lorem@Ipsum.com",
                Role.ORGANIZER);

        assertDoesNotThrow(()->service.checkAddEvent(eventRequest, sessionUser));


        AuthSessionUser sessionUser2 = new AuthSessionUser(2,
                "Placeholder", "Lorem@Ipsum.com",
                Role.ADMIN);

        assertDoesNotThrow(()->service.checkAddEvent(eventRequest, sessionUser2));
    }

    @Test
    void checkAddEvent_shouldThrowWhenRoleNotAllowed(){
        EventRequest eventRequest = getValidEventRequest();

        AuthSessionUser sessionUser = new AuthSessionUser(1,
                "Placeholder", "Lorem@Ipsum.com",
                Role.USER);

        assertThrows(NotAuthorizedException.class, ()->service.checkAddEvent(eventRequest, sessionUser));
    }

    @Test
    void checkAddEvent_shouldThrowWhenDateInPast(){
        EventRequest eventRequest = getValidEventRequest();

        eventRequest.setDate(LocalDate.now().minusDays(1));

        AuthSessionUser sessionUser = new AuthSessionUser(1,
                "Placeholder", "Lorem@Ipsum.com",
                Role.ORGANIZER);

        assertThrows(EventValidationException.class, ()-> service.checkAddEvent(eventRequest, sessionUser));
    }

    @Test
    void checkJoinEvent_success(){
        // Tilføjer et event direkte, som der vil prøves at tilmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 120,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        assertDoesNotThrow(()-> service.checkJoinEvent
                (3,1));
    }

    @Test
    void checkJoinEvent_shouldThrowWhenNoEventFound(){
        // Tilføjer et event direkte, som der vil prøves at tilmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 120,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        // Søger efter et andet event
        assertThrows(NoEventFoundException.class, ()-> service.checkJoinEvent
                (3,2));
    }

    @Test
    void checkJoinEvent_shouldThrowWhenNoAvailableSlots(){
        // Tilføjer et event direkte, som der vil prøves at tilmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 0,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        assertThrows(ParticipateEventException.class, ()-> service.checkJoinEvent(3,1));
    }

    @Test
    void checkJoinEvent_shouldThrowWhenAlreadyJoined(){
        // Tilføjer et event direkte, som der vil prøves at tilmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 119,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        // Tilføjer brugeren direkte
        repository.addUserToEvent(3,1);

        assertThrows(ParticipateEventException.class, ()-> service.checkJoinEvent(3, 1));
    }

    @Test
    void checkJoinEvent_shouldThrowWhenEventInPast(){
        // Tilføjer et event direkte, som der vil prøves at tilmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 120,
                "Lorem Ipsum", LocalTime.NOON,
                LocalDate.now().minusDays(1)));

        assertThrows(ParticipateEventException.class, ()-> service.checkJoinEvent(3,1));
    }


    @Test
    void checkLeaveEvent_success(){
        // Tilføjer et event direkte, som der vil prøves at afmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 119,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        // Tilføjer brugeren direkte
        repository.addUserToEvent(3,1);

        assertDoesNotThrow(()->service.checkLeaveEvent(3,1));
    }

    @Test
    void checkLeaveEvent_shouldThrowWhenNoEventFound(){
        // Tilføjer et event direkte, som der vil prøves at afmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 120,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        // Søger efter et andet event
        assertThrows(NoEventFoundException.class, ()-> service.checkLeaveEvent
                (3,2));
    }



    @Test
    void checkLeaveEvent_shouldThrowWhenNotParticipant(){
        // Tilføjer et event direkte, som der vil prøves at afmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 119,
                "Lorem Ipsum", LocalTime.NOON, LocalDate.now()));

        // Tilføjer en anden bruger direkte
        repository.addUserToEvent(3,1);

        assertThrows(ParticipateEventException.class, ()-> service.checkLeaveEvent
                (2, 1));
    }

    @Test
    void checkLeaveEvent_shouldThrowWhenEventInPast(){
        // Tilføjer et event direkte, som der vil prøves at tilmeldes
        repository.addNewEvent(new Event(1, 1,
                "Placeholder", 120, 120,
                "Lorem Ipsum", LocalTime.NOON,
                LocalDate.now().minusDays(1)));

        // Tilføjer brugeren direkte
        repository.addUserToEvent(3,1);

        assertThrows(ParticipateEventException.class, ()-> service.checkLeaveEvent
                (3,1));
    }







    // Valid objects
    EventRequest getValidEventRequest(){
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEventName("Placeholder");
        eventRequest.setMaxSlots(120);
        eventRequest.setAvailableSlots(120);
        eventRequest.setLocation("Lorem Ipsum");
        eventRequest.setStartTime(LocalTime.NOON);
        eventRequest.setDate(LocalDate.now());
        return eventRequest;
    }
}
