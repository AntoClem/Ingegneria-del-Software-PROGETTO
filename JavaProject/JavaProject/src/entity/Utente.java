package entity;

import java.io.Serializable;

/**
 * Classe astratta che rappresenta un utente generico della piattaforma.
 * Specializzata da {@link Docente} e {@link Studente}.
 */
public abstract class Utente implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int id;
    protected String nome;
    protected String cognome;
    protected String emailIstituzionale;
    protected String username;
    protected String password;

    public Utente() {
    }

    public Utente(int id, String nome, String cognome, String emailIstituzionale,
                  String username, String password) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.emailIstituzionale = emailIstituzionale;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmailIstituzionale() {
        return emailIstituzionale;
    }

    public void setEmailIstituzionale(String emailIstituzionale) {
        this.emailIstituzionale = emailIstituzionale;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce il ruolo dell'utente (DOCENTE o STUDENTE).
     * Implementato dalle sottoclassi concrete.
     */
    public abstract RuoloUtente getRuolo();

    @Override
    public String toString() {
        return nome + " " + cognome + " <" + username + ">";
    }
}
