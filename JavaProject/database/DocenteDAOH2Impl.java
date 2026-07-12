package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Docente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione H2 di DocenteDAO.
 * Docente e Studente condividono la tabella UTENTE (strategia "una sola
 * tabella per tutta la gerarchia", vedi 4.1.2.4 Package Database):
 * la colonna TIPO discrimina le righe che rappresentano un Docente.
 */
public class DocenteDAOH2Impl implements DocenteDAO {

    @Override
    public Docente cercaPerUsername(String username) {
        String sql = "SELECT * FROM UTENTE WHERE USERNAME = ? AND TIPO = 'DOCENTE'";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del Docente per username", e);
        }
    }

    @Override
    public Docente cercaPerId(int id) {
        String sql = "SELECT * FROM UTENTE WHERE ID = ? AND TIPO = 'DOCENTE'";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del Docente per id", e);
        }
    }

    @Override
    public void salva(Docente docente) {
        String sql = "INSERT INTO UTENTE (NOME, COGNOME, EMAIL_ISTITUZIONALE, USERNAME, PASSWORD, TIPO) VALUES (?, ?, ?, ?, ?, 'DOCENTE')";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, docente.getNome());
            ps.setString(2, docente.getCognome());
            ps.setString(3, docente.getEmailIstituzionale());
            ps.setString(4, docente.getUsername());
            ps.setString(5, docente.getPassword());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    docente.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del Docente", e);
        }
    }

    private Docente mappaRiga(ResultSet rs) throws SQLException {
        Docente d = new Docente(rs.getString("NOME"), rs.getString("COGNOME"),
                rs.getString("EMAIL_ISTITUZIONALE"), rs.getString("USERNAME"), rs.getString("PASSWORD"));
        d.setId(rs.getInt("ID"));
        return d;
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}
