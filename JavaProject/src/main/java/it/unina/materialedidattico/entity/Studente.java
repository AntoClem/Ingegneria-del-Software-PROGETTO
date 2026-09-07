package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Utente che si iscrive ai Corsi e ne consulta il materiale didattico.
 */
@Entity
@DiscriminatorValue("STUDENTE")
public class Studente extends Utente {

    /**
     * Lato inverso dell'associazione con Iscrizione: serve al mapping ORM e a
     * navigare i Corsi seguiti a partire dallo Studente (RF13).
     */
    @OneToMany(mappedBy = "studente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Iscrizione> iscrizioni = new ArrayList<>();

    /** Costruttore vuoto richiesto da JPA: l'istanza nasce da StudenteFactory (UC1). */
    public Studente() {
    }

    public Studente(String nome, String cognome, String emailIstituzionale, String password) {
        super(nome, cognome, emailIstituzionale, password);
    }

    @Override
    public String getRuolo() {
        return "Studente";
    }

    // La collezione delle Iscrizioni e' mappata dall'ORM ma non viene navigata dal
    // codice: la verifica "lo Studente e' gia' iscritto a questo Corso" e' esposta da
    // RegistroCorsi.esisteIscrizione come interrogazione, per la ragione spiegata in Corso.
}
