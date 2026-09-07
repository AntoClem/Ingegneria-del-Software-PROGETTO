package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.SezioneDTO;
import it.unina.materialedidattico.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestore dei casi d'uso che riguardano i Corsi: IscrivitiCorso (UC6),
 * VisualizzaCorsiIscritti (UC12) e le operazioni di consultazione dei Corsi
 * gestiti da un Docente.
 *
 * Le condizioni di errore sono comunicate alla Boundary con IllegalArgumentException
 * e un messaggio gia' pronto da mostrare: la Boundary non deve interpretare codici di
 * esito ne' decidere il testo da presentare all'utente.
 */
public class GestoreCorsi {

    private static GestoreCorsi istanza;

    private final RegistroCorsi registroCorsi = RegistroCorsi.getIstanza();
    private final RegistroUtenti registroUtenti = RegistroUtenti.getIstanza();

    private GestoreCorsi() {
    }

    public static synchronized GestoreCorsi getIstanza() {
        if (istanza == null) {
            istanza = new GestoreCorsi();
        }
        return istanza;
    }

    // ---------------------------------------------------------------- UC6

    /**
     * Iscrive lo Studente al Corso individuato dal codice univoco.
     *
     * @throws IllegalArgumentException se il codice non e' nel formato corretto,
     *                                  se nessun Corso corrisponde al codice,
     *                                  o se lo Studente risulta gia' iscritto.
     */
    public CorsoDTO iscrivitiCorso(Long idStudente, String codiceCorso) {
        if (codiceCorso == null || codiceCorso.isBlank()) {
            throw new IllegalArgumentException("Il codice del Corso e' obbligatorio.");
        }
        String codice = codiceCorso.trim();
        if (!codice.matches("[A-Za-z0-9]{8,12}")) {
            throw new IllegalArgumentException(
                    "Il codice del Corso deve essere alfanumerico e lungo tra 8 e 12 caratteri.");
        }

        Studente studente = registroUtenti.trovaStudentePerId(idStudente);
        if (studente == null) {
            throw new IllegalArgumentException("Studente non trovato.");
        }

        Corso corso = registroCorsi.cercaCorsoPerCodice(codice);
        if (corso == null) {
            throw new IllegalArgumentException("Nessun Corso corrisponde al codice inserito.");
        }

        if (registroCorsi.esisteIscrizione(studente, corso)) {
            throw new IllegalArgumentException("Risulti gia' iscritto a questo Corso.");
        }

        Iscrizione iscrizione = new Iscrizione(studente, corso);
        registroCorsi.registraIscrizione(iscrizione);

        return toDTO(corso);
    }

    // ---------------------------------------------------------------- UC12

    /** Corsi a cui lo Studente risulta iscritto (RF13). */
    public List<CorsoDTO> visualizzaCorsiIscritti(Long idStudente) {
        Studente studente = registroUtenti.trovaStudentePerId(idStudente);
        if (studente == null) {
            throw new IllegalArgumentException("Studente non trovato.");
        }
        return toDTO(registroCorsi.cercaCorsiPerStudente(studente));
    }

    /** Corsi di cui il Docente e' titolare, per le schermate riservate al Docente. */
    public List<CorsoDTO> visualizzaCorsiGestiti(Long idDocente) {
        Docente docente = registroUtenti.trovaDocentePerId(idDocente);
        if (docente == null) {
            throw new IllegalArgumentException("Docente non trovato.");
        }
        return toDTO(registroCorsi.cercaCorsiPerDocente(docente));
    }

    /** Sezioni definite all'interno di un Corso, per i menu a tendina delle Boundary. */
    public List<SezioneDTO> visualizzaSezioni(Long idCorso) {
        Corso corso = trovaCorso(idCorso);
        List<SezioneDTO> risultato = new ArrayList<>();
        for (Sezione sezione : registroCorsi.cercaSezioniPerCorso(corso)) {
            risultato.add(new SezioneDTO(sezione.getId(), sezione.getTitolo()));
        }
        return risultato;
    }

    /**
     * Verifica che il Docente indicato sia titolare del Corso: garantisce la
     * pre-condizione dei casi d'uso riservati al Docente titolare (V05).
     */
    public void verificaTitolarita(Long idDocente, Long idCorso) {
        Docente docente = registroUtenti.trovaDocentePerId(idDocente);
        Corso corso = trovaCorso(idCorso);
        if (docente == null || !docente.isTitolareDi(corso)) {
            throw new IllegalArgumentException("Operazione consentita al solo Docente titolare del Corso.");
        }
    }

    /** Corso corrispondente all'id, con messaggio di errore uniforme se non esiste. */
    Corso trovaCorso(Long idCorso) {
        Corso corso = registroCorsi.trovaCorsoPerId(idCorso);
        if (corso == null) {
            throw new IllegalArgumentException("Corso non trovato.");
        }
        return corso;
    }

    private List<CorsoDTO> toDTO(List<Corso> corsi) {
        List<CorsoDTO> risultato = new ArrayList<>();
        for (Corso corso : corsi) {
            risultato.add(toDTO(corso));
        }
        return risultato;
    }

    private CorsoDTO toDTO(Corso corso) {
        Docente docente = corso.getDocenteTitolare();
        String nomeDocente = docente == null ? "-" : docente.getNome() + " " + docente.getCognome();
        return new CorsoDTO(corso.getId(), corso.getTitolo(), corso.getDescrizione(),
                corso.getCodiceUnivoco(), corso.getAnnoAccademico(), nomeDocente);
    }
}
