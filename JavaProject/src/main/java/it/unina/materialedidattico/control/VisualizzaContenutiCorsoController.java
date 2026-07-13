package it.unina.materialedidattico.control;

import it.unina.materialedidattico.database.ContenutoDAO;
import it.unina.materialedidattico.database.ContenutoDAOH2Impl;
import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case Controller del caso d'uso VisualizzaContenutiCorso (UC13).
 * Espone tre metodi, uno per ciascun passo del diagramma di sequenza
 * di progetto: elenco iniziale, applicazione filtri («include»
 * FiltraContenuti) e selezione del dettaglio di un singolo Contenuto.
 */
public class VisualizzaContenutiCorsoController {

    private final ContenutoDAO contenutoDAO = new ContenutoDAOH2Impl();

    public List<ContenutoDTO> visualizzaContenutiCorso(Corso corso) {
        return toDTO(contenutoDAO.cercaPerCorso(corso));
    }

    public List<ContenutoDTO> filtraContenuti(Corso corso, Categoria categoria, LocalDate data, Sezione sezione) {
        return toDTO(contenutoDAO.cercaConFiltri(corso, categoria, data, sezione));
    }

    public ContenutoDTO selezionaContenuto(int idContenuto) {
        Contenuto contenuto = contenutoDAO.cercaPerId(idContenuto);
        return contenuto != null ? new ContenutoDTO(contenuto) : null;
    }

    private List<ContenutoDTO> toDTO(List<Contenuto> contenuti) {
        List<ContenutoDTO> risultato = new ArrayList<>();
        for (Contenuto c : contenuti) {
            risultato.add(new ContenutoDTO(c));
        }
        return risultato;
    }
}
