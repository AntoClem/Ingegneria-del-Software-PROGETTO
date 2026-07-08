package controller;

import database.CorsoDAO;
import database.CorsoDAOImpl;
import database.IscrizioneDAO;
import database.IscrizioneDAOImpl;
import database.SezioneDAO;
import database.SezioneDAOImpl;
import database.UtenteDAO;
import database.UtenteDAOImpl;
import entity.Corso;
import entity.Iscrizione;
import entity.Iscrizione.ModalitaIscrizione;
import entity.RuoloUtente;
import entity.Sezione;
import entity.Utente;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsabile della creazione dei corsi, della loro
 * organizzazione in sezioni e della gestione delle iscrizioni.
 * Corrisponde ai casi d'uso UC: CreaCorso, IscriviStudente, IscrizioneAutonoma, CreaSezione.
 */
public class ControllerGestioneCorsi {

    private final CorsoDAO corsoDAO;
    private final IscrizioneDAO iscrizioneDAO;
    private final SezioneDAO sezioneDAO;
    private final UtenteDAO utenteDAO;

    public ControllerGestioneCorsi() {
        this.corsoDAO = new CorsoDAOImpl();
        this.iscrizioneDAO = new IscrizioneDAOImpl();
        this.sezioneDAO = new SezioneDAOImpl();
        this.utenteDAO = new UtenteDAOImpl();
    }

    /** Crea un nuovo corso, gestito dal docente indicato. */
    public int creaCorso(int docenteId, String titolo, String descrizione,
                          String codiceUnivoco, String annoAccademico) {
        if (corsoDAO.findByCodiceUnivoco(codiceUnivoco) != null) {
            throw new IllegalArgumentException("Codice corso gia' in uso.");
        }
        Corso corso = new Corso(0, titolo, descrizione, codiceUnivoco, annoAccademico, docenteId);
        return corsoDAO.inserisci(corso);
    }

    /** Crea una nuova sezione (modulo didattico) all'interno di un corso. */
    public int creaSezione(int corsoId, String titolo) {
        return sezioneDAO.inserisci(new Sezione(0, titolo, corsoId));
    }

    /** Il docente iscrive direttamente uno studente al proprio corso. */
    public void iscriviStudenteDiretto(int docenteId, int studenteId, int corsoId) {
        Corso corso = validaProprietaCorso(docenteId, corsoId);
        validaStudente(studenteId);
        eseguiIscrizione(studenteId, corso.getId(), ModalitaIscrizione.DIRETTA_DOCENTE);
    }

    /** Lo studente si iscrive autonomamente fornendo il codice univoco del corso. */
    public void iscrizioneAutonoma(int studenteId, String codiceCorso) {
        Corso corso = corsoDAO.findByCodiceUnivoco(codiceCorso);
        if (corso == null) {
            throw new IllegalArgumentException("Nessun corso trovato con il codice fornito.");
        }
        validaStudente(studenteId);
        eseguiIscrizione(studenteId, corso.getId(), ModalitaIscrizione.AUTONOMA_CODICE);
    }

    private void eseguiIscrizione(int studenteId, int corsoId, ModalitaIscrizione modalita) {
        if (iscrizioneDAO.isIscritto(studenteId, corsoId)) {
            throw new IllegalArgumentException("Lo studente e' gia' iscritto a questo corso.");
        }
        Iscrizione iscrizione = new Iscrizione(0, studenteId, corsoId, LocalDate.now(), modalita);
        iscrizioneDAO.inserisci(iscrizione);
    }

    private Corso validaProprietaCorso(int docenteId, int corsoId) {
        Corso corso = corsoDAO.findById(corsoId);
        if (corso == null) {
            throw new IllegalArgumentException("Corso non trovato.");
        }
        if (corso.getDocenteId() != docenteId) {
            throw new IllegalArgumentException("Il corso non appartiene al docente indicato.");
        }
        return corso;
    }

    private void validaStudente(int studenteId) {
        Utente utente = utenteDAO.findById(studenteId);
        if (utente == null || utente.getRuolo() != RuoloUtente.STUDENTE) {
            throw new IllegalArgumentException("Utente non valido: non e' uno studente registrato.");
        }
    }

    public List<Sezione> visualizzaSezioni(int corsoId) {
        return sezioneDAO.findByCorso(corsoId);
    }

    public List<Corso> corsiDelDocente(int docenteId) {
        return corsoDAO.findByDocente(docenteId);
    }

    public List<Corso> corsiDelloStudente(int studenteId) {
        return corsoDAO.findByStudente(studenteId);
    }
}
