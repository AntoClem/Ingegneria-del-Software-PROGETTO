package it.unina.materialedidattico.dto;

/**
 * Data Transfer Object di una Sezione, usato per popolare i menu a tendina delle
 * Boundary (scelta della Sezione in PubblicaContenuto, filtro in VisualizzaContenutiCorso).
 *
 * Nella versione precedente del progetto la Boundary interrogava direttamente il DAO
 * delle Sezioni: era una violazione del BCED, perche' la Boundary raggiungeva il layer
 * Database saltando il Control. Adesso le Sezioni arrivano dalla Facade come DTO.
 */
public class SezioneDTO {

    private final Long id;
    private final String titolo;

    public SezioneDTO(Long id, String titolo) {
        this.id = id;
        this.titolo = titolo;
    }

    public Long getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    @Override
    public String toString() {
        return titolo;
    }
}
