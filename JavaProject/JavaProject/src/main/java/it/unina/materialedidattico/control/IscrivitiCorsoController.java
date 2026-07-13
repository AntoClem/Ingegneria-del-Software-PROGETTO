package it.unina.materialedidattico.control;

import it.unina.materialedidattico.database.CorsoDAO;
import it.unina.materialedidattico.database.CorsoDAOH2Impl;
import it.unina.materialedidattico.database.IscrizioneDAO;
import it.unina.materialedidattico.database.IscrizioneDAOH2Impl;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Iscrizione;
import it.unina.materialedidattico.entity.Studente;

/**
 * Use Case Controller del caso d'uso IscrivitiCorso (UC6).
 * Riceve dalla Boundary il codice del Corso gia' validato nel formato
 * (controllo demandato a Validazione.verificaCodiceCorso, eseguito
 * lato Boundary) e coordina le regole di business del caso d'uso:
 * esistenza del Corso e non duplicazione dell'Iscrizione.
 */
public class IscrivitiCorsoController {

    /** Esito restituito alla Boundary, corrisponde ai tre rami del diagramma di sequenza di progetto. */
    public enum EsitoIscrizione {
        SUCCESSO,
        STUDENTE_GIA_ISCRITTO,
        CORSO_INESISTENTE
    }

    private final CorsoDAO corsoDAO = new CorsoDAOH2Impl();
    private final IscrizioneDAO iscrizioneDAO = new IscrizioneDAOH2Impl();

    public EsitoIscrizione iscrivitiCorso(Studente studente, String codiceCorso) {
        Corso corso = corsoDAO.cercaPerCodice(codiceCorso);
        if (corso == null) {
            return EsitoIscrizione.CORSO_INESISTENTE;
        }

        boolean giaIscritto = iscrizioneDAO.verificaEsistente(studente, corso);
        if (giaIscritto) {
            return EsitoIscrizione.STUDENTE_GIA_ISCRITTO;
        }

        Iscrizione iscrizione = new Iscrizione(studente, corso);
        iscrizioneDAO.salva(iscrizione);
        corso.aggiungiIscrizione(iscrizione);
        return EsitoIscrizione.SUCCESSO;
    }
}
