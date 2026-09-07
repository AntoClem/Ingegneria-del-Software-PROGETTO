package it.unina.materialedidattico.entity;

/**
 * Categorie di materiale didattico previste dalla traccia (RD05).
 * Mappata come stringa nella colonna CATEGORIA della tabella CONTENUTO
 * (@Enumerated(EnumType.STRING) in Contenuto): si preferisce il nome alla
 * posizione ordinale perche' resta leggibile sul database e non si rompe
 * se in futuro l'ordine delle costanti cambia.
 */
public enum Categoria {
    SLIDE,
    DISPENSE,
    ESERCIZI,
    SOLUZIONI,
    AVVISI,
    MATERIALE_INTEGRATIVO
}
