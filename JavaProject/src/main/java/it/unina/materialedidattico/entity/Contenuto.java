package it.unina.materialedidattico.entity;

import java.time.LocalDate;

/**
 * Materiale didattico pubblicato dal Docente all'interno di un Corso.
 */
public class Contenuto {

    private int id;
    private String titolo;
    private String descrizione;
    private Categoria categoria;
    private LocalDate dataPubblicazione;
    private boolean pubblicato;
    private Corso corso;
    private Sezione sezione;

    public Contenuto(String titolo, String descrizione, Categoria categoria, Corso corso, Sezione sezione, boolean pubblicato) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.corso = corso;
        this.sezione = sezione;
        this.dataPubblicazione = LocalDate.now();
        this.pubblicato = pubblicato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isPubblicato() {
        return pubblicato;
    }

    public Corso getCorso() {
        return corso;
    }

    public Sezione getSezione() {
        return sezione;
    }
}
