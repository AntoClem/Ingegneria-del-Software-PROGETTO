package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Notifica;

/** Interfaccia DAO per la persistenza dell'Entity Notifica. */
public interface NotificaDAO {
    void salva(Notifica notifica);
}
