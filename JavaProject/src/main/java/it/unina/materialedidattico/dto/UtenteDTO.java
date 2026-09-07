package it.unina.materialedidattico.dto;

/**
 * Data Transfer Object dell'Utente autenticato o appena registrato.
 *
 * Il package dto e' esterno ai quattro layer del pattern BCED e ne e' al servizio:
 * appiattisce il grafo degli oggetti di dominio in strutture di soli tipi primitivi,
 * cosi' le operazioni che la Facade del Control espone alle Boundary non riportano
 * mai riferimenti alle classi Entity. E' cio' che permette di rispettare il vincolo
 * BCED per cui la Boundary non deve conoscere il layer Entity.
 */
public class UtenteDTO {

    private final Long id;
    private final String nome;
    private final String cognome;
    private final String emailIstituzionale;
    private final String ruolo;

    public UtenteDTO(Long id, String nome, String cognome, String emailIstituzionale, String ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.emailIstituzionale = emailIstituzionale;
        this.ruolo = ruolo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmailIstituzionale() {
        return emailIstituzionale;
    }

    public String getRuolo() {
        return ruolo;
    }

    public boolean isDocente() {
        return "Docente".equals(ruolo);
    }

    public boolean isStudente() {
        return "Studente".equals(ruolo);
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + ruolo + ")";
    }
}
