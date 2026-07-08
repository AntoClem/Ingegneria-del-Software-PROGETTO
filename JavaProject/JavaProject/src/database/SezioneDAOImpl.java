package database;

import entity.Sezione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC di {@link SezioneDAO} basata su H2, tramite {@link DBManager}.
 */
public class SezioneDAOImpl implements SezioneDAO {

    private final Connection connection;

    public SezioneDAOImpl() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public Sezione findById(int id) {
        String sql = "SELECT * FROM sezioni WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findById(Sezione)", e);
        }
        return null;
    }

    @Override
    public List<Sezione> findByCorso(int corsoId) {
        List<Sezione> risultato = new ArrayList<>();
        String sql = "SELECT * FROM sezioni WHERE corso_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByCorso(Sezione)", e);
        }
        return risultato;
    }

    @Override
    public int inserisci(Sezione sezione) {
        String sql = "INSERT INTO sezioni (titolo, corso_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sezione.getTitolo());
            ps.setInt(2, sezione.getCorsoId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in inserisci(Sezione)", e);
        }
        return -1;
    }

    @Override
    public void aggiorna(Sezione sezione) {
        String sql = "UPDATE sezioni SET titolo = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, sezione.getTitolo());
            ps.setInt(2, sezione.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in aggiorna(Sezione)", e);
        }
    }

    @Override
    public void elimina(int id) {
        String sql = "DELETE FROM sezioni WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in elimina(Sezione)", e);
        }
    }

    private Sezione mapRow(ResultSet rs) throws SQLException {
        return new Sezione(rs.getInt("id"), rs.getString("titolo"), rs.getInt("corso_id"));
    }
}
