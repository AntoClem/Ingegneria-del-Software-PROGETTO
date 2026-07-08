package database;

import entity.Sezione;
import java.util.List;

/**
 * Interfaccia DAO per l'accesso ai dati delle sezioni (moduli didattici).
 */
public interface SezioneDAO {

    Sezione findById(int id);

    List<Sezione> findByCorso(int corsoId);

    int inserisci(Sezione sezione);

    void aggiorna(Sezione sezione);

    void elimina(int id);
}
