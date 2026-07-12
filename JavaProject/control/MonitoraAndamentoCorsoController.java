package it.unina.materialedidattico.control;

import it.unina.materialedidattico.database.ContenutoDAO;
import it.unina.materialedidattico.database.ContenutoDAOH2Impl;
import it.unina.materialedidattico.database.IscrizioneDAO;
import it.unina.materialedidattico.database.IscrizioneDAOH2Impl;
import it.unina.materialedidattico.dto.RiepilogoAndamentoDTO;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;

import java.util.Map;

/**
 * Use Case Controller del caso d'uso MonitoraAndamentoCorso (UC15).
 * Se il Corso non ha ancora Contenuti pubblicati ne' Studenti iscritti,
 * le query restituiscono naturalmente 0 / una mappa vuota: non serve
 * alcun controllo esplicito, il riepilogo viene comunque costruito e
 * restituito (vedi nota nel diagramma di sequenza di progetto).
 */
public class MonitoraAndamentoCorsoController {

    private final ContenutoDAO contenutoDAO = new ContenutoDAOH2Impl();
    private final IscrizioneDAO iscrizioneDAO = new IscrizioneDAOH2Impl();

    public RiepilogoAndamentoDTO monitoraAndamentoCorso(Corso corso) {
        // «include» VisualizzaContenutiPubblicati
        int numeroContenuti = contenutoDAO.contaPerCorso(corso);

        int numeroStudenti = iscrizioneDAO.contaPerCorso(corso);

        // «include» VisualizzaDistribuzioneCategoria
        Map<Categoria, Integer> distribuzione = contenutoDAO.distribuzionePerCategoria(corso);

        return new RiepilogoAndamentoDTO(numeroContenuti, numeroStudenti, distribuzione);
    }
}
