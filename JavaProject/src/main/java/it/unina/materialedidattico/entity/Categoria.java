package it.unina.materialedidattico.entity;

/**
 * Enumerazione delle categorie di Contenuto didattico.
 * Corrisponde, a livello di persistenza, a una colonna VARCHAR
 * discriminatrice nella tabella CONTENUTO (vedi schema.sql).
 */
public enum Categoria {
    SLIDE,
    DISPENSE,
    ESERCIZI,
    SOLUZIONI,
    AVVISI,
    MATERIALE_INTEGRATIVO
}
