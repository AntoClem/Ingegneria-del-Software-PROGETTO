package it.unina.materialedidattico.entity.stato;

import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.StatoContenutoEnum;

/**
 * Stato di un Contenuto didattico (pattern State).
 *
 * Il Contenuto delega a questa interfaccia le operazioni il cui effetto dipende
 * dallo stato corrente: pubblicare una bozza e' lecito, ripubblicare un materiale
 * gia' pubblicato no, e nulla puo' essere fatto su un materiale rimosso.
 *
 * L'alternativa ingenua sarebbe una catena di if sullo stato dentro ogni metodo di
 * Contenuto (soluzione mostrata come esempio negativo nella lezione 15): il pattern
 * State la sostituisce con una classe per stato, ciascuna responsabile di sapere
 * quali transizioni siano ammesse a partire da se'.
 *
 * Gli stati concreti sono privi di dati propri e realizzati come Singleton, quindi
 * condivisi da tutti i Contenuti che si trovano nello stesso stato.
 */
public interface StatoContenuto {

    /**
     * Rende visibile il Contenuto agli Studenti.
     * @throws IllegalStateException se la transizione non e' ammessa dallo stato corrente.
     */
    void pubblica(Contenuto contenuto);

    /**
     * Rimuove il Contenuto, che non sara' piu' visibile agli Studenti.
     * @throws IllegalStateException se la transizione non e' ammessa dallo stato corrente.
     */
    void rimuovi(Contenuto contenuto);

    /** True se, in questo stato, il Contenuto e' visibile agli Studenti. */
    boolean isVisibile();

    /** Codice con cui lo stato viene memorizzato sul database. */
    StatoContenutoEnum getCodice();
}
