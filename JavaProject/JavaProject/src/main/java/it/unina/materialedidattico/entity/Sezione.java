package it.unina.materialedidattico.entity;

/**
 * Modulo didattico in cui il Docente organizza i Contenuti di un Corso.
 */
public class Sezione {

    private int id;
    private String titolo;
    private Corso corso;

    public Sezione(String titolo, Corso corso) {
        this.titolo = titolo;
        this.corso = corso;
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

    public Corso getCorso() {
        return corso;
    }
}
