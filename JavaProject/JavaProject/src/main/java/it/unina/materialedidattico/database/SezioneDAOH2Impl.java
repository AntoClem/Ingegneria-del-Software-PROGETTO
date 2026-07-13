package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementazione H2 di SezioneDAO. */
public class SezioneDAOH2Impl implements SezioneDAO {

    @Override
    public List<Sezione> cercaPerCorso(Corso corso) {
        String sql = "SELECT * FROM SEZIONE WHERE CORSO_ID = ?";
        List<Sezione> risultato = new ArrayList<>();
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sezione s = new Sezione(rs.getString("TITOLO"), corso);
                    s.setId(rs.getInt("ID"));
                    risultato.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca delle Sezioni del Corso", e);
        }
        return risultato;
    }

    @Override
    public void salva(Sezione sezione) {
        String sql = "INSERT INTO SEZIONE (TITOLO, CORSO_ID) VALUES (?, ?)";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sezione.getTitolo());
            ps.setInt(2, sezione.getCorso().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    sezione.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della Sezione", e);
        }
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}
