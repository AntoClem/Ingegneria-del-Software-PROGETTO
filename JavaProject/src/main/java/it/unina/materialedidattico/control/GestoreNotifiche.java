package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.NotificaDTO;
import it.unina.materialedidattico.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestore del caso d'uso incluso InviaNotifica (UC21).
 *
 * Realizza l'interfaccia Observer del layer entity: invece di essere chiamato da
 * GestoreContenuti al termine della pubblicazione, viene avvisato dal Contenuto stesso
 * nel momento in cui diventa visibile. La logica di pubblicazione non deve piu'
 * ricordarsi di notificare, e un domani altri eventi di dominio potrebbero attivare
 * altri osservatori senza toccare il codice che li genera.
 *
 * Per il recapito effettivo dipende dall'astrazione ServizioNotifiche, realizzata nel
 * boundary da un adattatore verso il servizio di messaggistica (vincolo V04).
 */
public class GestoreNotifiche implements Observer {

    private static GestoreNotifiche istanza;

    private final RegistroContenuti registroContenuti = RegistroContenuti.getIstanza();
    private final RegistroCorsi registroCorsi = RegistroCorsi.getIstanza();
    private final RegistroUtenti registroUtenti = RegistroUtenti.getIstanza();

    /** Iniettato all'avvio dell'applicazione (si veda Main). */
    private ServizioNotifiche servizioNotifiche;

    private GestoreNotifiche() {
    }

    public static synchronized GestoreNotifiche getIstanza() {
        if (istanza == null) {
            istanza = new GestoreNotifiche();
        }
        return istanza;
    }

    /** Collega il servizio concreto di recapito, fornito dal boundary. */
    public void setServizioNotifiche(ServizioNotifiche servizioNotifiche) {
        this.servizioNotifiche = servizioNotifiche;
    }

    /**
     * Invocato dal Contenuto quando diventa visibile agli Studenti: crea e persiste una
     * Notifica per ciascun iscritto al Corso e ne chiede il recapito al servizio (RF22, RF23).
     */
    @Override
    public void aggiorna(Contenuto contenuto) {
        Corso corso = contenuto.getCorso();
        String messaggio = componiMessaggio(corso, contenuto);

        for (Studente iscritto : registroCorsi.cercaStudentiIscritti(corso)) {
            registroContenuti.registraNotifica(new Notifica(messaggio, iscritto, contenuto));

            if (servizioNotifiche != null) {
                servizioNotifiche.invia(iscritto.getEmailIstituzionale(),
                        "Nuovo materiale in " + corso.getTitolo(), messaggio);
            }
        }
    }

    /**
     * Elenco delle Notifiche ricevute dallo Studente (RF22, RF23), dalla piu' recente.
     *
     * @param idStudente id dello Studente destinatario
     */
    public List<NotificaDTO> visualizzaNotifiche(Long idStudente) {
        Studente studente = registroUtenti.trovaStudentePerId(idStudente);
        if (studente == null) {
            throw new IllegalArgumentException("Studente non trovato.");
        }

        List<NotificaDTO> risultato = new ArrayList<>();
        for (Notifica notifica : registroContenuti.cercaNotifichePerStudente(studente)) {
            risultato.add(toDTO(notifica));
        }
        return risultato;
    }

    /** Numero di Notifiche non ancora lette, mostrato nella schermata dello Studente. */
    public int contaNotificheNonLette(Long idStudente) {
        Studente studente = registroUtenti.trovaStudentePerId(idStudente);
        if (studente == null) {
            throw new IllegalArgumentException("Studente non trovato.");
        }
        return registroContenuti.cercaNotificheNonLette(studente).size();
    }

    /**
     * Segna una Notifica come letta.
     *
     * @param idNotifica id della Notifica aperta dallo Studente
     */
    public void segnaComeLetta(Long idNotifica) {
        Notifica notifica = registroContenuti.trovaNotificaPerId(idNotifica);
        if (notifica == null) {
            throw new IllegalArgumentException("Notifica non trovata.");
        }
        if (!notifica.isLetta()) {
            notifica.segnaComeLetta();
            registroContenuti.aggiornaNotifica(notifica);
        }
    }

    private NotificaDTO toDTO(Notifica notifica) {
        Contenuto contenuto = notifica.getContenutoCorrelato();
        String titoloContenuto = contenuto == null ? "-" : contenuto.getTitolo();
        String titoloCorso = (contenuto == null || contenuto.getCorso() == null)
                ? "-" : contenuto.getCorso().getTitolo();
        return new NotificaDTO(notifica.getId(), notifica.getMessaggio(), notifica.getData(),
                notifica.isLetta(), titoloContenuto, titoloCorso);
    }

    /**
     * Testo della Notifica. La composizione del messaggio e' logica applicativa e
     * appartiene al control: l'Entity Notifica conserva il testo gia' pronto, senza
     * sapere come sia stato costruito.
     */
    private String componiMessaggio(Corso corso, Contenuto contenuto) {
        return "Nuovo materiale pubblicato nel corso " + corso.getTitolo()
                + ": " + contenuto.getTitolo() + " (" + contenuto.getCategoria() + ").";
    }
}
