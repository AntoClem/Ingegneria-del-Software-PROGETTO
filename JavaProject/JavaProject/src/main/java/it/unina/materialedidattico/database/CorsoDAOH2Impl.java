package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Docente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Implementazione H2 di CorsoDAO. */
public class CorsoDAOH2Impl implements CorsoDAO {

    private final DocenteDAO docenteDAO = new DocenteDAOH2Impl();

    @Override
    public Corso cercaPerCodice(String codiceUnivoco) {
        String sql = "SELECT * FROM CORSO WHERE CODICE_UNIVOCO = ?";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setString(1, codiceUnivoco);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del Corso per codice", e);
        }
    }

    @Override
    public Corso cercaPerId(int id) {
        String sql = "SELECT * FROM CORSO WHERE ID = ?";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mappaRiga(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la ricerca del Corso per id", e);
        }
    }

    @Override
    public void salva(Corso corso) {
        String sql = "INSERT INTO CORSO (TITOLO, DESCRIZIONE, CODICE_UNIVOCO, ANNO_ACCADEMICO, DOCENTE_ID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, corso.getTitolo());
            ps.setString(2, corso.getDescrizione());
            ps.setString(3, corso.getCodiceUnivoco());
            ps.setString(4, corso.getAnnoAccademico());
            ps.setInt(5, corso.getDocenteTitolare().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    corso.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del Corso", e);
        }
    }

    private Corso mappaRiga(ResultSet rs) throws SQLException {
        Docente docente = docenteDAO.cercaPerId(rs.getInt("DOCENTE_ID"));
        Corso c = new Corso(rs.getString("TITOLO"), rs.getString("DESCRIZIONE"),
                rs.getString("CODICE_UNIVOCO"), rs.getString("ANNO_ACCADEMICO"), docente);
        c.setId(rs.getInt("ID"));
        return c;
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}
