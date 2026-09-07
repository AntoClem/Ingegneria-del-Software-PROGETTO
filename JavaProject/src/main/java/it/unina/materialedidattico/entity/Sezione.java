package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

/**
 * Modulo didattico in cui il Docente organizza i Contenuti di un Corso (RF08).
 */
@Entity
@Table(name = "SEZIONE")
public class Sezione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titolo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CORSO_ID", nullable = false)
    private Corso corso;

    /** Costruttore vuoto richiesto da JPA. */
    public Sezione() {
    }

    public Sezione(String titolo, Corso corso) {
        this.titolo = titolo;
        this.corso = corso;
    }

    public Long getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Corso getCorso() {
        return corso;
    }

    @Override
    public String toString() {
        return titolo;
    }
}
