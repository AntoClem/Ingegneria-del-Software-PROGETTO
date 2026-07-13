package it.unina.materialedidattico.entity;

/**
 * Utente con ruolo Studente.
 *
 * Il metodo iscrivitiCorso(...) presente nel modello di analisi e'
 * stato rimosso per lo stesso motivo spiegato in Docente: la creazione
 * dell'Iscrizione e il suo salvataggio sono ora responsabilita' del
 * Control (IscrivitiCorsoController), che e' l'unico oggetto a
 * conoscere sia lo Studente che il Corso coinvolti nel caso d'uso.
 */
public class Studente extends Utente {

    public Studente(String nome, String cognome, String emailIstituzionale, String username, String password) {
        super(nome, cognome, emailIstituzionale, username, password);
    }
}
