package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.UtenteDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite del caso d'uso IscrivitiCorso (UC6), piano di test funzionale paragrafo 3.3.
 */
@DisplayName("UC6 - IscrivitiCorso")
class IscrivitiCorsoTest extends BaseTest {

    private static final String CODICE_CORSO = "CINF2026A";

    @Test
    @DisplayName("Iscrizione con codice valido")
    void iscrizioneValida() {
        UtenteDTO docente = registraDocente("a.fasolino@unina.it");
        creaCorso(docente.getId(), CODICE_CORSO);
        UtenteDTO studente = registraStudente("g.aliperta@studenti.unina.it");

        CorsoDTO corso = facade.iscrivitiCorso(studente.getId(), CODICE_CORSO);

        assertEquals(CODICE_CORSO, corso.getCodiceUnivoco());

        List<CorsoDTO> corsiSeguiti = facade.visualizzaCorsiIscritti(studente.getId());
        assertEquals(1, corsiSeguiti.size(), "il corso deve comparire fra quelli seguiti");
        assertEquals(CODICE_CORSO, corsiSeguiti.get(0).getCodiceUnivoco());
    }

    @Test
    @DisplayName("Codice che non corrisponde a nessun corso")
    void codiceInesistente() {
        UtenteDTO studente = registraStudente("g.aliperta@studenti.unina.it");

        assertThrows(IllegalArgumentException.class,
                () -> facade.iscrivitiCorso(studente.getId(), "CINF9999Z"));
    }

    @Test
    @DisplayName("Codice di formato non valido: troppo corto")
    void codiceTroppoCorto() {
        UtenteDTO studente = registraStudente("g.aliperta@studenti.unina.it");

        assertThrows(IllegalArgumentException.class,
                () -> facade.iscrivitiCorso(studente.getId(), "CINF"));
    }

    @Test
    @DisplayName("Codice di formato non valido: contiene caratteri non alfanumerici")
    void codiceConCaratteriNonAmmessi() {
        UtenteDTO studente = registraStudente("g.aliperta@studenti.unina.it");

        assertThrows(IllegalArgumentException.class,
                () -> facade.iscrivitiCorso(studente.getId(), "CINF-2026"));
    }

    @Test
    @DisplayName("Un secondo tentativo di iscrizione allo stesso corso viene rifiutato")
    void studenteGiaIscritto() {
        UtenteDTO docente = registraDocente("a.fasolino@unina.it");
        creaCorso(docente.getId(), CODICE_CORSO);
        UtenteDTO studente = registraStudente("g.aliperta@studenti.unina.it");
        facade.iscrivitiCorso(studente.getId(), CODICE_CORSO);

        assertThrows(IllegalArgumentException.class,
                () -> facade.iscrivitiCorso(studente.getId(), CODICE_CORSO));

        assertEquals(1, facade.visualizzaCorsiIscritti(studente.getId()).size(),
                "non deve essere creata una seconda iscrizione");
    }
}
