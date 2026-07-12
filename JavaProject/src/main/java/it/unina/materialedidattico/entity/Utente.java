package it.unina.materialedidattico.entity;

/**
 * Classe astratta che rappresenta un generico utente della piattaforma.
 * Superclasse di Docente e Studente (generalizzazione).
 *
 * Rispetto alla versione di analisi, a livello di progettazione viene
 * aggiunto l'attributo id: rappresenta la chiave primaria della tabella
 * UTENTE (o della tabella della sottoclasse, a seconda della strategia
 * scelta per la generalizzazione - vedi 4.1.2.4 Package Database) ed e'
 * necessario alle classi DAO per identificare univocamente la riga da
 * leggere, aggiornare o cancellare.
 */
public abstract class Utente {

    private int id;
    private String nome;
    private String cognome;
    private String emailIstituzionale;
    private String username;
    private String password;

    public Utente(String nome, String cognome, String emailIstituzionale, String username, String password) {
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
}
