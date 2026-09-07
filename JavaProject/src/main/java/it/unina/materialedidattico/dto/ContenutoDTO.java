package it.unina.materialedidattico.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object di un Contenuto didattico.
 * La Categoria e' esposta come stringa e non come enum del package entity: in questo
 * modo nessuna classe Boundary ha bisogno di importare tipi del layer Entity.
 */
public class ContenutoDTO {

    private final Long id;
    private final String titolo;
    private final String descrizione;
    private final String categoria;
    private final LocalDate dataPubblicazione;
    private final String sezione;
    private final String stato;

    public ContenutoDTO(Long id, String titolo, String descrizione, String categoria,
                        LocalDate dataPubblicazione, String sezione, String stato) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.dataPubblicazione = dataPubblicazione;
        this.sezione = sezione;
        this.stato = stato;
    }

    public Long getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public String getSezione() {
        return sezione;
    }

    /** Stato corrente del materiale: BOZZA, PUBBLICATO oppure RIMOSSO. */
    public String getStato() {
        return stato;
    }

    /** True se il materiale e' attualmente visibile agli Studenti. */
    public boolean isVisibile() {
        return "PUBBLICATO".equals(stato);
    }

    @Override
    public String toString() {
        return titolo + "  [" + categoria + "]";
    }
}
