package it.unina.materialedidattico.control;

import it.unina.materialedidattico.control.factory.UtenteFactory;
import it.unina.materialedidattico.dto.UtenteDTO;
import it.unina.materialedidattico.entity.RegistroUtenti;
import it.unina.materialedidattico.entity.Utente;

/**
 * Gestore dei casi d'uso Registrazione (UC1) e Accesso (UC2).
 *
 * Raccoglie le responsabilita' che la tabella delle responsabilita' raffinata
 * assegna alla Piattaforma. E' realizzato come Singleton perche' non ha stato
 * proprio da replicare: coordina il caso d'uso appoggiandosi al RegistroUtenti.
 *
 * Qui risiede la validazione dei dati: nella versione precedente del progetto i
 * controlli di formato stavano in un package utility invocato dalla Boundary, il
 * che collocava logica applicativa fuori dai quattro layer del BCED. Adesso la
 * Boundary si limita a raccogliere l'input e a mostrare l'esito.
 */
public class GestoreUtenti {

    /** RQ-SEC-01: tentativi consecutivi ammessi prima del blocco temporaneo. */
    private static final int MAX_TENTATIVI = 5;

    /** RQ-SEC-01: durata in minuti del blocco temporaneo dell'account. */
    private static final int MINUTI_BLOCCO = 15;

    /** Email istituzionale dell'ateneo: dominio unina.it o suoi sottodomini (RD07). */
    private static final String REGEX_EMAIL_ISTITUZIONALE = "^[^@\\s]+@([^@\\s]+\\.)*unina\\.it$";

    private static final int PASSWORD_LUNGHEZZA_MIN = 8;
    private static final int PASSWORD_LUNGHEZZA_MAX = 32;
    private static final int NOME_LUNGHEZZA_MAX = 50;

    private static GestoreUtenti istanza;

    private final RegistroUtenti registroUtenti = RegistroUtenti.getIstanza();

    private GestoreUtenti() {
    }

    public static synchronized GestoreUtenti getIstanza() {
        if (istanza == null) {
            istanza = new GestoreUtenti();
        }
        return istanza;
    }

    // ---------------------------------------------------------------- UC1

    /**
     * Registra un nuovo Utente con il ruolo dichiarato.
     *
     * @throws IllegalArgumentException se i dati non sono nel formato richiesto
     *                                  o se l'email risulta gia' registrata.
     */
    public UtenteDTO registrazione(String ruolo, String nome, String cognome,
                                   String emailIstituzionale, String password) {
        verificaValiditaDati(ruolo, nome, cognome, emailIstituzionale, password);

        if (registroUtenti.esisteEmailIstituzionale(emailIstituzionale)) {
            throw new IllegalArgumentException("Email istituzionale gia' associata a un account.");
        }

        Utente nuovoUtente = UtenteFactory.perRuolo(ruolo)
                .creaUtente(nome.trim(), cognome.trim(), emailIstituzionale.trim(), password);

        registroUtenti.registraUtente(nuovoUtente);
        return toDTO(nuovoUtente);
    }

    // ---------------------------------------------------------------- UC2

    /**
     * Autentica un Utente e apre la sessione di lavoro.
     *
     * Il messaggio di errore e' identico per email inesistente e password errata,
     * per non rivelare quali email risultino registrate (RQ-SEC-02).
     *
     * @throws IllegalArgumentException se le credenziali non sono nel formato richiesto
     * @throws SecurityException        se le credenziali non sono valide o l'account e' bloccato
     */
    public UtenteDTO accesso(String emailIstituzionale, String password) {
        verificaFormatoCredenziali(emailIstituzionale, password);

        Utente utente = registroUtenti.cercaUtentePerEmail(emailIstituzionale.trim());
        if (utente == null) {
            throw new SecurityException("Credenziali non valide.");
        }

        if (utente.isBloccato()) {
            throw new SecurityException("Account bloccato per troppi tentativi falliti. "
                    + "Riprovare tra " + MINUTI_BLOCCO + " minuti.");
        }

        if (!utente.verificaCredenziali(password)) {
            utente.incrementaTentativiFalliti();
            boolean appenaBloccato = utente.getTentativiFalliti() >= MAX_TENTATIVI;
            if (appenaBloccato) {
                utente.bloccaTemporaneamente(MINUTI_BLOCCO);
            }
            registroUtenti.aggiorna(utente);

            if (appenaBloccato) {
                throw new SecurityException("Credenziali non valide. Account bloccato per "
                        + MINUTI_BLOCCO + " minuti.");
            }
            throw new SecurityException("Credenziali non valide.");
        }

        utente.azzeraTentativiFalliti();
        registroUtenti.aggiorna(utente);

        UtenteDTO utenteDTO = toDTO(utente);
        SessioneUtente.getIstanza().apri(utenteDTO);
        return utenteDTO;
    }

    /** Chiude la sessione di lavoro dell'Utente autenticato. */
    public void logout() {
        SessioneUtente.getIstanza().chiudi();
    }

    // ---------------------------------------------------------------- validazione

    private void verificaValiditaDati(String ruolo, String nome, String cognome,
                                      String emailIstituzionale, String password) {
        richiedi(ruolo != null && !ruolo.isBlank(), "Il ruolo e' obbligatorio.");
        richiedi("Studente".equalsIgnoreCase(ruolo) || "Docente".equalsIgnoreCase(ruolo),
                "Il ruolo deve essere Studente o Docente.");
        validaNome(nome, "nome");
        validaNome(cognome, "cognome");
        validaEmail(emailIstituzionale);
        validaPassword(password);
    }

    /**
     * Controllo di formato delle credenziali in ingresso, eseguito prima di
     * interrogare il registro: un input malformato viene rifiutato subito e non
     * consuma un tentativo di accesso.
     */
    private void verificaFormatoCredenziali(String emailIstituzionale, String password) {
        validaEmail(emailIstituzionale);
        richiedi(password != null && !password.isEmpty(), "La password e' obbligatoria.");
    }

    private void validaNome(String valore, String etichetta) {
        richiedi(valore != null && !valore.isBlank(), "Il " + etichetta + " e' obbligatorio.");
        richiedi(valore.trim().length() <= NOME_LUNGHEZZA_MAX,
                "Il " + etichetta + " non puo' superare i " + NOME_LUNGHEZZA_MAX + " caratteri.");
        richiedi(valore.trim().matches("\\p{L}+([ '\\-]\\p{L}+)*"),
                "Formato del " + etichetta + " non valido: sono ammesse solo lettere.");
    }

    private void validaEmail(String emailIstituzionale) {
        richiedi(emailIstituzionale != null && !emailIstituzionale.isBlank(),
                "L'email istituzionale e' obbligatoria.");
        richiedi(emailIstituzionale.trim().matches(REGEX_EMAIL_ISTITUZIONALE),
                "Formato email non valido: e' richiesta un'email istituzionale unina.it.");
    }

    private void validaPassword(String password) {
        richiedi(password != null && password.length() >= PASSWORD_LUNGHEZZA_MIN,
                "La password deve contenere almeno " + PASSWORD_LUNGHEZZA_MIN + " caratteri.");
        richiedi(password.length() <= PASSWORD_LUNGHEZZA_MAX,
                "La password non puo' superare i " + PASSWORD_LUNGHEZZA_MAX + " caratteri.");
    }

    /** Guardia di validazione: lancia IllegalArgumentException se la condizione e' falsa. */
    private void richiedi(boolean condizione, String messaggio) {
        if (!condizione) {
            throw new IllegalArgumentException(messaggio);
        }
    }

    /** Converte l'Entity nel DTO restituito alla Boundary. */
    private UtenteDTO toDTO(Utente utente) {
        return new UtenteDTO(utente.getId(), utente.getNome(), utente.getCognome(),
                utente.getEmailIstituzionale(), utente.getRuolo());
    }
}
