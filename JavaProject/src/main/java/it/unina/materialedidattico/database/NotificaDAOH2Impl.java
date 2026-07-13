package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Notifica;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Implementazione H2 di NotificaDAO. */
public class NotificaDAOH2Impl implements NotificaDAO {

    @Override
    public void salva(Notifica notifica) {
        String sql = "INSERT INTO NOTIFICA (MESSAGGIO, DATA, LETTA, DESTINATARIO_ID, CONTENUTO_ID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnessione().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, notifica.getMessaggio());
            ps.setDate(2, Date.valueOf(notifica.getData()));
            ps.setBoolean(3, notifica.isLetta());
            ps.setInt(4, notifica.getDestinatario().getId());
            ps.setInt(5, notifica.getContenutoCorrelato().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    notifica.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della Notifica", e);
        }
    }

    private Connection getConnessione() {
        return DBManager.getIstanza().getConnessione();
    }
}
