package database;

import entity.Corso;
import java.util.List;

/**
 * Interfaccia DAO per l'accesso ai dati dei corsi.
 */
public interface CorsoDAO {

    Corso findById(int id);

    Corso findByCodiceUnivoco(String codice);

    List<Corso> findByDocente(int docenteId);

    /** Restituisce i corsi a cui uno studente e' iscritto. */
    List<Corso> findByStudente(int studenteId);

    List<Corso> findAll();

    int inserisci(Corso corso);

    void aggiorna(Corso corso);

    void elimina(int id);
}
