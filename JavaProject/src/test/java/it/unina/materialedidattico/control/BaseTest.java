package it.unina.materialedidattico.control;

import it.unina.materialedidattico.database.GestorePersistenza;
import it.unina.materialedidattico.database.JpaUtil;
import it.unina.materialedidattico.dto.UtenteDTO;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Docente;
import it.unina.materialedidattico.entity.RegistroUtenti;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base comune alle suite di test dei casi d'uso.
 *
 * I test attaccano la PiattaformaFacade, cioe' lo stesso punto di ingresso che usano le
 * schermate: si verifica quindi il comportamento dei casi d'uso, non quello delle singole
 * classi isolate. E' possibile perche' il layer Control non dipende dalla GUI, che e' uno
 * dei vantaggi della stratificazione BCED (progettazione per la testabilita', lezione 13).
 *
 * Le prove girano su un database separato, materialedidatticodb_test, dichiarato in
 * src/test/resources/META-INF/persistence.xml: il database di lavoro non viene toccato.
 * Ogni test parte da tabelle vuote, cosi' l'esito non dipende dall'ordine di esecuzione.
 */
abstract class BaseTest {

    protected PiattaformaFacade facade;
    protected GestorePersistenza gestorePersistenza;

    protected static final String PASSWORD_VALIDA = "Password01";

    @BeforeEach
    void preparaAmbiente() {
        facade = PiattaformaFacade.getIstanza();
        gestorePersistenza = new GestorePersistenza();
        svuotaDatabase();
        SessioneUtente.getIstanza().chiudi();
    }

    @AfterEach
    void ripulisciAmbiente() {
        svuotaDatabase();
        SessioneUtente.getIstanza().chiudi();
    }

    /** Svuota le tabelle rispettando l'ordine imposto dalle chiavi esterne. */
    protected void svuotaDatabase() {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        EntityTransaction transazione = em.getTransaction();
        try {
            transazione.begin();
            em.createQuery("DELETE FROM Notifica").executeUpdate();
            em.createQuery("DELETE FROM Iscrizione").executeUpdate();
            em.createQuery("DELETE FROM Contenuto").executeUpdate();
            em.createQuery("DELETE FROM Sezione").executeUpdate();
            em.createQuery("DELETE FROM Corso").executeUpdate();
            em.createQuery("DELETE FROM Utente").executeUpdate();
            transazione.commit();
        } catch (RuntimeException e) {
            if (transazione.isActive()) {
                transazione.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Registra un Docente di prova e restituisce il suo DTO. */
    protected UtenteDTO registraDocente(String email) {
        return facade.registrazione("Docente", "Anna", "Fasolino", email, PASSWORD_VALIDA);
    }

    /** Registra uno Studente di prova e restituisce il suo DTO. */
    protected UtenteDTO registraStudente(String email) {
        return facade.registrazione("Studente", "Giovanni", "Aliperta", email, PASSWORD_VALIDA);
    }

    /**
     * Crea un Corso di prova per il Docente indicato.
     *
     * Il Corso viene creato passando dalle Entity e dalla facciata di persistenza perche'
     * il caso d'uso CreaCorso non e' fra quelli sviluppati in dettaglio: e' materiale di
     * preparazione del test, non l'oggetto della verifica.
     */
    protected Corso creaCorso(Long idDocente, String codice) {
        Docente docente = RegistroUtenti.getIstanza().trovaDocentePerId(idDocente);
        Corso corso = new Corso("Ingegneria del Software", "Corso di prova",
                codice, "2025/2026", docente);
        gestorePersistenza.salva(corso);
        return corso;
    }
}
