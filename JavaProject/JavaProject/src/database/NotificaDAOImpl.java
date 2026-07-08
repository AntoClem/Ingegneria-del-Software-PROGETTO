package database;

import entity.Notifica;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC di {@link NotificaDAO} basata su H2, tramite {@link DBManager}.
 */
public class NotificaDAOImpl implements NotificaDAO {

    private final Connection connection;

    public NotificaDAOImpl() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public List<Notifica> findByStudente(int studenteId) {
        List<Notifica> risultato = new ArrayList<>();
        String sql = "SELECT * FROM notifiche WHERE studente_id = ? ORDER BY data DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByStudente(Notifica)", e);
        }
        return risultato;
    }

    @Override
    public List<Notifica> findNonLetteByStudente(int studenteId) {
        List<Notifica> risultato = new ArrayList<>();
        String sql = "SELECT * FROM notifiche WHERE studente_id = ? AND letta = FALSE ORDER BY data DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findNonLetteByStudente", e);
        }
        return risultato;
    }

    @Override
    public int inserisci(Notifica n) {
        String sql = "INSERT INTO notifiche (messaggio, data, letta, studente_id, contenuto_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getMessaggio());
            ps.setDate(2, Date.valueOf(n.getData()));
            ps.setBoolean(3, n.isLetta());
            ps.setInt(4, n.getStudenteId());
            if (n.getContenutoId() != null) {
                ps.setInt(5, n.getContenutoId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in inserisci(Notifica)", e);
        }
        return -1;
    }

    @Override
    public void segnaComeLetta(int id) {
        String sql = "UPDATE notifiche SET letta = TRUE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in segnaComeLetta", e);
        }
    }

    private Notifica mapRow(ResultSet rs) throws SQLException {
        int contenutoId = rs.getInt("contenuto_id");
        Integer contenuto = rs.wasNull() ? null : contenutoId;
        return new Notifica(
                rs.getInt("id"),
                rs.getString("messaggio"),
                rs.getDate("data").toLocalDate(),
                rs.getBoolean("letta"),
                rs.getInt("studente_id"),
                contenuto
        );
    }
}
