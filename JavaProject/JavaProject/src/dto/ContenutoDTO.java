package dto;

import java.time.LocalDate;

/**
 * Data Transfer Object usato per trasportare i dati di un contenuto
 * didattico dal livello Controller al livello Boundary, senza esporre
 * la struttura interna della classe Entity corrispondente.
 */
public class ContenutoDTO {

    private int id;
    private String titolo;
    private String descrizione;
    private String categoria;
    private LocalDate dataPubblicazione;
    private String sezione; // titolo della sezione, oppure null

    public ContenutoDTO(int id, String titolo, String descrizione, String categoria,
                         LocalDate dataPubblicazione, String sezione) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.dataPubblicazione = dataPubblicazione;
        this.sezione = sezione;
    }

    public int getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public String getSezione() {
        return sezione;
    }
}
