package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Studente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Implementazione H2 di StudenteDAO (tabella condivisa UTENTE, TIPO = 'STUDENTE'). */
public class StudenteDAOH2Impl implements StudenteDAO {

    @Override
    public Studente cercaPerUsername(String username) {
        String sql = "SELECT * FROM UTENTE WHERE USERNAME = ? AND TIPO = 'STUDENTE'";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca dello Studente per username", e);
        }
    }

    @Override
    public Studente cercaPerId(int id) {
        String sql = "SELECT * FROM UTENTE WHERE ID = ? AND TIPO = 'STUDENTE'";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca dello Studente per id", e);
        }
    }

    @Override
    public void salva(Studente studente) {
        String sql = "INSERT INTO UTENTE (NOME, COGNOME, EMAIL_ISTITUZIONALE, USERNAME, PASSWORD, TIPO) VALUES (?, ?, ?, ?, ?, 'STUDENTE')";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, studente.getNome());
            ps.setString(2, studente.getCognome());
            ps.setString(3, studente.getEmailIstituzionale());
            ps.setString(4, studente.getUsername());
            ps.setString(5, studente.getPassword());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    studente.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio dello Studente", e);
        }
    }

    private Studente mappaRiga(ResultSet rs) throws SQLException {
        Studente s = new Studente(rs.getString("NOME"), rs.getString("COGNOME"),
                rs.getString("EMAIL_ISTITUZIONALE"), rs.getString("USERNAME"), rs.getString("PASSWORD"));
        s.setId(rs.getInt("ID"));
        return s;
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}
