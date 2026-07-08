package database;

import entity.Corso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC di {@link CorsoDAO} basata su H2, tramite {@link DBManager}.
 */
public class CorsoDAOImpl implements CorsoDAO {

    private final Connection connection;

    public CorsoDAOImpl() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public Corso findById(int id) {
        String sql = "SELECT * FROM corsi WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findById(Corso)", e);
        }
        return null;
    }

    @Override
    public Corso findByCodiceUnivoco(String codice) {
        String sql = "SELECT * FROM corsi WHERE codice_univoco = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codice);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByCodiceUnivoco", e);
        }
        return null;
    }

    @Override
    public List<Corso> findByDocente(int docenteId) {
        List<Corso> risultato = new ArrayList<>();
        String sql = "SELECT * FROM corsi WHERE docente_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, docenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByDocente", e);
        }
        return risultato;
    }

    @Override
    public List<Corso> findByStudente(int studenteId) {
        List<Corso> risultato = new ArrayList<>();
        String sql = "SELECT c.* FROM corsi c " +
                     "JOIN iscrizioni i ON c.id = i.corso_id " +
                     "WHERE i.studente_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByStudente", e);
        }
        return risultato;
    }

    @Override
    public List<Corso> findAll() {
        List<Corso> risultato = new ArrayList<>();
        String sql = "SELECT * FROM corsi";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) risultato.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findAll(Corso)", e);
        }
        return risultato;
    }

    @Override
    public int inserisci(Corso corso) {
        String sql = "INSERT INTO corsi (titolo, descrizione, codice_univoco, anno_accademico, docente_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, corso.getTitolo());
            ps.setString(2, corso.getDescrizione());
            ps.setString(3, corso.getCodiceUnivoco());
            ps.setString(4, corso.getAnnoAccademico());
            ps.setInt(5, corso.getDocenteId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in inserisci(Corso)", e);
        }
        return -1;
    }

    @Override
    public void aggiorna(Corso corso) {
        String sql = "UPDATE corsi SET titolo = ?, descrizione = ?, anno_accademico = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, corso.getTitolo());
            ps.setString(2, corso.getDescrizione());
            ps.setString(3, corso.getAnnoAccademico());
            ps.setInt(4, corso.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in aggiorna(Corso)", e);
        }
    }

    @Override
    public void elimina(int id) {
        String sql = "DELETE FROM corsi WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in elimina(Corso)", e);
        }
    }

    private Corso mapRow(ResultSet rs) throws SQLException {
        return new Corso(
                rs.getInt("id"),
                rs.getString("titolo"),
                rs.getString("descrizione"),
                rs.getString("codice_univoco"),
                rs.getString("anno_accademico"),
                rs.getInt("docente_id")
        );
    }
}
