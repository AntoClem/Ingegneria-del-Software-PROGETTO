package it.unina.materialedidattico.utility;

import it.unina.materialedidattico.entity.Categoria;

/**
 * Raccoglie i controlli di validazione del formato dei dati inseriti
 * dall'utente, derivati dalle classi di equivalenza [ERROR] individuate
 * nel Piano di test funzionale (Category Partition Testing, sez. 3).
 *
 * Per la scelta progettuale illustrata nelle slide del corso (cfr. "Perche'
 * validare l'input nella GUI nel modello BCED"), questi controlli sono
 * invocati direttamente dalle classi Boundary, PRIMA di chiamare il
 * Controller: in questo modo il Controller riceve sempre dati gia'
 * corretti nel formato e puo' concentrarsi sulle sole regole di business
 * che richiedono accesso al database (es. verificare se un Corso esiste
 * davvero, se uno Studente e' gia' iscritto, ecc.).
 * Package estraneo al pattern BCED, analogo al package "utility" del
 * progetto di riferimento consultato (CarpoolingUnina).
 */
public class Validazione {

    private static final int CODICE_CORSO_LUNGHEZZA_MIN = 8;
    private static final int CODICE_CORSO_LUNGHEZZA_MAX = 12;
    private static final int TITOLO_LUNGHEZZA_MAX = 100;
    private static final int DESCRIZIONE_LUNGHEZZA_MAX = 500;

    private Validazione() {
        // classe di sole utility, non istanziabile
    }

    /**
     * Usato da FormIscrivitiCorso. Corrisponde alle classi di
     * equivalenza della categoria "Codice Corso" in sez. 3.1: valido se
     * alfanumerico e di lunghezza compresa tra 8 e 12 caratteri; non
     * valido se vuoto, se contiene caratteri non alfanumerici o se di
     * lunghezza eccessiva (> 12 caratteri).
     */
    public static boolean verificaCodiceCorso(String codiceCorso) {
        if (codiceCorso == null) {
            return false;
        }
        String valore = codiceCorso.trim();
        if (valore.isEmpty()) {
            return false;
        }
        if (!valore.matches("[a-zA-Z0-9]+")) {
            return false;
        }
        return valore.length() >= CODICE_CORSO_LUNGHEZZA_MIN && valore.length() <= CODICE_CORSO_LUNGHEZZA_MAX;
    }

    /**
     * Usato da FormPubblicaContenuto. Corrisponde alle classi di
     * equivalenza delle categorie "Titolo" (non vuoto, <= 100
     * caratteri), "Descrizione" (non vuota, <= 500 caratteri) e
     * "Categoria" (non nulla) in sez. 3.2.
     */
    public static boolean verificaDatiPubblicazione(String titolo, String descrizione, Categoria categoria) {
        if (titolo == null || titolo.trim().isEmpty() || titolo.trim().length() > TITOLO_LUNGHEZZA_MAX) {
            return false;
        }
        if (descrizione == null || descrizione.trim().isEmpty() || descrizione.trim().length() > DESCRIZIONE_LUNGHEZZA_MAX) {
            return false;
        }
        return categoria != null;
    }
}
