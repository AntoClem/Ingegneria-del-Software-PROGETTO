package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Iscrizione;
import it.unina.materialedidattico.entity.Studente;

import java.util.List;

/** Interfaccia DAO per la persistenza dell'Entity Iscrizione (classe associativa reificata). */
public interface IscrizioneDAO {
    /** Usato da IscrivitiCorsoController per il controllo "studente gia' iscritto". */
    boolean verificaEsistente(Studente studente, Corso corso);
    void salva(Iscrizione iscrizione);

    /** Usato da MonitoraAndamentoCorsoController. */
    int contaPerCorso(Corso corso);

    /** Usato da PubblicaContenutoController per individuare i destinatari delle Notifiche (caso d'uso incluso InviaNotifica). */
    List<Studente> cercaIscrittiPerCorso(Corso corso);
}
