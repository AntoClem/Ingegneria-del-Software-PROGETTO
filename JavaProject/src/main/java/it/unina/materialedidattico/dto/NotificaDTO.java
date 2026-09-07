package it.unina.materialedidattico.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object di una Notifica ricevuta dallo Studente.
 * Espone gia' appiattiti il titolo del materiale e quello del Corso, cosi' la Boundary
 * non deve navigare le associazioni Notifica-Contenuto-Corso del modello di dominio.
 */
public class NotificaDTO {

    private final Long id;
    private final String messaggio;
    private final LocalDate data;
    private final boolean letta;
    private final String titoloContenuto;
    private final String titoloCorso;

    public NotificaDTO(Long id, String messaggio, LocalDate data, boolean letta,
                       String titoloContenuto, String titoloCorso) {
        this.id = id;
        this.messaggio = messaggio;
        this.data = data;
        this.letta = letta;
        this.titoloContenuto = titoloContenuto;
        this.titoloCorso = titoloCorso;
    }

    public Long getId() {
        return id;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public LocalDate getData() {
        return data;
    }

    public boolean isLetta() {
        return letta;
    }

    public String getTitoloContenuto() {
        return titoloContenuto;
    }

    public String getTitoloCorso() {
        return titoloCorso;
    }
}
