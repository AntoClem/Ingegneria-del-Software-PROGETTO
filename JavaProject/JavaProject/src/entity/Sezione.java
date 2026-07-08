package entity;

import java.io.Serializable;

/**
 * Rappresenta una sezione (modulo didattico) all'interno di un corso,
 * usata dal docente per organizzare i contenuti pubblicati.
 */
public class Sezione implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String titolo;
    private int corsoId;

    public Sezione() {
    }

    public Sezione(int id, String titolo, int corsoId) {
        this.id = id;
        this.titolo = titolo;
        this.corsoId = corsoId;
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

    public int getCorsoId() {
        return corsoId;
    }

    public void setCorsoId(int corsoId) {
        this.corsoId = corsoId;
    }

    @Override
    public String toString() {
        return titolo;
    }
}
