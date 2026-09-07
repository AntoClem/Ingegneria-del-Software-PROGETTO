package it.unina.materialedidattico.entity;

/**
 * Codice dello stato di un Contenuto, usato per la persistenza.
 *
 * Il comportamento associato a ciascuno stato non sta qui, ma nelle classi del
 * sottopackage entity.stato (pattern State): questo enum serve soltanto a
 * memorizzare su database quale stato il Contenuto ha raggiunto, perche' un
 * oggetto-stato non e' di per se' persistibile.
 */
public enum StatoContenutoEnum {

    /** Salvato dal Docente ma non ancora visibile agli Studenti (RF10). */
    BOZZA,

    /** Visibile agli Studenti iscritti al Corso. */
    PUBBLICATO,

    /** Rimosso dal Docente: non e' piu' visibile agli Studenti (RF12). */
    RIMOSSO
}
