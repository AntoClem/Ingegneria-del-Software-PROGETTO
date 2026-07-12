package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Docente;

/** Interfaccia DAO per la persistenza dell'Entity Docente. */
public interface DocenteDAO {
    Docente cercaPerUsername(String username);
    Docente cercaPerId(int id);
    void salva(Docente docente);
}
