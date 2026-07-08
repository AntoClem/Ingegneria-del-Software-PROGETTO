package entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Rappresenta un singolo contenuto didattico pubblicato dal docente
 * all'interno di un corso (ed eventualmente di una sezione).
 */
public class Contenuto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String titolo;
    private String descrizione;
    private Categoria categoria;
    private LocalDate dataPubblicazione;
    private boolean pubblicato;
    private int corsoId;
    private Integer sezioneId; // opzionale: può non appartenere a nessuna sezione

    public Contenuto() {
    }

    public Contenuto(int id, String titolo, String descrizione,
                               Categoria categoria, LocalDate dataPubblicazione,
                               boolean pubblicato, int corsoId, Integer sezioneId) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.dataPubblicazione = dataPubblicazione;
        this.pubblicato = pubblicato;
        this.corsoId = corsoId;
        this.sezioneId = sezioneId;
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

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(LocalDate dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public boolean isPubblicato() {
        return pubblicato;
    }

    public void setPubblicato(boolean pubblicato) {
        this.pubblicato = pubblicato;
    }

    public int getCorsoId() {
        return corsoId;
    }

    public void setCorsoId(int corsoId) {
        this.corsoId = corsoId;
    }

    public Integer getSezioneId() {
        return sezioneId;
    }

    public void setSezioneId(Integer sezioneId) {
        this.sezioneId = sezioneId;
    }

    @Override
    public String toString() {
        return titolo + " (" + categoria + ")";
    }

    /**
     * attiva() - come da class diagram di analisi: rende visibile agli
     * studenti un contenuto precedentemente non pubblicato.
     */
    public void attiva() {
        this.pubblicato = true;
    }

    /**
     * modifica(titolo, descrizione, categoria) - come da class diagram di analisi:
     * aggiorna le informazioni principali del contenuto.
     */
    public void modifica(String titolo, String descrizione, Categoria categoria) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
    }
}
