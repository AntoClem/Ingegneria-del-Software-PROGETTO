package entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Reifica l'associazione molti-a-molti tra {@link Studente} e {@link Corso}.
 * Registra anche la modalità e la data di iscrizione.
 */
public class Iscrizione implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int studenteId;
    private int corsoId;
    private LocalDate dataIscrizione;
    private ModalitaIscrizione modalita;

    public Iscrizione() {
    }

    public Iscrizione(int id, int studenteId, int corsoId, LocalDate dataIscrizione,
                       ModalitaIscrizione modalita) {
        this.id = id;
        this.studenteId = studenteId;
        this.corsoId = corsoId;
        this.dataIscrizione = dataIscrizione;
        this.modalita = modalita;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudenteId() {
        return studenteId;
    }

    public void setStudenteId(int studenteId) {
        this.studenteId = studenteId;
    }

    public int getCorsoId() {
        return corsoId;
    }

    public void setCorsoId(int corsoId) {
        this.corsoId = corsoId;
    }

    public LocalDate getDataIscrizione() {
        return dataIscrizione;
    }

    public void setDataIscrizione(LocalDate dataIscrizione) {
        this.dataIscrizione = dataIscrizione;
    }

    public ModalitaIscrizione getModalita() {
        return modalita;
    }

    public void setModalita(ModalitaIscrizione modalita) {
        this.modalita = modalita;
    }

    /** Modalità con cui è avvenuta l'iscrizione dello studente al corso. */
    public enum ModalitaIscrizione {
        DIRETTA_DOCENTE,
        AUTONOMA_CODICE
    }
}
