package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Studente;

/** Interfaccia DAO per la persistenza dell'Entity Studente. */
public interface StudenteDAO {
    Studente cercaPerUsername(String username);
    Studente cercaPerId(int id);
    void salva(Studente studente);
}
