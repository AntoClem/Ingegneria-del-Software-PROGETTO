package it.unina.materialedidattico.dto;

import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Contenuto;

import java.time.LocalDate;

/**
 * Data Transfer Object usato per mostrare un elenco di Contenuti nella
 * GUI (VisualizzaContenutiCorsoController) senza esporre alla Boundary
 * la classe Entity Contenuto e i suoi riferimenti a Corso/Sezione.
 * Package estraneo al pattern BCED, introdotto secondo quanto indicato
 * nel template (par. 5.5) per evitare accoppiamento eccessivo tra
 * Boundary ed Entity quando si devono visualizzare collezioni.
 */
public class ContenutoDTO {

    private final int id;
    private final String titolo;
    private final String descrizione;
    private final Categoria categoria;
    private final LocalDate dataPubblicazione;
    private final String sezione;

    public ContenutoDTO(Contenuto contenuto) {
        this.id = contenuto.getId();
        this.titolo = contenuto.getTitolo();
        this.descrizione = contenuto.getDescrizione();
        this.categoria = contenuto.getCategoria();
        this.dataPubblicazione = contenuto.getDataPubblicazione();
        this.sezione = contenuto.getSezione() != null ? contenuto.getSezione().getTitolo() : "-";
    }

    public int getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public String getSezione() {
        return sezione;
    }
}
