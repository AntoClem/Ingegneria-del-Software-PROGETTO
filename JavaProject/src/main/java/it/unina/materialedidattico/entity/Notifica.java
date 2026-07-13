package it.unina.materialedidattico.entity;

import java.time.LocalDate;

/**
 * Messaggio inviato a uno Studente per informarlo della pubblicazione
 * di nuovo materiale.
 */
public class Notifica {

    private int id;
    private String messaggio;
    private LocalDate data;
    private boolean letta;
    private Studente destinatario;
    private Contenuto contenutoCorrelato;

    public Notifica(String messaggio, Studente destinatario, Contenuto contenutoCorrelato) {
        this.messaggio = messaggio;
        this.destinatario = destinatario;
        this.contenutoCorrelato = contenutoCorrelato;
        this.data = LocalDate.now();
        this.letta = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public LocalDate getData() {
        return data;
    }

    public boolean isLetta() {
        return letta;
    }

    public void setLetta(boolean letta) {
        this.letta = letta;
    }

    public Studente getDestinatario() {
        return destinatario;
    }

    public Contenuto getContenutoCorrelato() {
        return contenutoCorrelato;
    }
}
