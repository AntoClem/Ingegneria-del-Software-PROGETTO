package entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Notifica inviata a uno studente in seguito alla pubblicazione di nuovo
 * contenuto o di un avviso da parte del docente.
 *
 * Come da class diagram di analisi, non ha un riferimento diretto al corso:
 * "avviso" è una categoria di {@link Contenuto} come le altre, quindi il
 * corso di riferimento è sempre derivabile tramite contenutoId -> Contenuto -> Corso.
 */
public class Notifica implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String messaggio;
    private LocalDate data;
    private boolean letta;
    private int studenteId;      // destinatario
    private Integer contenutoId; // contenutoCorrelato

    public Notifica() {
    }

    public Notifica(int id, String messaggio, LocalDate data, boolean letta,
                     int studenteId, Integer contenutoId) {
        this.id = id;
        this.messaggio = messaggio;
        this.data = data;
        this.letta = letta;
        this.studenteId = studenteId;
        this.contenutoId = contenutoId;
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

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public boolean isLetta() {
        return letta;
    }

    public void setLetta(boolean letta) {
        this.letta = letta;
    }

    public int getStudenteId() {
        return studenteId;
    }

    public void setStudenteId(int studenteId) {
        this.studenteId = studenteId;
    }

    public Integer getContenutoId() {
        return contenutoId;
    }

    public void setContenutoId(Integer contenutoId) {
        this.contenutoId = contenutoId;
    }
}
