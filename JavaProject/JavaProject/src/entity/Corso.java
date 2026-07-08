package entity;

import java.io.Serializable;

/**
 * Rappresenta un corso gestito da un docente, identificato da un
 * codice univoco che gli studenti possono usare per l'iscrizione autonoma.
 */
public class Corso implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String titolo;
    private String descrizione;
    private String codiceUnivoco;
    private String annoAccademico; // opzionale
    private int docenteId;

    public Corso() {
    }

    public Corso(int id, String titolo, String descrizione, String codiceUnivoco,
                 String annoAccademico, int docenteId) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.codiceUnivoco = codiceUnivoco;
        this.annoAccademico = annoAccademico;
        this.docenteId = docenteId;
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

    public String getCodiceUnivoco() {
        return codiceUnivoco;
    }

    public void setCodiceUnivoco(String codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
    }

    public String getAnnoAccademico() {
        return annoAccademico;
    }

    public void setAnnoAccademico(String annoAccademico) {
        this.annoAccademico = annoAccademico;
    }

    public int getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(int docenteId) {
        this.docenteId = docenteId;
    }

    @Override
    public String toString() {
        return titolo + " [" + codiceUnivoco + "]";
    }
}
