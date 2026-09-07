package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.UtenteDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite del caso d'uso Accesso (UC2), piano di test funzionale paragrafo 3.2.
 * Verifica anche i due requisiti di sicurezza: il blocco temporaneo dopo cinque
 * tentativi falliti (RQ-SEC-01) e l'indistinguibilita' dei messaggi di errore
 * fra email inesistente e password errata (RQ-SEC-02).
 */
@DisplayName("UC2 - Accesso")
class AccessoTest extends BaseTest {

    private static final String EMAIL_STUDENTE = "g.aliperta@studenti.unina.it";

    @Test
    @DisplayName("Accesso con credenziali corrette e apertura della sessione")
    void accessoValido() {
        registraStudente(EMAIL_STUDENTE);

        UtenteDTO autenticato = facade.accesso(EMAIL_STUDENTE, PASSWORD_VALIDA);

        assertEquals(EMAIL_STUDENTE, autenticato.getEmailIstituzionale());
        assertTrue(autenticato.isStudente());
        assertTrue(SessioneUtente.getIstanza().isAperta(), "la sessione deve risultare aperta");
        assertEquals(autenticato.getId(),
                SessioneUtente.getIstanza().getUtenteAutenticato().getId());
    }

    @Test
    @DisplayName("Password errata: accesso rifiutato")
    void passwordErrata() {
        registraStudente(EMAIL_STUDENTE);

        assertThrows(SecurityException.class,
                () -> facade.accesso(EMAIL_STUDENTE, "PasswordSbagliata"));
        assertFalse(SessioneUtente.getIstanza().isAperta(),
                "un accesso fallito non deve aprire la sessione");
    }

    @Test
    @DisplayName("Email inesistente e password errata producono lo stesso messaggio (RQ-SEC-02)")
    void messaggiIndistinguibili() {
        registraStudente(EMAIL_STUDENTE);

        SecurityException conPasswordErrata = assertThrows(SecurityException.class,
                () -> facade.accesso(EMAIL_STUDENTE, "PasswordSbagliata"));
        SecurityException conEmailInesistente = assertThrows(SecurityException.class,
                () -> facade.accesso("nessuno@studenti.unina.it", PASSWORD_VALIDA));

        assertEquals(conPasswordErrata.getMessage(), conEmailInesistente.getMessage(),
                "i due messaggi devono coincidere per non rivelare quali email siano registrate");
    }

    @Test
    @DisplayName("Formato email non valido: rifiutato prima di interrogare il registro")
    void formatoEmailNonValido() {
        registraStudente(EMAIL_STUDENTE);

        assertThrows(IllegalArgumentException.class,
                () -> facade.accesso("non-una-email", PASSWORD_VALIDA));
    }

    @Test
    @DisplayName("Password vuota: rifiutata come formato non valido")
    void passwordVuota() {
        registraStudente(EMAIL_STUDENTE);

        assertThrows(IllegalArgumentException.class, () -> facade.accesso(EMAIL_STUDENTE, ""));
    }

    @Test
    @DisplayName("Dopo cinque tentativi falliti l'account viene bloccato (RQ-SEC-01)")
    void bloccoDopoCinqueTentativi() {
        registraStudente(EMAIL_STUDENTE);

        for (int tentativo = 1; tentativo <= 4; tentativo++) {
            assertThrows(SecurityException.class,
                    () -> facade.accesso(EMAIL_STUDENTE, "PasswordSbagliata"));
        }

        SecurityException quinto = assertThrows(SecurityException.class,
                () -> facade.accesso(EMAIL_STUDENTE, "PasswordSbagliata"));
        assertTrue(quinto.getMessage().toLowerCase().contains("bloccato"),
                "il quinto tentativo deve segnalare il blocco dell'account");

        // Da questo momento anche la password corretta viene rifiutata.
        SecurityException conPasswordCorretta = assertThrows(SecurityException.class,
                () -> facade.accesso(EMAIL_STUDENTE, PASSWORD_VALIDA));
        assertTrue(conPasswordCorretta.getMessage().toLowerCase().contains("bloccato"));
    }

    @Test
    @DisplayName("Un accesso riuscito azzera i tentativi falliti precedenti")
    void accessoRiuscitoAzzeraITentativi() {
        registraStudente(EMAIL_STUDENTE);

        for (int tentativo = 1; tentativo <= 3; tentativo++) {
            assertThrows(SecurityException.class,
                    () -> facade.accesso(EMAIL_STUDENTE, "PasswordSbagliata"));
        }
        facade.accesso(EMAIL_STUDENTE, PASSWORD_VALIDA);

        // Se il contatore non fosse azzerato, bastarebbero due errori per bloccare l'account.
        for (int tentativo = 1; tentativo <= 4; tentativo++) {
            SecurityException errore = assertThrows(SecurityException.class,
                    () -> facade.accesso(EMAIL_STUDENTE, "PasswordSbagliata"));
            assertFalse(errore.getMessage().toLowerCase().contains("bloccato"),
                    "al tentativo " + tentativo + " l'account non deve ancora essere bloccato");
        }
    }

    @Test
    @DisplayName("Il logout chiude la sessione")
    void logoutChiudeLaSessione() {
        registraStudente(EMAIL_STUDENTE);
        facade.accesso(EMAIL_STUDENTE, PASSWORD_VALIDA);

        facade.logout();

        assertFalse(SessioneUtente.getIstanza().isAperta());
    }
}
