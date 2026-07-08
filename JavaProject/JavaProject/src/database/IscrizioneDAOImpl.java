package database;

import entity.Iscrizione;
import entity.Iscrizione.ModalitaIscrizione;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC di {@link IscrizioneDAO} basata su H2, tramite {@link DBManager}.
 */
public class IscrizioneDAOImpl implements IscrizioneDAO {

    private final Connection connection;

    public IscrizioneDAOImpl() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public boolean isIscritto(int studenteId, int corsoId) {
        String sql = "SELECT COUNT(*) FROM iscrizioni WHERE studente_id = ? AND corso_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studenteId);
            ps.setInt(2, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in isIscritto", e);
        }
    }

    @Override
    public List<Iscrizione> findByCorso(int corsoId) {
        List<Iscrizione> risultato = new ArrayList<>();
        String sql = "SELECT * FROM iscrizioni WHERE corso_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByCorso(Iscrizione)", e);
        }
        return risultato;
    }

    @Override
    public List<Iscrizione> findByStudente(int studenteId) {
        List<Iscrizione> risultato = new ArrayList<>();
        String sql = "SELECT * FROM iscrizioni WHERE studente_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByStudente(Iscrizione)", e);
        }
        return risultato;
    }

    @Override
    public int inserisci(Iscrizione iscrizione) {
        String sql = "INSERT INTO iscrizioni (studente_id, corso_id, data_iscrizione, modalita) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, iscrizione.getStudenteId());
            ps.setInt(2, iscrizione.getCorsoId());
            ps.setDate(3, Date.valueOf(iscrizione.getDataIscrizione()));
            ps.setString(4, iscrizione.getModalita().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in inserisci(Iscrizione)", e);
        }
        return -1;
    }

    @Override
    public void elimina(int id) {
        String sql = "DELETE FROM iscrizioni WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in elimina(Iscrizione)", e);
        }
    }

    @Override
    public int contaIscrittiPerCorso(int corsoId) {
        String sql = "SELECT COUNT(*) FROM iscrizioni WHERE corso_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in contaIscrittiPerCorso", e);
        }
    }

    private Iscrizione mapRow(ResultSet rs) throws SQLException {
        return new Iscrizione(
                rs.getInt("id"),
                rs.getInt("studente_id"),
                rs.getInt("corso_id"),
                rs.getDate("data_iscrizione").toLocalDate(),
                ModalitaIscrizione.valueOf(rs.getString("modalita"))
        );
    }
}
