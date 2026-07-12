package it.unina.materialedidattico.utility;

import it.unina.materialedidattico.entity.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test di unita' su Validazione (package utility), i controlli di
 * formato dell'input derivati dalle classi di equivalenza [ERROR]
 * individuate nel Piano di test funzionale (sez. 3). Non richiedono un
 * database ne' una GUI: verificano solo la logica pura dei due metodi
 * statici.
 */
class ValidazioneTest {

    // --- verificaCodiceCorso (sez. 3.1, IscrivitiCorso) ---

    @Test
    void codiceCorsoValidoRestituisceTrue() {
        assertTrue(Validazione.verificaCodiceCorso("CINF2024A"));
    }

    @Test
    void codiceCorsoVuotoRestituisceFalse() {
        assertFalse(Validazione.verificaCodiceCorso(""));
    }

    @Test
    void codiceCorsoSoloSpaziRestituisceFalse() {
        assertFalse(Validazione.verificaCodiceCorso("   "));
    }

    @Test
    void codiceCorsoNullRestituisceFalse() {
        assertFalse(Validazione.verificaCodiceCorso(null));
    }

    // --- verificaDatiPubblicazione (sez. 3.2, PubblicaContenuto) ---

    @Test
    void datiPubblicazioneCompletiRestituisconoTrue() {
        assertTrue(Validazione.verificaDatiPubblicazione(
                "Slide Lezione 1", "Introduzione al corso", Categoria.SLIDE));
    }

    @Test
    void titoloVuotoRestituisceFalse() {
        assertFalse(Validazione.verificaDatiPubblicazione(
                "", "Introduzione", Categoria.SLIDE));
    }

    @Test
    void descrizioneVuotaRestituisceFalse() {
        assertFalse(Validazione.verificaDatiPubblicazione(
                "Slide Lezione 1", "", Categoria.SLIDE));
    }

    @Test
    void categoriaNonSpecificataRestituisceFalse() {
        assertFalse(Validazione.verificaDatiPubblicazione(
                "Slide Lezione 1", "Introduzione", null));
    }

    @Test
    void titoloNullRestituisceFalse() {
        assertFalse(Validazione.verificaDatiPubblicazione(
                null, "Introduzione", Categoria.SLIDE));
    }

    @Test
    void descrizioneSoloSpaziRestituisceFalse() {
        assertFalse(Validazione.verificaDatiPubblicazione(
                "Titolo", "   ", Categoria.SLIDE));
    }
}
