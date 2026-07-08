package controller;

import database.IscrizioneDAO;
import database.IscrizioneDAOImpl;
import database.NotificaDAO;
import database.NotificaDAOImpl;
import entity.Iscrizione;
import entity.Notifica;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsabile della creazione e consultazione delle notifiche
 * inviate agli studenti (nuovo contenuto pubblicato, avvisi del docente).
 * Corrisponde al caso d'uso UC: InvioNotifica (incluso in PubblicaContenuto).
 */
public class ControllerNotifiche {

    private final NotificaDAO notificaDAO;
    private final IscrizioneDAO iscrizioneDAO;

    public ControllerNotifiche() {
        this.notificaDAO = new NotificaDAOImpl();
        this.iscrizioneDAO = new IscrizioneDAOImpl();
    }

    /**
     * Notifica tutti gli studenti iscritti a un corso della pubblicazione
     * di un nuovo contenuto (o di un avviso). Il corso serve solo per
     * individuare gli iscritti: la Notifica stessa memorizza solo il
     * destinatario e il contenutoCorrelato (il corso e' derivabile da questo).
     */
    public void notificaStudentiIscritti(int corsoId, Integer contenutoId, String messaggio) {
        List<Iscrizione> iscritti = iscrizioneDAO.findByCorso(corsoId);
        for (Iscrizione iscrizione : iscritti) {
            Notifica notifica = new Notifica(0, messaggio, LocalDate.now(), false,
                    iscrizione.getStudenteId(), contenutoId);
            notificaDAO.inserisci(notifica);
        }
    }

    public List<Notifica> visualizzaNotifiche(int studenteId) {
        return notificaDAO.findByStudente(studenteId);
    }

    public List<Notifica> visualizzaNotificheNonLette(int studenteId) {
        return notificaDAO.findNonLetteByStudente(studenteId);
    }

    public void segnaComeLetta(int notificaId) {
        notificaDAO.segnaComeLetta(notificaId);
    }
}
