package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Classe associativa reificata fra Studente e Corso (RD03): l'associazione
 * "si iscrive a" porta con se' l'attributo dataIscrizione, che non appartiene
 * ne' allo Studente ne' al Corso.
 *
 * Il vincolo di unicita' sulla coppia (STUDENTE_ID, CORSO_ID) impedisce a
 * livello di database la doppia iscrizione allo stesso Corso.
 */
@Entity
@Table(name = "ISCRIZIONE",
        uniqueConstraints = @UniqueConstraint(name = "UQ_ISCRIZIONE",
                columnNames = {"STUDENTE_ID", "CORSO_ID"}))
public class Iscrizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "STUDENTE_ID", nullable = false)
    private Studente studente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CORSO_ID", nullable = false)
    private Corso corso;

    @Column(name = "DATA_ISCRIZIONE", nullable = false)
    private LocalDate dataIscrizione;

    /** Costruttore vuoto richiesto da JPA. */
    public Iscrizione() {
    }

    public Iscrizione(Studente studente, Corso corso) {
        this.studente = studente;
        this.corso = corso;
        this.dataIscrizione = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public Studente getStudente() {
        return studente;
    }

    public Corso getCorso() {
        return corso;
    }

    public LocalDate getDataIscrizione() {
        return dataIscrizione;
    }
}
