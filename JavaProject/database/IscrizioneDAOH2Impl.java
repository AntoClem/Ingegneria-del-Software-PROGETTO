package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Iscrizione;
import it.unina.materialedidattico.entity.Studente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementazione H2 di IscrizioneDAO. */
public class IscrizioneDAOH2Impl implements IscrizioneDAO {

    private final StudenteDAO studenteDAO = new StudenteDAOH2Impl();

    @Override
    public boolean verificaEsistente(Studente studente, Corso corso) {
        String sql = "SELECT COUNT(*) FROM ISCRIZIONE WHERE STUDENTE_ID = ? AND CORSO_ID = ?";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, studente.getId());
            ps.setInt(2, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la verifica dell'Iscrizione esistente", e);
        }
    }

    @Override
    public void salva(Iscrizione iscrizione) {
        String sql = "INSERT INTO ISCRIZIONE (STUDENTE_ID, CORSO_ID, DATA_ISCRIZIONE) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, iscrizione.getStudente().getId());
            ps.setInt(2, iscrizione.getCorso().getId());
            ps.setDate(3, Date.valueOf(iscrizione.getDataIscrizione()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    iscrizione.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio dell'Iscrizione", e);
        }
    }

    @Override
    public int contaPerCorso(Corso corso) {
        String sql = "SELECT COUNT(*) FROM ISCRIZIONE WHERE CORSO_ID = ?";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il conteggio delle Iscrizioni del Corso", e);
        }
    }

    @Override
    public List<Studente> cercaIscrittiPerCorso(Corso corso) {
        String sql = "SELECT STUDENTE_ID FROM ISCRIZIONE WHERE CORSO_ID = ?";
        List<Studente> risultato = new ArrayList<>();
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(studenteDAO.cercaPerId(rs.getInt("STUDENTE_ID")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca degli iscritti al Corso", e);
        }
        return risultato;
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}
