package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Messaggio recapitato a uno Studente quando viene pubblicato nuovo materiale
 * in un Corso a cui e' iscritto (RF22, RF23; caso d'uso incluso InviaNotifica).
 */
@Entity
@Table(name = "NOTIFICA")
public class Notifica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String messaggio;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private boolean letta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "DESTINATARIO_ID", nullable = false)
    private Studente destinatario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CONTENUTO_ID", nullable = false)
    private Contenuto contenutoCorrelato;

    /** Costruttore vuoto richiesto da JPA. */
    public Notifica() {
    }

    public Notifica(String messaggio, Studente destinatario, Contenuto contenutoCorrelato) {
        this.messaggio = messaggio;
        this.destinatario = destinatario;
        this.contenutoCorrelato = contenutoCorrelato;
        this.data = LocalDate.now();
        this.letta = false;
    }

    public void segnaComeLetta() {
        this.letta = true;
    }

    public Long getId() {
        return id;
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

    public Studente getDestinatario() {
        return destinatario;
    }

    public Contenuto getContenutoCorrelato() {
        return contenutoCorrelato;
    }
}
