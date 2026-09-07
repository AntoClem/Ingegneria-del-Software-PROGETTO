package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Utente che crea Corsi, li organizza in Sezioni e vi pubblica materiale didattico.
 */
@Entity
@DiscriminatorValue("DOCENTE")
public class Docente extends Utente {

    /** Corsi di cui il Docente e' titolare (RF21: profilo del Docente con i Corsi gestiti). */
    @OneToMany(mappedBy = "docenteTitolare", cascade = CascadeType.ALL)
    private List<Corso> corsiGestiti = new ArrayList<>();

    /** Costruttore vuoto richiesto da JPA: l'istanza nasce da DocenteFactory (UC1). */
    public Docente() {
    }

    public Docente(String nome, String cognome, String emailIstituzionale, String password) {
        super(nome, cognome, emailIstituzionale, password);
    }

    @Override
    public String getRuolo() {
        return "Docente";
    }

    /**
     * True se il Docente e' titolare del Corso indicato: e' la verifica che
     * garantisce la pre-condizione dei casi d'uso PubblicaContenuto e
     * MonitoraAndamentoCorso una volta introdotta l'autenticazione (V05).
     */
    public boolean isTitolareDi(Corso corso) {
        return corso != null && corso.getDocenteTitolare() != null
                && corso.getDocenteTitolare().getId() != null
                && corso.getDocenteTitolare().getId().equals(getId());
    }

    // Come in Corso: la collezione dei Corsi gestiti e' mappata ma non navigata;
    // l'elenco si ottiene da RegistroCorsi.cercaCorsiPerDocente.
}
