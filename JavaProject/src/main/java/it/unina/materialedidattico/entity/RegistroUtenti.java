package it.unina.materialedidattico.entity;

import it.unina.materialedidattico.database.GestorePersistenza;

import java.util.Map;

/**
 * Facciata del layer Entity per l'accesso agli Utenti registrati.
 *
 * Nel modello di analisi la classe Piattaforma era l'Information Expert sulla
 * collezione degli Utenti (metodi esisteEmailIstituzionale, cercaUtentePerEmail,
 * aggiungiUtente emersi dai sequence diagram di Registrazione e Accesso). In
 * progettazione quella responsabilita' e' stata assegnata a questo Registro, che
 * la realizza appoggiandosi alla facciata del package Database: i Gestori del
 * package Control non conoscono quindi ne' JPQL ne' l'ORM sottostante.
 */
public class RegistroUtenti {

    private static RegistroUtenti istanza;

    private final GestorePersistenza gestorePersistenza;

    private RegistroUtenti() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public static synchronized RegistroUtenti getIstanza() {
        if (istanza == null) {
            istanza = new RegistroUtenti();
        }
        return istanza;
    }

    /** Registra un nuovo Utente (UC1). */
    public void registraUtente(Utente utente) {
        gestorePersistenza.salva(utente);
    }

    /** Aggiorna un Utente esistente, ad esempio dopo un tentativo di accesso fallito. */
    public Utente aggiorna(Utente utente) {
        return gestorePersistenza.aggiorna(utente);
    }

    /** Cerca un Utente per email istituzionale, in modo polimorfo su Studente e Docente (UC2). */
    public Utente cercaUtentePerEmail(String emailIstituzionale) {
        return gestorePersistenza.cercaPrimoPerCampi(Utente.class,
                Map.of("emailIstituzionale", emailIstituzionale));
    }

    /** True se l'email istituzionale risulta gia' associata a un account (RD07). */
    public boolean esisteEmailIstituzionale(String emailIstituzionale) {
        return cercaUtentePerEmail(emailIstituzionale) != null;
    }

    public Studente trovaStudentePerId(Long id) {
        return gestorePersistenza.trovaPerId(Studente.class, id);
    }

    public Docente trovaDocentePerId(Long id) {
        return gestorePersistenza.trovaPerId(Docente.class, id);
    }
}
