package com.HolgersDream.Deckforge.repository;

import com.HolgersDream.Deckforge.config.DatabaseConfig;
import com.HolgersDream.Deckforge.domain.Event;
import com.HolgersDream.Deckforge.domain.Role;
import com.HolgersDream.Deckforge.domain.User;
import com.HolgersDream.Deckforge.domain.interfaces.IEventRepository;
import com.HolgersDream.Deckforge.exceptions.DataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MySQLEventRepository implements IEventRepository {

    private final DatabaseConfig databaseConfig;

    public MySQLEventRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }


    @Override
    public List<Event> findComingEvents() {
        String sql = """
                SELECT e.event_id, e.owner_id, e.event_name, e.max_slots, e.location, e.start_time, e.date, max_slots - count(upe.user_id) as available_slots
                FROM event e
                LEFT JOIN user_participate_event upe
                ON e.event_id = upe.event_id
                Where e.date >= current_date()
                GROUP BY e.event_id, e.owner_id, e.event_name, e.max_slots, e.location, e.start_time, e.date
                ORDER BY e.date;
                """;

        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            List<Event> events = new ArrayList<>();

            while (rs.next()) {
                events.add(mapEvent(rs));
            }

            return events;

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med databasen", sqle);
        }
    }

    @Override
    public List<Event> findRegisteredEvents(int userId) {
        // Tabellen event har først et INNER JOIN med user_participate_event,
        // hvor der filtreres fra de events som brugeren (userId) ikke deltager i.
        // Baseret på resultatet af det første join, bliver der gjort et LEFT JOIN,
        // igen med user_participate_event, hvor der nu tælles tilmeldte og udregnes ledige pladser.
        // WHERE sorterer fra de events som er i fortiden.
        String sql = """
                SELECT
                e.event_id, e.owner_id, e.event_name, e.max_slots, e.location, e.start_time,
                e.date, e.max_slots - COUNT(upe_left.user_id) AS available_slots
                FROM event e
                JOIN user_participate_event upe_in
                ON upe_in.event_id = e.event_id
                AND upe_in.user_id = ?
                LEFT JOIN user_participate_event upe_left
                ON e.event_id = upe_left.event_id
                WHERE e.date >= CURRENT_DATE()
                GROUP BY
                e.event_id, e.owner_id, e.event_name, e.max_slots,
                e.location, e.start_time, e.date
                ORDER BY e.date;
                """;

        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            List<Event> events = new ArrayList<>();

            while (rs.next()) {
                events.add(mapEvent(rs));
            }

            return events;

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med databasen", sqle);
        }
    }

    @Override
    public void addNewEvent(Event newEvent) {
        String sql = """
                INSERT INTO event (owner_id, event_name, max_slots, location, start_time, date)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, newEvent.getOwnerId());
            stmt.setString(2, newEvent.getEventName());
            stmt.setInt(3, newEvent.getMaxSlots());
            stmt.setString(4, newEvent.getLocation());
            stmt.setTime(5, Time.valueOf(newEvent.getStartTime()));
            stmt.setDate(6, Date.valueOf(newEvent.getDate()));

            stmt.executeUpdate();

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at oprette et nyt deck", sqle);
        }
    }

    @Override
    public Optional<Event> findEventById(int eventId) {
        // Skal bruge LEFT JOIN for at tælle deltagere for events som har 0 deltagere
        String sql = """
                SELECT e.event_id, e.owner_id, e.event_name, e.max_slots, e.location, e.start_time, e.date, max_slots - count(upe.user_id) as available_slots
                FROM event e
                LEFT JOIN user_participate_event upe
                ON e.event_id = upe.event_id
                Where e.event_id = ?
                GROUP BY e.event_id, e.owner_id, e.event_name, e.max_slots, e.location, e.start_time, e.date;
                """;

        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, eventId);


            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEvent(rs));
                }
                return Optional.empty();
            }


        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at finde oplysninger om det specifikke event", sqle);
        }
    }

    @Override
    public List<User> findEventParticipants(int eventId) {
        String sql = """
                SELECT u.user_id, name, role
                FROM user u
                JOIN user_participate_event upe
                ON u.user_id = upe.user_id
                WHERE upe.event_id = ?
                GROUP BY u.user_id, u.name, u.role;
                """;
        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            ResultSet rs = stmt.executeQuery();

            List<User> participants = new ArrayList<>();

            while (rs.next()) {
                participants.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        Role.valueOf(rs.getString("role"))
                ));
            }

            return participants;

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med databasen", sqle);
        }
    }

    @Override
    public void addUserToEvent(int userId, int eventId) {
        String sql = """
                INSERT INTO user_participate_event (user_id, event_id)
                VALUES (?, ?);
                """;
        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            stmt.executeUpdate();

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at tilmelde en bruger", sqle);
        }
    }

    @Override
    public void removeUserFromEvent(int userId, int eventId) {
        String sql = """
                DELETE FROM user_participate_event
                WHERE user_id = ? AND event_id = ?;
                """;
        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            stmt.executeUpdate();

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at tilmelde en bruger", sqle);
        }
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("event_id"),
                rs.getInt("owner_id"),
                rs.getString("event_name"),
                rs.getInt("max_slots"),
                rs.getInt("available_slots"),
                rs.getString("location"),
                LocalTime.parse(rs.getString("start_time")),
                LocalDate.parse(rs.getString("date"))
        );
    }
}

