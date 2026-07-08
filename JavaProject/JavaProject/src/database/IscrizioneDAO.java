package database;

import entity.Iscrizione;
import java.util.List;

/**
 * Interfaccia DAO per l'accesso ai dati delle iscrizioni (Studente-Corso).
 */
public interface IscrizioneDAO {

    boolean isIscritto(int studenteId, int corsoId);

    List<Iscrizione> findByCorso(int corsoId);

    List<Iscrizione> findByStudente(int studenteId);

    int inserisci(Iscrizione iscrizione);

    void elimina(int id);

    /** Conta il numero di studenti iscritti a un corso. */
    int contaIscrittiPerCorso(int corsoId);
}
