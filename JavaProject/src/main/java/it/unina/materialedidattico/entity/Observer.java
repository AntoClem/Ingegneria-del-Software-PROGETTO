package it.unina.materialedidattico.entity;

/**
 * Osservatore di un Contenuto (pattern Observer).
 *
 * L'interfaccia e' dichiarata nel package entity, ma viene realizzata da una classe
 * del package control (GestoreNotifiche). La dipendenza va quindi dal layer superiore
 * verso un'astrazione del layer inferiore: il package entity non conosce nessuna
 * classe di livello superiore, e la stratificazione BCED resta rispettata anche se
 * un evento di dominio deve "risalire" verso il control.
 */
public interface Observer {

    /**
     * Notifica che il Contenuto osservato ha cambiato stato.
     * @param contenuto il Contenuto che ha generato l'evento
     */
    void aggiorna(Contenuto contenuto);
}
