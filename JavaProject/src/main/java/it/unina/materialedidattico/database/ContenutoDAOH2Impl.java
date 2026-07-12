package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Implementazione H2 di ContenutoDAO. */
public class ContenutoDAOH2Impl implements ContenutoDAO {

    private final SezioneDAO sezioneDAO = new SezioneDAOH2Impl();
    private final CorsoDAO corsoDAO = new CorsoDAOH2Impl();

    @Override
    public void salva(Contenuto contenuto) {
        String sql = "INSERT INTO CONTENUTO (TITOLO, DESCRIZIONE, CATEGORIA, DATA_PUBBLICAZIONE, PUBBLICATO, CORSO_ID, SEZIONE_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, contenuto.getTitolo());
            ps.setString(2, contenuto.getDescrizione());
            ps.setString(3, contenuto.getCategoria().name());
            ps.setDate(4, Date.valueOf(contenuto.getDataPubblicazione()));
            ps.setBoolean(5, contenuto.isPubblicato());
            ps.setInt(6, contenuto.getCorso().getId());
            if (contenuto.getSezione() != null) {
                ps.setInt(7, contenuto.getSezione().getId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    contenuto.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del Contenuto", e);
        }
    }

    @Override
    public Contenuto cercaPerId(int id) {
        String sql = "SELECT * FROM CONTENUTO WHERE ID = ?";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del Contenuto per id", e);
        }
    }

    @Override
    public List<Contenuto> cercaPerCorso(Corso corso) {
        String sql = "SELECT * FROM CONTENUTO WHERE CORSO_ID = ? AND PUBBLICATO = TRUE";
        List<Contenuto> risultato = new ArrayList<>();
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(mappaRiga(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca dei Contenuti del Corso", e);
        }
        return risultato;
    }

    @Override
    public List<Contenuto> cercaConFiltri(Corso corso, Categoria categoria, LocalDate data, Sezione sezione) {
        StringBuilder sql = new StringBuilder("SELECT * FROM CONTENUTO WHERE CORSO_ID = ? AND PUBBLICATO = TRUE");
        if (categoria != null) sql.append(" AND CATEGORIA = ?");
        if (data != null) sql.append(" AND DATA_PUBBLICAZIONE = ?");
        if (sezione != null) sql.append(" AND SEZIONE_ID = ?");

        List<Contenuto> risultato = new ArrayList<>();
        try (PreparedStatement ps = getConnessione().prepareStatement(sql.toString())) {
            int indice = 1;
            ps.setInt(indice++, corso.getId());
            if (categoria != null) ps.setString(indice++, categoria.name());
            if (data != null) ps.setDate(indice++, Date.valueOf(data));
            if (sezione != null) ps.setInt(indice++, sezione.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultato.add(mappaRiga(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca filtrata dei Contenuti", e);
        }
        return risultato;
    }

    @Override
    public int contaPerCorso(Corso corso) {
        String sql = "SELECT COUNT(*) FROM CONTENUTO WHERE CORSO_ID = ? AND PUBBLICATO = TRUE";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il conteggio dei Contenuti del Corso", e);
        }
    }

    @Override
    public Map<Categoria, Integer> distribuzionePerCategoria(Corso corso) {
        String sql = "SELECT CATEGORIA, COUNT(*) AS TOTALE FROM CONTENUTO WHERE CORSO_ID = ? AND PUBBLICATO = TRUE GROUP BY CATEGORIA";
        Map<Categoria, Integer> distribuzione = new EnumMap<>(Categoria.class);
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, corso.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    distribuzione.put(Categoria.valueOf(rs.getString("CATEGORIA")), rs.getInt("TOTALE"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il calcolo della distribuzione per Categoria", e);
        }
        return distribuzione;
    }

    private Contenuto mappaRiga(ResultSet rs) throws SQLException {
        Corso corso = corsoDAO.cercaPerId(rs.getInt("CORSO_ID"));
        Sezione sezione = null;
        int sezioneId = rs.getInt("SEZIONE_ID");
        if (!rs.wasNull()) {
            sezione = trovaSezionePerId(corso, sezioneId);
        }
        Contenuto c = new Contenuto(rs.getString("TITOLO"), rs.getString("DESCRIZIONE"),
                Categoria.valueOf(rs.getString("CATEGORIA")), corso, sezione, rs.getBoolean("PUBBLICATO"));
        c.setId(rs.getInt("ID"));
        return c;
    }

    private Sezione trovaSezionePerId(Corso corso, int sezioneId) {
        for (Sezione s : sezioneDAO.cercaPerCorso(corso)) {
            if (s.getId() == sezioneId) {
                return s;
            }
        }
        return null;
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}