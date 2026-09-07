package it.unina.materialedidattico.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Oggetto osservabile (pattern Observer, ruolo Subject).
 *
 * Mantiene la collezione degli osservatori registrati e offre le operazioni per
 * registrarli, rimuoverli e notificarli. Contenuto la estende assumendo il ruolo di
 * Concrete Subject ed esponendo il metodo pubblico notificaObservers().
 *
 * Non e' una classe persistente: la lista degli osservatori vive in memoria per la
 * durata dell'operazione e non compare in alcuna tabella del database.
 */
public abstract class Subject {

    private final transient List<Observer> observers = new ArrayList<>();

    public void registraObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void rimuoviObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Avvisa tutti gli osservatori registrati. La copia della lista evita una
     * ConcurrentModificationException se un osservatore si deregistra mentre viene
     * notificato.
     */
    protected void notifica(Contenuto contenuto) {
        for (Observer observer : new ArrayList<>(observers)) {
            observer.aggiorna(contenuto);
        }
    }
}
