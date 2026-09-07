package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.UtenteDTO;

/**
 * Sessione di lavoro dell'Utente autenticato (V05).
 *
 * Sostituisce il menu della versione precedente del progetto, che "impersonava" un
 * utente prelevandolo dai dati di esempio: da qui in avanti nessuna schermata puo'
 * essere aperta senza che l'Accesso (UC2) sia andato a buon fine, e ogni caso d'uso
 * ricava da qui l'identita' e il ruolo di chi lo sta eseguendo.
 */
public class SessioneUtente {

    private static SessioneUtente istanza;

    private UtenteDTO utenteAutenticato;

    private SessioneUtente() {
    }

    public static synchronized SessioneUtente getIstanza() {
        if (istanza == null) {
            istanza = new SessioneUtente();
        }
        return istanza;
    }

    /** Apre la sessione al termine di un Accesso andato a buon fine. */
    public void apri(UtenteDTO utente) {
        this.utenteAutenticato = utente;
    }

    /** Chiude la sessione (logout). */
    public void chiudi() {
        this.utenteAutenticato = null;
    }

    public boolean isAperta() {
        return utenteAutenticato != null;
    }

    /**
     * Utente attualmente autenticato.
     * @throws IllegalStateException se nessun Utente ha effettuato l'accesso.
     */
    public UtenteDTO getUtenteAutenticato() {
        if (utenteAutenticato == null) {
            throw new IllegalStateException("Nessun Utente autenticato: effettuare l'accesso.");
        }
        return utenteAutenticato;
    }
}
