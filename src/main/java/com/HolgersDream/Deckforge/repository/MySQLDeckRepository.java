package com.HolgersDream.Deckforge.repository;

import com.HolgersDream.Deckforge.config.DatabaseConfig;
import com.HolgersDream.Deckforge.domain.*;
import com.HolgersDream.Deckforge.domain.interfaces.IDeckRepository;
import com.HolgersDream.Deckforge.exceptions.DataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MySQLDeckRepository implements IDeckRepository {

    private final DatabaseConfig databaseConfig;
    private final EnumCardMapper enumCardMapper;

    public MySQLDeckRepository(DatabaseConfig databaseConfig, EnumCardMapper enumCardMapper) {
        this.databaseConfig = databaseConfig;
        this.enumCardMapper = enumCardMapper;
    }

    @Override
    public List<Deck> findDecksById(int userId) {
        // Left join for at tælle kort selvom der er 0 kort til et deck, (vil stadig have et felt med 0)
        String sql = """
                SELECT d.deck_id, d.user_id, d.deck_name, d.format, count(dcc.card_id) as card_amount
                from deck d
                left JOIN deck_contain_card dcc
                 ON d.deck_id = dcc.deck_id
                WHERE user_id = ?
                group by d.deck_id, d.deck_name, d.format;
                """;


        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            List<Deck> decks = new ArrayList<>();

            while (rs.next()) {
                decks.add(mapDeck(rs));
            }
            return decks;

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at finde listen af deck", sqle);
        }
    }

    @Override
    public void addDeckToUser(Deck newDeck) {
        String sql = "INSERT INTO deck (user_id, deck_name, format) " +
                "VALUES (?, ?, ?)";

        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, newDeck.getUserId());
            stmt.setString(2, newDeck.getDeckName());
            stmt.setString(3, newDeck.getFormat().name());

            stmt.executeUpdate();

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at oprette et nyt deck", sqle);
        }
    }

    @Override
    public Optional<Deck> findDeckById(int deckId) {
        // Left join for at tælle kort selvom der er 0 kort til et deck, (vil stadig have et felt med 0)
        String sql = """
                SELECT d.deck_id, d.user_id, d.deck_name, d.format, count(dcc.card_id) as card_amount
                from deck d
                left JOIN deck_contain_card dcc
                 ON d.deck_id = dcc.deck_id
                WHERE d.deck_id = ?
                group by d.deck_id, d.deck_name, d.format;
                """;
        try (Connection c = databaseConfig.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {

            stmt.setInt(1, deckId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDeck(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at hente decket", sqle);
        }
    }

    @Override
    public List<DeckCard> findDeckCards(int deckId) {
        // tabellen deck_contain_card har et INNER JOIN med henholdsvis card_list med referencer til kort,
        // og deck som kortene tilhører
        String sql = """
                SELECT dcc.deck_id, dcc.deck_contain_id, dcc.is_commander, cl.*
                FROM deck_contain_card dcc
                JOIN card_list cl
                ON dcc.card_id = cl.card_id
                JOIN deck d
                ON dcc.deck_id = d.deck_id
                WHERE dcc.deck_id = ?
                ORDER BY is_commander DESC, name;
                """;
        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, deckId);

            List<DeckCard> cards = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    cards.add(mapDeckCard(rs));
                }
            }

            return cards;

        } catch (SQLException sqle){
            throw new DataAccessException("Der gik noget galt i forbindelse med at hente kort til decket", sqle);
        }
    }

    // Har ligeledes ændret til at passe til ændringer i interfacet
    @Override
    public void addCardToDeck(Deck deck, Card card, boolean isCommander) {
        String sql = """
                INSERT INTO deck_contain_card (deck_id, card_id, is_commander)
                VALUES (?, ?, ?);
                """;
        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, deck.getDeckId());
            stmt.setInt(2, card.getCardId());
            stmt.setBoolean(3, isCommander);

            stmt.executeUpdate();

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at tilføje en commander", sqle);
        }
    }

    @Override
    public Optional<DeckCard> findDeckCardById(int deckContainId) {
        // tabellen deck_contain_card har et INNER JOIN med henholdsvis card_list med referencer til kort,
        // og deck som kortene tilhører
        String sql = """
                SELECT dcc.deck_id, dcc.deck_contain_id, dcc.is_commander, cl.*
                FROM deck_contain_card dcc
                JOIN card_list cl
                ON dcc.card_id = cl.card_id
                JOIN deck d
                ON dcc.deck_id = d.deck_id
                WHERE dcc.deck_contain_id = ?;
                """;

        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, deckContainId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapDeckCard(rs));
            }
            return Optional.empty();

        } catch (SQLException sqle) {
            throw new DataAccessException("Kunne ikke hente kort detaljer", sqle);
        }
    }

    @Override
    public void deleteCardFromDeck(int deckContainId) {
        String sql = """
                DELETE FROM deck_contain_card
                WHERE deck_contain_id = ?;
                """;
        try (Connection con = databaseConfig.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, deckContainId);

            stmt.executeUpdate();

        } catch (SQLException sqle) {
            throw new DataAccessException("Der gik noget galt i forbindelse med at fjerne et kort", sqle);
        }
    }

    private Deck mapDeck(ResultSet rs) throws SQLException{
        return new Deck(
                rs.getInt("deck_id"),
                rs.getInt("user_id"),
                rs.getInt("card_amount"),
                rs.getString("deck_name"),
                Format.valueOf(rs.getString("format"))
        );
    }

    private DeckCard mapDeckCard(ResultSet rs) throws SQLException {
        return new DeckCard(
                rs.getInt("card_id"),
                rs.getInt("black_mana"),
                rs.getInt("blue_mana"),
                rs.getInt("green_mana"),
                rs.getInt("red_mana"),
                rs.getInt("white_mana"),
                rs.getInt("neutral_mana"),
                rs.getString("name"),
                enumCardMapper.checkSuperType(rs.getString("super_type")),
                Type.valueOf(rs.getString("card_type")),
                enumCardMapper.checkMultiType(rs.getString("multi_type")),
                rs.getString("sub_type"),
                rs.getBoolean("can_be_commander"),
                rs.getString("picture"),
                rs.getString("set_name"),
                rs.getString("rule_text"),
                rs.getInt("power"),
                rs.getInt("toughness"),
                Rarity.valueOf(rs.getString("rarity")),

                rs.getInt("deck_id"),
                rs.getInt("deck_contain_id"),
                rs.getBoolean("is_commander")
        );
    }
}
