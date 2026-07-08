package database;

import entity.Categoria;
import entity.Contenuto;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaccia DAO per l'accesso ai dati dei contenuti didattici.
 */
public interface ContenutoDAO {

    Contenuto findById(int id);

    /** Contenuti di un corso, opzionalmente filtrati per categoria/data/sezione. */
    List<Contenuto> findByCorso(int corsoId, Categoria categoria,
                                 LocalDate data, Integer sezioneId);

    /** Solo i contenuti pubblicati (visibili agli studenti) di un corso. */
    List<Contenuto> findPubblicatiByCorso(int corsoId);

    /** Ricerca full-text (semplificata) su titolo, descrizione o categoria. */
    List<Contenuto> ricerca(String testo);

    /** Numero totale di contenuti pubblicati per corso. */
    int contaContenutiPerCorso(int corsoId);

    int inserisci(Contenuto contenuto);

    void aggiorna(Contenuto contenuto);

    /** Rimozione logica o fisica: dopo la chiamata il contenuto non e' piu' visibile agli studenti. */
    void elimina(int id);
}
