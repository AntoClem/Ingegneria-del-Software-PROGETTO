package it.unina.materialedidattico.entity;

/**
 * Utente con ruolo Docente.
 *
 * Rispetto alla versione di analisi, il metodo creaCorso(...) e' stato
 * rimosso da questa classe: a livello di progettazione la creazione di
 * un Corso e il relativo salvataggio su database sono responsabilita'
 * del Control (vedi PubblicaContenutoController e, in generale, i
 * Controller che gestiscono la gestione dei corsi), non dell'Entity
 * Docente stessa. L'Entity resta cosi' un contenitore di dati e di
 * sole regole di dominio che non richiedono accesso alla persistenza.
 */
public class Docente extends Utente {

    public Docente(String nome, String cognome, String emailIstituzionale, String username, String password) {
        super(nome, cognome, emailIstituzionale, username, password);
    }
}
