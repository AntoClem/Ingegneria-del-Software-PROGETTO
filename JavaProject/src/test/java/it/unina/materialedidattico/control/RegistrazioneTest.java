package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.UtenteDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite del caso d'uso Registrazione (UC1).
 * I casi di prova derivano dalle classi di equivalenza del piano di test funzionale
 * (paragrafo 3.1): per ogni categoria una prova valida e una per ciascuna classe [ERROR].
 */
@DisplayName("UC1 - Registrazione")
class RegistrazioneTest extends BaseTest {

    @Test
    @DisplayName("Registrazione di uno Studente con dati validi")
    void registrazioneStudenteValida() {
        UtenteDTO studente = facade.registrazione("Studente", "Giovanni", "Aliperta",
                "g.aliperta@studenti.unina.it", PASSWORD_VALIDA);

        assertNotNull(studente.getId(), "l'identificatore deve essere assegnato dal database");
        assertEquals("Studente", studente.getRuolo());
        assertTrue(studente.isStudente());
        assertEquals("g.aliperta@studenti.unina.it", studente.getEmailIstituzionale());
    }

    @Test
    @DisplayName("Registrazione di un Docente con dati validi")
    void registrazioneDocenteValida() {
        UtenteDTO docente = facade.registrazione("Docente", "Anna", "Fasolino",
                "a.fasolino@unina.it", PASSWORD_VALIDA);

        assertNotNull(docente.getId());
        assertEquals("Docente", docente.getRuolo());
        assertTrue(docente.isDocente());
    }

    @Test
    @DisplayName("Il ruolo deve essere Studente o Docente")
    void ruoloNonAmmesso() {
        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Segretario", "Mario", "Rossi", "m.rossi@unina.it", PASSWORD_VALIDA));
    }

    @Test
    @DisplayName("Il nome non puo' essere vuoto")
    void nomeVuoto() {
        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Studente", "", "Rossi", "m.rossi@studenti.unina.it", PASSWORD_VALIDA));
    }

    @Test
    @DisplayName("Il nome non puo' contenere cifre")
    void nomeConCifre() {
        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Studente", "Mar1o", "Rossi", "m.rossi@studenti.unina.it", PASSWORD_VALIDA));
    }

    @Test
    @DisplayName("L'email deve appartenere al dominio unina.it")
    void emailNonIstituzionale() {
        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Studente", "Mario", "Rossi", "m.rossi@gmail.com", PASSWORD_VALIDA));
    }

    @Test
    @DisplayName("La password deve avere almeno 8 caratteri")
    void passwordTroppoCorta() {
        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Studente", "Mario", "Rossi", "m.rossi@studenti.unina.it", "Pass1"));
    }

    @Test
    @DisplayName("La password non puo' superare i 32 caratteri")
    void passwordTroppoLunga() {
        String troppoLunga = "P".repeat(33);
        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Studente", "Mario", "Rossi", "m.rossi@studenti.unina.it", troppoLunga));
    }

    @Test
    @DisplayName("L'email istituzionale deve essere univoca nel sistema (RD07)")
    void emailGiaRegistrata() {
        facade.registrazione("Studente", "Giovanni", "Aliperta",
                "g.aliperta@studenti.unina.it", PASSWORD_VALIDA);

        assertThrows(IllegalArgumentException.class, () -> facade.registrazione(
                "Docente", "Giovanni", "Aliperta", "g.aliperta@studenti.unina.it", PASSWORD_VALIDA));
    }
}
