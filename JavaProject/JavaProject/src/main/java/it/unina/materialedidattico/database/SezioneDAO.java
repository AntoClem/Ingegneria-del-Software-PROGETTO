package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import java.util.List;

/** Interfaccia DAO per la persistenza dell'Entity Sezione. */
public interface SezioneDAO {
    List<Sezione> cercaPerCorso(Corso corso);
    void salva(Sezione sezione);
}
