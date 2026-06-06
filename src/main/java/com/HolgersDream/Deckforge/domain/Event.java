package com.HolgersDream.Deckforge.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int eventId;
    private int ownerId;
    private String eventName;
    private int maxSlots;
    private int availableSlots;
    private String location;
    private LocalTime startTime;
    private LocalDate date;
    private List<User> participants;


    public Event(int eventId, int ownerId, String eventName, int maxSlots, int availableSlots, String location,
                 LocalTime startTime, LocalDate date) {
        if (eventId < 0){
            throw new IllegalArgumentException("Dette event id er ikke et gyldigt id til et event");
        }
        this.eventId = eventId;

        if (ownerId < 0){
            throw new IllegalArgumentException("Dette bruger id er ikke et gyldigt id til et event");
        }
        this.ownerId = ownerId;

        final int MAX_EVENT_NAME = 200;
        if (eventName == null || eventName.isBlank()){
            throw new IllegalArgumentException("Et event skal indeholde et navn");
        } else if (eventName.length() > MAX_EVENT_NAME) {
            throw new IllegalArgumentException("Et events navn kan maksimalt være " + MAX_EVENT_NAME + " tegn langt");
        }
        this.eventName = eventName;

        if (maxSlots < 0){
            throw new IllegalArgumentException("Et event kan ikke have negative maksimale pladser");
        }
        this.maxSlots = maxSlots;

        if (availableSlots < 0){
            throw new IllegalArgumentException("Et event kan ikke have et negativt antal ledige pladser");
        }
        this.availableSlots = availableSlots;

        final int MAX_LOCATION = 400;
        if (location == null || location.isBlank()){
            throw new IllegalArgumentException("Et event skal indeholde en lokation eller adresse");
        } else if (location.length() > MAX_LOCATION) {
            throw new IllegalArgumentException("Et events lokation kan maksimalt være " + MAX_LOCATION + " tegn langt");
        }
        this.location = location;

        if (startTime == null){
            throw new IllegalArgumentException("Et event skal have tildelt et startstidspunkt");
        }
        this.startTime = startTime;

        // Har fjernet "date.isBefore(LocalDate.now())",
        // da der så ikke kunne være fortidige events i systemet
        if (date == null){
            throw new IllegalArgumentException("Et event skal have en start dato");
        }
        this.date = date;

        this.participants = new ArrayList<>();
    }

    /// Getters
    public int getEventId() {
        return eventId;
    }

    public int getOwnerId(){
        return ownerId;
    }

    public String getEventName(){
        return eventName;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public String getLocation() {
        return location;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<User> getParticipants(){
        return participants;
    }


    // Tilføjet en metode til at tilføje listen af "User" til Event
    /// Others
    public void addAllParticipants(List<User> newParticipants){
        this.participants.addAll(newParticipants);
    }
}
