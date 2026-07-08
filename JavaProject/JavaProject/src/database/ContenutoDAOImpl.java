package database;

import entity.Categoria;
import entity.Contenuto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JDBC di {@link ContenutoDAO} basata su H2, tramite {@link DBManager}.
 */
public class ContenutoDAOImpl implements ContenutoDAO {

    private final Connection connection;

    public ContenutoDAOImpl() {
        this.connection = DBManager.getInstance().getConnection();
    }

    @Override
    public Contenuto findById(int id) {
        String sql = "SELECT * FROM contenuti WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findById(Contenuto)", e);
        }
        return null;
    }

    @Override
    public List<Contenuto> findByCorso(int corsoId, Categoria categoria,
                                                 LocalDate data, Integer sezioneId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM contenuti WHERE corso_id = ? AND pubblicato = TRUE");
        List<Object> parametri = new ArrayList<>();
        parametri.add(corsoId);

        if (categoria != null) {
            sql.append(" AND categoria = ?");
            parametri.add(categoria.name());
        }
        if (data != null) {
            sql.append(" AND data_pubblicazione = ?");
            parametri.add(Date.valueOf(data));
        }
        if (sezioneId != null) {
            sql.append(" AND sezione_id = ?");
            parametri.add(sezioneId);
        }
        sql.append(" ORDER BY data_pubblicazione DESC");

        List<Contenuto> risultato = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametri.size(); i++) {
                ps.setObject(i + 1, parametri.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in findByCorso(Contenuto)", e);
        }
        return risultato;
    }

    @Override
    public List<Contenuto> findPubblicatiByCorso(int corsoId) {
        return findByCorso(corsoId, null, null, null);
    }

    @Override
    public List<Contenuto> ricerca(String testo) {
        List<Contenuto> risultato = new ArrayList<>();
        String sql = "SELECT * FROM contenuti WHERE pubblicato = TRUE AND " +
                     "(LOWER(titolo) LIKE ? OR LOWER(descrizione) LIKE ? OR LOWER(categoria) LIKE ?)";
        String pattern = "%" + testo.toLowerCase() + "%";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) risultato.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in ricerca(Contenuto)", e);
        }
        return risultato;
    }

    @Override
    public int contaContenutiPerCorso(int corsoId) {
        String sql = "SELECT COUNT(*) FROM contenuti WHERE corso_id = ? AND pubblicato = TRUE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in contaContenutiPerCorso", e);
        }
    }

    @Override
    public int inserisci(Contenuto contenuto) {
        String sql = "INSERT INTO contenuti (titolo, descrizione, categoria, data_pubblicazione, " +
                     "pubblicato, corso_id, sezione_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, contenuto.getTitolo());
            ps.setString(2, contenuto.getDescrizione());
            ps.setString(3, contenuto.getCategoria().name());
            ps.setDate(4, Date.valueOf(contenuto.getDataPubblicazione()));
            ps.setBoolean(5, contenuto.isPubblicato());
            ps.setInt(6, contenuto.getCorsoId());
            if (contenuto.getSezioneId() != null) {
                ps.setInt(7, contenuto.getSezioneId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore in inserisci(Contenuto)", e);
        }
        return -1;
    }

    @Override
    public void aggiorna(Contenuto contenuto) {
        String sql = "UPDATE contenuti SET titolo = ?, descrizione = ?, categoria = ?, " +
                     "pubblicato = ?, sezione_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, contenuto.getTitolo());
            ps.setString(2, contenuto.getDescrizione());
            ps.setString(3, contenuto.getCategoria().name());
            ps.setBoolean(4, contenuto.isPubblicato());
            if (contenuto.getSezioneId() != null) {
                ps.setInt(5, contenuto.getSezioneId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setInt(6, contenuto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in aggiorna(Contenuto)", e);
        }
    }

    @Override
    public void elimina(int id) {
        // Per semplicita' un contenuto eliminato non e' piu' visibile agli studenti:
        // si opta per una eliminazione fisica del record.
        String sql = "DELETE FROM contenuti WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore in elimina(Contenuto)", e);
        }
    }

    private Contenuto mapRow(ResultSet rs) throws SQLException {
        int sezioneId = rs.getInt("sezione_id");
        Integer sezione = rs.wasNull() ? null : sezioneId;
        return new Contenuto(
                rs.getInt("id"),
                rs.getString("titolo"),
                rs.getString("descrizione"),
                Categoria.valueOf(rs.getString("categoria")),
                rs.getDate("data_pubblicazione").toLocalDate(),
                rs.getBoolean("pubblicato"),
                rs.getInt("corso_id"),
                sezione
        );
    }
}
