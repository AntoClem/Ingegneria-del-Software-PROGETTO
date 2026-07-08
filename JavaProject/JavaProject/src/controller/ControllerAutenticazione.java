package controller;

import database.UtenteDAO;
import database.UtenteDAOImpl;
import entity.Docente;
import entity.RuoloUtente;
import entity.Studente;
import entity.Utente;

/**
 * Controller responsabile della registrazione e dell'accesso degli utenti
 * (Docenti e Studenti) alla piattaforma.
 * Corrisponde ai casi d'uso UC: Registrazione, Accesso
 * (Piattaforma.registrazione(...) / Piattaforma.accesso(...) nel class diagram).
 */
public class ControllerAutenticazione {

    private final UtenteDAO utenteDAO;

    public ControllerAutenticazione() {
        this.utenteDAO = new UtenteDAOImpl();
    }

    /**
     * Registra un nuovo utente sulla piattaforma.
     *
     * @return l'id dell'utente creato
     * @throws IllegalArgumentException se email/username sono gia' in uso o i dati non sono validi
     */
    public int registrazione(String nome, String cognome, String emailIstituzionale,
                              RuoloUtente ruolo, String username, String password) {
        if (nome == null || nome.isBlank() || cognome == null || cognome.isBlank()) {
            throw new IllegalArgumentException("Nome e cognome sono obbligatori.");
        }
        if (emailIstituzionale == null || !emailIstituzionale.contains("@")) {
            throw new IllegalArgumentException("Indirizzo email non valido.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Lo username e' obbligatorio.");
        }
        if (utenteDAO.findByEmail(emailIstituzionale) != null) {
            throw new IllegalArgumentException("Esiste gia' un utente registrato con questa email.");
        }
        if (utenteDAO.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username gia' in uso.");
        }

        Utente nuovoUtente = (ruolo == RuoloUtente.DOCENTE)
                ? new Docente(0, nome, cognome, emailIstituzionale, username, password)
                : new Studente(0, nome, cognome, emailIstituzionale, username, password);

        return utenteDAO.inserisci(nuovoUtente);
    }

    /**
     * Verifica le credenziali (username + password) dell'utente.
     *
     * @return l'utente autenticato, oppure null se le credenziali non sono valide
     */
    public Utente accesso(String username, String password) {
        return utenteDAO.autentica(username, password);
    }
}
