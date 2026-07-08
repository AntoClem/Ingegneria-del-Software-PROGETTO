package database;

import entity.Utente;
import java.util.List;

/**
 * Interfaccia DAO per l'accesso ai dati degli utenti (Docenti e Studenti).
 */
public interface UtenteDAO {

    Utente findById(int id);

    Utente findByEmail(String emailIstituzionale);

    Utente findByUsername(String username);

    List<Utente> findAll();

    /** Verifica le credenziali (username + password) e restituisce l'utente se valide, altrimenti null. */
    Utente autentica(String username, String password);

    int inserisci(Utente utente);

    void aggiorna(Utente utente);

    void elimina(int id);
}
