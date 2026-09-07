package it.unina.materialedidattico.entity;

import it.unina.materialedidattico.database.GestorePersistenza;

import java.util.List;
import java.util.Map;

/**
 * Facciata del layer Entity per l'accesso ai Corsi, alle Sezioni e alle Iscrizioni.
 * Realizza le responsabilita' che in analisi appartenevano alla Piattaforma
 * (cercaCorsoPerCodice, aggiungiCorso) e al Corso (verifica delle iscrizioni).
 */
public class RegistroCorsi {

    private static RegistroCorsi istanza;

    private final GestorePersistenza gestorePersistenza;

    private RegistroCorsi() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public static synchronized RegistroCorsi getIstanza() {
        if (istanza == null) {
            istanza = new RegistroCorsi();
        }
        return istanza;
    }

    /** Cerca il Corso a partire dal codice inserito dallo Studente (UC6). */
    public Corso cercaCorsoPerCodice(String codiceUnivoco) {
        return gestorePersistenza.cercaPrimoPerCampi(Corso.class,
                Map.of("codiceUnivoco", codiceUnivoco));
    }

    public Corso trovaCorsoPerId(Long id) {
        return gestorePersistenza.trovaPerId(Corso.class, id);
    }

    /** Corsi di cui il Docente indicato e' titolare. */
    public List<Corso> cercaCorsiPerDocente(Docente docente) {
        return gestorePersistenza.cercaPerCampo(Corso.class, "docenteTitolare", docente);
    }

    /** Corsi a cui lo Studente indicato risulta iscritto (RF13). */
    public List<Corso> cercaCorsiPerStudente(Studente studente) {
        return gestorePersistenza.eseguiQuery(
                "SELECT i.corso FROM Iscrizione i WHERE i.studente = :studente",
                Corso.class, Map.of("studente", studente));
    }

    /** True se lo Studente risulta gia' iscritto al Corso (UC6). */
    public boolean esisteIscrizione(Studente studente, Corso corso) {
        return !gestorePersistenza.cercaPerCampi(Iscrizione.class,
                Map.of("studente", studente, "corso", corso)).isEmpty();
    }

    public void registraIscrizione(Iscrizione iscrizione) {
        gestorePersistenza.salva(iscrizione);
    }

    public void registraCorso(Corso corso) {
        gestorePersistenza.salva(corso);
    }

    public void registraSezione(Sezione sezione) {
        gestorePersistenza.salva(sezione);
    }

    /** Sezioni definite all'interno del Corso indicato. */
    public List<Sezione> cercaSezioniPerCorso(Corso corso) {
        return gestorePersistenza.cercaPerCampo(Sezione.class, "corso", corso);
    }

    /** Numero di Studenti iscritti al Corso (UC15). */
    public int contaIscrittiPerCorso(Corso corso) {
        return gestorePersistenza.cercaPerCampo(Iscrizione.class, "corso", corso).size();
    }

    /** Studenti iscritti al Corso: destinatari delle Notifiche (UC21). */
    public List<Studente> cercaStudentiIscritti(Corso corso) {
        return gestorePersistenza.eseguiQuery(
                "SELECT i.studente FROM Iscrizione i WHERE i.corso = :corso",
                Studente.class, Map.of("corso", corso));
    }
}
