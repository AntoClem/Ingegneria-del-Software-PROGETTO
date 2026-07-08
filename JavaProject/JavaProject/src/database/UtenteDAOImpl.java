package database;

import entity.Docente;
import entity.RuoloUtente;
import entity.Studente;
import entity.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC di {@link UtenteDAO} basata su H2, tramite {@link DBManager}.
 */
public class UtenteDAOImpl implements UtenteDAO {

    private final Connection connection;

    public UtenteDAOImpl() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public Utente findById(int id) {
        String sql = "SELECT * FROM utenti WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findById(Utente)", e);
        }
        return null;
    }

    @Override
    public Utente findByEmail(String emailIstituzionale) {
        String sql = "SELECT * FROM utenti WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, emailIstituzionale);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByEmail", e);
        }
        return null;
    }

    @Override
    public Utente findByUsername(String username) {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByUsername", e);
        }
        return null;
    }

    @Override
    public List<Utente> findAll() {
        List<Utente> risultato = new ArrayList<>();
        String sql = "SELECT * FROM utenti";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findAll(Utente)", e);
        }
        return risultato;
    }

    @Override
    public Utente autentica(String username, String password) {
        Utente u = findByUsername(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    @Override
    public int inserisci(Utente utente) {
        String sql = "INSERT INTO utenti (nome, cognome, email, username, password, ruolo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmailIstituzionale());
            ps.setString(4, utente.getUsername());
            ps.setString(5, utente.getPassword());
            ps.setString(6, utente.getRuolo().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in inserisci(Utente)", e);
        }
        return -1;
    }

    @Override
    public void aggiorna(Utente utente) {
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmailIstituzionale());
            ps.setInt(4, utente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in aggiorna(Utente)", e);
        }
    }

    @Override
    public void elimina(int id) {
        String sql = "DELETE FROM utenti WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in elimina(Utente)", e);
        }
    }

    /** Mappa una riga del ResultSet nella sottoclasse concreta corretta (Docente/Studente). */
    private Utente mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cognome = rs.getString("cognome");
        String email = rs.getString("email");
        String username = rs.getString("username");
        String password = rs.getString("password");
        RuoloUtente ruolo = RuoloUtente.valueOf(rs.getString("ruolo"));

        if (ruolo == RuoloUtente.DOCENTE) {
            return new Docente(id, nome, cognome, email, username, password);
        } else {
            return new Studente(id, nome, cognome, email, username, password);
        }
    }
}
