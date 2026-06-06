package com.HolgersDream.Deckforge;

import com.HolgersDream.Deckforge.domain.Event;
import com.HolgersDream.Deckforge.domain.Role;
import com.HolgersDream.Deckforge.domain.User;
import com.HolgersDream.Deckforge.domain.interfaces.IEventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeEventRepository implements IEventRepository {

    private List<Event> events = new ArrayList<>();
    private List<User> participants = new ArrayList<>();


    @Override
    public List<Event> findComingEvents() {
        return events;
    }

    @Override
    public List<Event> findRegisteredEvents(int userId) {
        return events;
    }

    @Override
    public void addNewEvent(Event event) {
        events.add(event);
    }

    @Override
    public Optional<Event> findEventById(int eventId) {
        for (Event event : events){
            if (event.getEventId() == eventId){
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findEventParticipants(int eventId) {
        return participants;
    }

    @Override
    public void addUserToEvent(int userId, int eventId) {
        participants.add(new User(userId, "Placeholder", Role.USER));
    }

    @Override
    public void removeUserFromEvent(int userId, int eventId) {
        User participant = findParticipant(userId);
        if (participant != null){
            participants.remove(participant);
        } else {
            throw new RuntimeException("Somethings wrong");
        }
    }

    private User findParticipant(int userId){
        for (User user : participants){
            if (user.getUserId() == userId){
                return user;
            }
        }
        return null;
    }

}
