package it.unina.materialedidattico.dto;

/**
 * Data Transfer Object di un Corso, usato per popolare gli elenchi delle Boundary.
 * Espone il nome del Docente titolare come stringa gia' composta, in modo che la
 * Boundary non debba navigare l'associazione Corso-Docente del modello di dominio.
 */
public class CorsoDTO {

    private final Long id;
    private final String titolo;
    private final String descrizione;
    private final String codiceUnivoco;
    private final String annoAccademico;
    private final String docenteTitolare;

    public CorsoDTO(Long id, String titolo, String descrizione, String codiceUnivoco,
                    String annoAccademico, String docenteTitolare) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.codiceUnivoco = codiceUnivoco;
        this.annoAccademico = annoAccademico;
        this.docenteTitolare = docenteTitolare;
    }

    public Long getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCodiceUnivoco() {
        return codiceUnivoco;
    }

    public String getAnnoAccademico() {
        return annoAccademico;
    }

    public String getDocenteTitolare() {
        return docenteTitolare;
    }

    @Override
    public String toString() {
        return titolo + " (" + codiceUnivoco + ")";
    }
}
