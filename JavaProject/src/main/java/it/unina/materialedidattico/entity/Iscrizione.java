package it.unina.materialedidattico.entity;

import java.time.LocalDate;

/**
 * Classe associativa (reificata) tra Studente e Corso.
 * La reificazione era gia' stata decisa nel modello di analisi
 * (l'associazione "si iscrive a" porta con se' l'attributo
 * dataIscrizione, che non appartiene ne' a Studente ne' a Corso).
 */
public class Iscrizione {

    private int id;
    private Studente studente;
    private Corso corso;
    private LocalDate dataIscrizione;

    public Iscrizione(Studente studente, Corso corso) {
        this.studente = studente;
        this.corso = corso;
        this.dataIscrizione = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
