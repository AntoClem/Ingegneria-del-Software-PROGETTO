package database;

import entity.Notifica;
import java.util.List;

/**
 * Interfaccia DAO per l'accesso ai dati delle notifiche.
 */
public interface NotificaDAO {

    List<Notifica> findByStudente(int studenteId);

    List<Notifica> findNonLetteByStudente(int studenteId);

    int inserisci(Notifica notifica);

    void segnaComeLetta(int id);
}
