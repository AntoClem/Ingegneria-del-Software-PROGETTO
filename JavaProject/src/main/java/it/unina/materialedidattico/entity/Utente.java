package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Generalizzazione degli utenti registrati alla piattaforma (Studente, Docente).
 *
 * Strategia di mappatura SINGLE_TABLE con colonna discriminatrice TIPO: Studente e
 * Docente non introducono attributi propri rispetto a Utente (la traccia richiede per
 * entrambi solo nome, cognome ed email istituzionale), quindi separare le tabelle
 * produrrebbe due tabelle prive di colonne proprie e JOIN inutili ad ogni lettura.
 *
 * I campi tentativiFalliti e bloccatoFinoA realizzano il requisito RQ-SEC-01: sono
 * attributi dell'Utente perche' e' l'Utente stesso l'Information Expert sul proprio
 * stato di accesso (si veda il diagramma di sequenza di analisi del caso d'uso Accesso).
 */
@Entity
@Table(name = "UTENTE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 50)
    private String cognome;

    /** Identificativo con cui l'Utente si autentica: univoco nel sistema (RD07). */
    @Column(name = "EMAIL_ISTITUZIONALE", nullable = false, unique = true, length = 150)
    private String emailIstituzionale;

    @Column(nullable = false, length = 100)
    private String password;

    /** Tentativi di accesso falliti consecutivi (RD08). */
    @Column(name = "TENTATIVI_FALLITI", nullable = false)
    private int tentativiFalliti;

    /** Istante fino al quale l'account resta bloccato; null se non bloccato (RD08). */
    @Column(name = "BLOCCATO_FINO_A")
    private LocalDateTime bloccatoFinoA;

    /** Costruttore vuoto richiesto da JPA: le istanze nascono dalle Factory del package control. */
    protected Utente() {
    }

    protected Utente(String nome, String cognome, String emailIstituzionale, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.emailIstituzionale = emailIstituzionale;
        this.password = password;
        this.tentativiFalliti = 0;
        this.bloccatoFinoA = null;
    }

    /**
     * Verifica che la password ricevuta corrisponda a quella dell'Utente.
     * Metodo emerso dal sequence diagram di analisi del caso d'uso Accesso: la
     * responsabilita' e' dell'Utente perche' e' l'unico oggetto che possiede la
     * propria password, che non deve essere letta da nessun altro oggetto.
     */
    public boolean verificaCredenziali(String passwordInserita) {
        return passwordInserita != null && passwordInserita.equals(this.password);
    }

    /** True se l'account risulta attualmente sotto blocco temporaneo (RQ-SEC-01). */
    public boolean isBloccato() {
        return bloccatoFinoA != null && LocalDateTime.now(ZoneId.of("Europe/Rome")).isBefore(bloccatoFinoA);
    }

    public void incrementaTentativiFalliti() {
        this.tentativiFalliti++;
    }

    public void bloccaTemporaneamente(int minutiBlocco) {
        this.bloccatoFinoA = LocalDateTime.now(ZoneId.of("Europe/Rome")).plusMinutes(minutiBlocco);
        this.tentativiFalliti = 0;
    }

    public void azzeraTentativiFalliti() {
        this.tentativiFalliti = 0;
        this.bloccatoFinoA = null;
    }

    /** Ruolo dell'Utente, implementato dalle sottoclassi concrete. */
    public abstract String getRuolo();

    public Long getId() {
        return id;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTentativiFalliti() {
        return tentativiFalliti;
    }
}
