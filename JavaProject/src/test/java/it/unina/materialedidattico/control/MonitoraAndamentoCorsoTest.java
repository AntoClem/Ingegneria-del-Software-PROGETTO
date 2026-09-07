package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.dto.RiepilogoAndamentoDTO;
import it.unina.materialedidattico.dto.UtenteDTO;
import it.unina.materialedidattico.entity.Corso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite dei casi d'uso MonitoraAndamentoCorso (UC15), VisualizzaContenutiCorso (UC13)
 * con il suo incluso FiltraContenuti (UC18), e VisualizzaNotifiche (UC22).
 * Piano di test funzionale, paragrafi 3.5, 3.6 e 3.7.
 */
@DisplayName("UC15, UC13 e UC22 - Consultazione e monitoraggio")
class MonitoraAndamentoCorsoTest extends BaseTest {

    private static final String CODICE_CORSO = "CINF2026A";

    private Corso preparaCorsoConDueIscritti() {
        UtenteDTO docente = registraDocente("a.fasolino@unina.it");
        Corso corso = creaCorso(docente.getId(), CODICE_CORSO);

        UtenteDTO primo = registraStudente("g.aliperta@studenti.unina.it");
        facade.iscrivitiCorso(primo.getId(), CODICE_CORSO);

        UtenteDTO secondo = facade.registrazione("Studente", "Laura", "Verdi",
                "l.verdi@studenti.unina.it", PASSWORD_VALIDA);
        facade.iscrivitiCorso(secondo.getId(), CODICE_CORSO);

        return corso;
    }

    @Test
    @DisplayName("Corso senza materiali ne' iscritti: riepilogo a zero, nessun errore")
    void riepilogoDiUnCorsoVuoto() {
        UtenteDTO docente = registraDocente("a.fasolino@unina.it");
        Corso corso = creaCorso(docente.getId(), CODICE_CORSO);

        RiepilogoAndamentoDTO riepilogo = facade.monitoraAndamentoCorso(corso.getId());

        assertEquals(0, riepilogo.getNumeroContenutiPubblicati());
        assertEquals(0, riepilogo.getNumeroStudentiIscritti());
        assertTrue(riepilogo.getDistribuzionePerCategoria().isEmpty());
    }

    @Test
    @DisplayName("Il riepilogo conta i materiali visibili, gli iscritti e la distribuzione")
    void riepilogoConMaterialiEIscritti() {
        Corso corso = preparaCorsoConDueIscritti();
        facade.pubblicaContenuto(corso.getId(), "Slide 1", "Prima lezione", "SLIDE", null, true);
        facade.pubblicaContenuto(corso.getId(), "Slide 2", "Seconda lezione", "SLIDE", null, true);
        facade.pubblicaContenuto(corso.getId(), "Dispensa", "Approfondimento", "DISPENSE", null, true);
        facade.pubblicaContenuto(corso.getId(), "Esercizi", "Bozza", "ESERCIZI", null, false);

        RiepilogoAndamentoDTO riepilogo = facade.monitoraAndamentoCorso(corso.getId());

        assertEquals(3, riepilogo.getNumeroContenutiPubblicati(),
                "la bozza non deve essere conteggiata");
        assertEquals(2, riepilogo.getNumeroStudentiIscritti());
        assertEquals(2, riepilogo.getDistribuzionePerCategoria().get("SLIDE"));
        assertEquals(1, riepilogo.getDistribuzionePerCategoria().get("DISPENSE"));
        assertNull(riepilogo.getDistribuzionePerCategoria().get("ESERCIZI"),
                "una categoria con soli materiali in bozza non deve comparire");
    }

    @Test
    @DisplayName("UC18: il filtro per categoria restituisce solo i materiali richiesti")
    void filtroPerCategoria() {
        Corso corso = preparaCorsoConDueIscritti();
        facade.pubblicaContenuto(corso.getId(), "Slide 1", "Prima lezione", "SLIDE", null, true);
        facade.pubblicaContenuto(corso.getId(), "Dispensa", "Approfondimento", "DISPENSE", null, true);

        List<ContenutoDTO> soloSlide = facade.filtraContenuti(corso.getId(), "SLIDE", null, null);

        assertEquals(1, soloSlide.size());
        assertEquals("SLIDE", soloSlide.get(0).getCategoria());
    }

    @Test
    @DisplayName("UC18: senza filtri impostati vengono restituiti tutti i materiali visibili")
    void filtroNonImpostato() {
        Corso corso = preparaCorsoConDueIscritti();
        facade.pubblicaContenuto(corso.getId(), "Slide 1", "Prima lezione", "SLIDE", null, true);
        facade.pubblicaContenuto(corso.getId(), "Dispensa", "Approfondimento", "DISPENSE", null, true);

        List<ContenutoDTO> tutti = facade.filtraContenuti(corso.getId(), null, null, null);

        assertEquals(2, tutti.size());
    }

    @Test
    @DisplayName("UC22: la notifica selezionata viene segnata come letta")
    void letturaDiUnaNotifica() {
        Corso corso = preparaCorsoConDueIscritti();
        facade.pubblicaContenuto(corso.getId(), "Slide 1", "Prima lezione", "SLIDE", null, true);

        Long idStudente = it.unina.materialedidattico.entity.RegistroUtenti.getIstanza()
                .cercaUtentePerEmail("g.aliperta@studenti.unina.it").getId();

        assertEquals(1, facade.contaNotificheNonLette(idStudente));

        facade.segnaNotificaComeLetta(facade.visualizzaNotifiche(idStudente).get(0).getId());

        assertEquals(0, facade.contaNotificheNonLette(idStudente));
        assertTrue(facade.visualizzaNotifiche(idStudente).get(0).isLetta());
    }

    @Test
    @DisplayName("Ogni iscritto riceve la propria notifica")
    void notificaAOgniIscritto() {
        Corso corso = preparaCorsoConDueIscritti();
        facade.pubblicaContenuto(corso.getId(), "Slide 1", "Prima lezione", "SLIDE", null, true);

        var registro = it.unina.materialedidattico.entity.RegistroUtenti.getIstanza();
        Long primo = registro.cercaUtentePerEmail("g.aliperta@studenti.unina.it").getId();
        Long secondo = registro.cercaUtentePerEmail("l.verdi@studenti.unina.it").getId();

        assertEquals(1, facade.visualizzaNotifiche(primo).size());
        assertEquals(1, facade.visualizzaNotifiche(secondo).size());
    }
}
