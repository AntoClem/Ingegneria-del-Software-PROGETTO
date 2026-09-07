package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Unico punto di accesso del layer Control per le Boundary (pattern Facade).
 *
 * Espone un'operazione per ciascun caso d'uso e delega ai quattro Gestori. Le firme
 * usano soltanto tipi primitivi e DTO: nessun tipo del package entity attraversa il
 * confine verso la Boundary, che quindi non conosce il modello di dominio ne', a
 * maggior ragione, la persistenza.
 *
 * E' realizzata come Singleton perche' non ha stato proprio: e' un punto di
 * smistamento verso i Gestori, anch'essi Singleton.
 */
public class PiattaformaFacade {

    private static PiattaformaFacade istanza;

    private PiattaformaFacade() {
    }

    public static synchronized PiattaformaFacade getIstanza() {
        if (istanza == null) {
            istanza = new PiattaformaFacade();
        }
        return istanza;
    }

    // ------------------------------------------------ GestoreUtenti (UC1, UC2)

    /**
     * UC1: registra un nuovo Utente.
     *
     * @param ruolo              "Studente" oppure "Docente"
     * @param nome               nome anagrafico
     * @param cognome            cognome anagrafico
     * @param emailIstituzionale email del dominio unina.it, univoca nel sistema
     * @param password           password di accesso (8-32 caratteri)
     */
    public UtenteDTO registrazione(String ruolo, String nome, String cognome,
                                   String emailIstituzionale, String password) {
        return GestoreUtenti.getIstanza().registrazione(ruolo, nome, cognome, emailIstituzionale, password);
    }

    /**
     * UC2: autentica l'Utente e apre la sessione di lavoro.
     *
     * @param emailIstituzionale email con cui l'Utente si e' registrato
     * @param password           password dell'account
     */
    public UtenteDTO accesso(String emailIstituzionale, String password) {
        return GestoreUtenti.getIstanza().accesso(emailIstituzionale, password);
    }

    /** Chiude la sessione dell'Utente autenticato. */
    public void logout() {
        GestoreUtenti.getIstanza().logout();
    }

    /** Utente attualmente autenticato; utile alle Boundary per intestare le schermate. */
    public UtenteDTO getUtenteAutenticato() {
        return SessioneUtente.getIstanza().getUtenteAutenticato();
    }

    // ------------------------------------------------ GestoreNotifiche (UC22)

    /**
     * UC22: elenco delle Notifiche ricevute dallo Studente, dalla piu' recente.
     *
     * @param idStudente id dello Studente destinatario
     */
    public List<NotificaDTO> visualizzaNotifiche(Long idStudente) {
        return GestoreNotifiche.getIstanza().visualizzaNotifiche(idStudente);
    }

    /**
     * Numero di Notifiche non ancora lette dallo Studente.
     *
     * @param idStudente id dello Studente destinatario
     */
    public int contaNotificheNonLette(Long idStudente) {
        return GestoreNotifiche.getIstanza().contaNotificheNonLette(idStudente);
    }

    /**
     * Segna come letta una Notifica aperta dallo Studente.
     *
     * @param idNotifica id della Notifica
     */
    public void segnaNotificaComeLetta(Long idNotifica) {
        GestoreNotifiche.getIstanza().segnaComeLetta(idNotifica);
    }

    // ------------------------------------------------ GestoreCorsi (UC6, UC12)

    /**
     * UC6: iscrive lo Studente al Corso individuato dal codice univoco.
     *
     * @param idStudente  id dello Studente che si sta iscrivendo
     * @param codiceCorso codice univoco comunicato dal Docente
     */
    public CorsoDTO iscrivitiCorso(Long idStudente, String codiceCorso) {
        return GestoreCorsi.getIstanza().iscrivitiCorso(idStudente, codiceCorso);
    }

    /**
     * UC12: elenco dei Corsi a cui lo Studente e' iscritto.
     *
     * @param idStudente id dello Studente
     */
    public List<CorsoDTO> visualizzaCorsiIscritti(Long idStudente) {
        return GestoreCorsi.getIstanza().visualizzaCorsiIscritti(idStudente);
    }

    /**
     * Elenco dei Corsi di cui il Docente e' titolare.
     *
     * @param idDocente id del Docente
     */
    public List<CorsoDTO> visualizzaCorsiGestiti(Long idDocente) {
        return GestoreCorsi.getIstanza().visualizzaCorsiGestiti(idDocente);
    }

    /**
     * Sezioni di un Corso, per i menu a tendina delle schermate.
     *
     * @param idCorso id del Corso
     */
    public List<SezioneDTO> visualizzaSezioni(Long idCorso) {
        return GestoreCorsi.getIstanza().visualizzaSezioni(idCorso);
    }

    // ------------------------------------------------ GestoreContenuti (UC7, UC13, UC14, UC15)

    /**
     * UC7: pubblica un nuovo Contenuto nel Corso; se reso visibile subito,
     * include l'invio delle Notifiche agli Studenti iscritti (UC21).
     *
     * @param idCorso        id del Corso in cui pubblicare
     * @param titolo         titolo del materiale
     * @param descrizione    breve descrizione del materiale
     * @param categoria      una fra le categorie restituite da getCategorieDisponibili()
     * @param idSezione      id della Sezione di appartenenza, oppure null
     * @param pubblicaSubito true per rendere il materiale immediatamente visibile
     */
    public ContenutoDTO pubblicaContenuto(Long idCorso, String titolo, String descrizione,
                                          String categoria, Long idSezione, boolean pubblicaSubito) {
        return GestoreContenuti.getIstanza()
                .pubblicaContenuto(idCorso, titolo, descrizione, categoria, idSezione, pubblicaSubito);
    }

    /**
     * UC9: rende visibile agli Studenti un materiale rimasto in bozza.
     *
     * @param idContenuto id del materiale da attivare
     */
    public ContenutoDTO attivaContenuto(Long idContenuto) {
        return GestoreContenuti.getIstanza().attivaContenuto(idContenuto);
    }

    /**
     * UC11: rimuove un materiale, che non sara' piu' visibile agli Studenti.
     *
     * @param idContenuto id del materiale da rimuovere
     */
    public ContenutoDTO rimuoviContenuto(Long idContenuto) {
        return GestoreContenuti.getIstanza().rimuoviContenuto(idContenuto);
    }

    /**
     * Elenco di tutti i materiali del Corso in qualunque stato, riservato al Docente
     * titolare per gestire bozze e rimozioni.
     *
     * @param idCorso id del Corso da gestire
     */
    public List<ContenutoDTO> visualizzaTuttiIContenuti(Long idCorso) {
        return GestoreContenuti.getIstanza().visualizzaTuttiIContenuti(idCorso);
    }

    /**
     * UC13: elenco dei Contenuti pubblicati nel Corso.
     *
     * @param idCorso id del Corso da consultare
     */
    public List<ContenutoDTO> visualizzaContenutiCorso(Long idCorso) {
        return GestoreContenuti.getIstanza().visualizzaContenutiCorso(idCorso);
    }

    /**
     * UC18: elenco dei Contenuti pubblicati che rispettano i filtri indicati.
     *
     * @param idCorso   id del Corso da consultare
     * @param categoria categoria richiesta, oppure null per non filtrare
     * @param data      data di pubblicazione richiesta, oppure null
     * @param idSezione id della Sezione richiesta, oppure null
     */
    public List<ContenutoDTO> filtraContenuti(Long idCorso, String categoria,
                                              LocalDate data, Long idSezione) {
        return GestoreContenuti.getIstanza().filtraContenuti(idCorso, categoria, data, idSezione);
    }

    /**
     * UC14: dettaglio di un singolo Contenuto.
     *
     * @param idContenuto id del Contenuto selezionato
     */
    public ContenutoDTO visualizzaDettaglioContenuto(Long idContenuto) {
        return GestoreContenuti.getIstanza().visualizzaDettaglioContenuto(idContenuto);
    }

    /**
     * UC16: ricerca dei Contenuti per titolo o descrizione.
     *
     * @param testo testo da cercare
     */
    public List<ContenutoDTO> cercaContenuti(String testo) {
        return GestoreContenuti.getIstanza().cercaContenuti(testo);
    }

    /**
     * UC15: riepilogo dell'andamento del Corso (include UC19 e UC20).
     *
     * @param idCorso id del Corso da monitorare
     */
    public RiepilogoAndamentoDTO monitoraAndamentoCorso(Long idCorso) {
        return GestoreContenuti.getIstanza().monitoraAndamentoCorso(idCorso);
    }

    /** Categorie ammesse per il materiale didattico. */
    public List<String> getCategorieDisponibili() {
        return GestoreContenuti.getIstanza().getCategorieDisponibili();
    }
}
