package it.unina.materialedidattico.control;

import it.unina.materialedidattico.database.ContenutoDAO;
import it.unina.materialedidattico.database.ContenutoDAOH2Impl;
import it.unina.materialedidattico.database.IscrizioneDAO;
import it.unina.materialedidattico.database.IscrizioneDAOH2Impl;
import it.unina.materialedidattico.database.NotificaDAO;
import it.unina.materialedidattico.database.NotificaDAOH2Impl;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Notifica;
import it.unina.materialedidattico.entity.Sezione;
import it.unina.materialedidattico.entity.Studente;

/**
 * Use Case Controller del caso d'uso PubblicaContenuto (UC7).
 * La verifica di presenza dei campi obbligatori (titolo, descrizione,
 * categoria) e' demandata alla Boundary (Validazione.verificaDatiPubblicazione)
 * prima ancora che questo metodo venga invocato: se tale controllo
 * fallisce, il Controller non viene nemmeno chiamato (vedi ramo "dati
 * non validi" del diagramma di sequenza di analisi, ora gestito
 * direttamente nella Boundary a livello di progetto).
 */
public class PubblicaContenutoController {

    private final ContenutoDAO contenutoDAO = new ContenutoDAOH2Impl();
    private final IscrizioneDAO iscrizioneDAO = new IscrizioneDAOH2Impl();
    private final NotificaDAO notificaDAO = new NotificaDAOH2Impl();

    public Contenuto pubblicaContenuto(Corso corso, String titolo, String descrizione,
                                        Categoria categoria, Sezione sezione, boolean pubblicaSubito) {
        Contenuto contenuto = new Contenuto(titolo, descrizione, categoria, corso, sezione, pubblicaSubito);
        contenutoDAO.salva(contenuto);
        corso.aggiungiContenuto(contenuto);

        // «include» InviaNotifica: eseguito solo se il contenuto e' visibile immediatamente
        if (pubblicaSubito) {
            for (Studente iscritto : iscrizioneDAO.cercaIscrittiPerCorso(corso)) {
                Notifica notifica = new Notifica("Nuovo contenuto pubblicato: " + contenuto.getTitolo(), iscritto, contenuto);
                notificaDAO.salva(notifica);
            }
        }

        return contenuto;
    }
}
