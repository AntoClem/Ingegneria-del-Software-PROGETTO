package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Corso;

/** Interfaccia DAO per la persistenza dell'Entity Corso. */
public interface CorsoDAO {
    /** Usato da IscrivitiCorsoController per verificare l'esistenza del Corso a partire dal codice inserito dallo Studente. */
    Corso cercaPerCodice(String codiceUnivoco);
    Corso cercaPerId(int id);
    void salva(Corso corso);
}
