package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.dto.NotificaDTO;
import it.unina.materialedidattico.dto.UtenteDTO;
import it.unina.materialedidattico.entity.Corso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite dei casi d'uso PubblicaContenuto (UC7), AttivaContenuto (UC9) e
 * RimuoviContenuto (UC11), piano di test funzionale paragrafo 3.4.
 *
 * Verifica anche le transizioni ammesse dal pattern State e la generazione automatica
 * delle Notifiche da parte del pattern Observer.
 */
@DisplayName("UC7 - PubblicaContenuto, con UC9 e UC11")
class PubblicaContenutoTest extends BaseTest {

    private static final String CODICE_CORSO = "CINF2026A";

    private Corso preparaCorsoConUnIscritto() {
        UtenteDTO docente = registraDocente("a.fasolino@unina.it");
        Corso corso = creaCorso(docente.getId(), CODICE_CORSO);
        UtenteDTO studente = registraStudente("g.aliperta@studenti.unina.it");
        facade.iscrivitiCorso(studente.getId(), CODICE_CORSO);
        return corso;
    }

    @Test
    @DisplayName("Pubblicazione immediata: il materiale e' visibile e gli iscritti sono notificati")
    void pubblicazioneImmediata() {
        Corso corso = preparaCorsoConUnIscritto();

        ContenutoDTO contenuto = facade.pubblicaContenuto(corso.getId(), "Slide Lezione 1",
                "Introduzione al corso", "SLIDE", null, true);

        assertEquals("PUBBLICATO", contenuto.getStato());
        assertTrue(contenuto.isVisibile());
        assertEquals(1, facade.visualizzaContenutiCorso(corso.getId()).size());

        // La notifica deve essere arrivata all'unico studente iscritto.
        List<NotificaDTO> notifiche = facade.visualizzaNotifiche(idStudenteIscritto());
        assertEquals(1, notifiche.size(), "l'iscritto deve aver ricevuto una notifica");
        assertFalse(notifiche.get(0).isLetta());
        assertTrue(notifiche.get(0).getMessaggio().contains("Slide Lezione 1"));
    }

    @Test
    @DisplayName("Salvataggio come bozza: non visibile agli studenti e nessuna notifica")
    void salvataggioComeBozza() {
        Corso corso = preparaCorsoConUnIscritto();

        ContenutoDTO contenuto = facade.pubblicaContenuto(corso.getId(), "Esercizi",
                "Non ancora attivi", "ESERCIZI", null, false);

        assertEquals("BOZZA", contenuto.getStato());
        assertFalse(contenuto.isVisibile());
        assertTrue(facade.visualizzaContenutiCorso(corso.getId()).isEmpty(),
                "una bozza non deve comparire fra i materiali visibili");
        assertTrue(facade.visualizzaNotifiche(idStudenteIscritto()).isEmpty(),
                "una bozza non deve generare notifiche");
    }

    @Test
    @DisplayName("UC9: l'attivazione di una bozza la rende visibile e notifica gli iscritti")
    void attivazioneDiUnaBozza() {
        Corso corso = preparaCorsoConUnIscritto();
        ContenutoDTO bozza = facade.pubblicaContenuto(corso.getId(), "Esercizi",
                "Non ancora attivi", "ESERCIZI", null, false);

        ContenutoDTO attivato = facade.attivaContenuto(bozza.getId());

        assertEquals("PUBBLICATO", attivato.getStato());
        assertEquals(1, facade.visualizzaContenutiCorso(corso.getId()).size());
        assertEquals(1, facade.visualizzaNotifiche(idStudenteIscritto()).size());
    }

    @Test
    @DisplayName("Un materiale gia' pubblicato non puo' essere ripubblicato (pattern State)")
    void ripubblicazioneNonAmmessa() {
        Corso corso = preparaCorsoConUnIscritto();
        ContenutoDTO contenuto = facade.pubblicaContenuto(corso.getId(), "Slide",
                "Descrizione", "SLIDE", null, true);

        assertThrows(IllegalStateException.class, () -> facade.attivaContenuto(contenuto.getId()));
        assertEquals(1, facade.visualizzaNotifiche(idStudenteIscritto()).size(),
                "non devono essere generate notifiche duplicate");
    }

    @Test
    @DisplayName("UC11: la rimozione toglie il materiale dalla vista degli studenti")
    void rimozioneDelMateriale() {
        Corso corso = preparaCorsoConUnIscritto();
        ContenutoDTO contenuto = facade.pubblicaContenuto(corso.getId(), "Slide",
                "Descrizione", "SLIDE", null, true);

        ContenutoDTO rimosso = facade.rimuoviContenuto(contenuto.getId());

        assertEquals("RIMOSSO", rimosso.getStato());
        assertTrue(facade.visualizzaContenutiCorso(corso.getId()).isEmpty());
    }

    @Test
    @DisplayName("Un materiale rimosso non puo' essere rimosso di nuovo ne' ripubblicato")
    void statoRimossoEFinale() {
        Corso corso = preparaCorsoConUnIscritto();
        ContenutoDTO contenuto = facade.pubblicaContenuto(corso.getId(), "Slide",
                "Descrizione", "SLIDE", null, true);
        facade.rimuoviContenuto(contenuto.getId());

        assertThrows(IllegalStateException.class, () -> facade.rimuoviContenuto(contenuto.getId()));
        assertThrows(IllegalStateException.class, () -> facade.attivaContenuto(contenuto.getId()));
    }

    @Test
    @DisplayName("Il titolo e' obbligatorio")
    void titoloMancante() {
        Corso corso = preparaCorsoConUnIscritto();

        assertThrows(IllegalArgumentException.class, () -> facade.pubblicaContenuto(
                corso.getId(), "", "Descrizione", "SLIDE", null, true));
    }

    @Test
    @DisplayName("La descrizione e' obbligatoria")
    void descrizioneMancante() {
        Corso corso = preparaCorsoConUnIscritto();

        assertThrows(IllegalArgumentException.class, () -> facade.pubblicaContenuto(
                corso.getId(), "Slide", "  ", "SLIDE", null, true));
    }

    @Test
    @DisplayName("La categoria deve essere una di quelle ammesse")
    void categoriaNonAmmessa() {
        Corso corso = preparaCorsoConUnIscritto();

        assertThrows(IllegalArgumentException.class, () -> facade.pubblicaContenuto(
                corso.getId(), "Slide", "Descrizione", "VIDEOLEZIONE", null, true));
    }

    /** Identificatore dell'unico Studente iscritto, usato per leggerne le notifiche. */
    private Long idStudenteIscritto() {
        return it.unina.materialedidattico.entity.RegistroUtenti.getIstanza()
                .cercaUtentePerEmail("g.aliperta@studenti.unina.it").getId();
    }
}
