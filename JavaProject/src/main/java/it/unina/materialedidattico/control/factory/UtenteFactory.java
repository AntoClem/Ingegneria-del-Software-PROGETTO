package it.unina.materialedidattico.control.factory;

import it.unina.materialedidattico.entity.Utente;

/**
 * Factory Method per la creazione degli Utenti (UC1).
 *
 * Il sequence diagram di analisi della Registrazione mostra che l'oggetto da creare
 * dipende dal ruolo dichiarato: isolare questa scelta in una gerarchia di factory
 * evita che il Gestore contenga un if sul ruolo per ogni operazione di creazione, e
 * permette di aggiungere in futuro un nuovo ruolo senza modificare il Gestore.
 */
public abstract class UtenteFactory {

    public abstract Utente creaUtente(String nome, String cognome,
                                      String emailIstituzionale, String password);

    /** Restituisce la factory concreta corrispondente al ruolo dichiarato. */
    public static UtenteFactory perRuolo(String ruolo) {
        if ("Studente".equalsIgnoreCase(ruolo)) {
            return new StudenteFactory();
        }
        if ("Docente".equalsIgnoreCase(ruolo)) {
            return new DocenteFactory();
        }
        throw new IllegalArgumentException("Ruolo non ammesso: deve essere Studente o Docente.");
    }
}
