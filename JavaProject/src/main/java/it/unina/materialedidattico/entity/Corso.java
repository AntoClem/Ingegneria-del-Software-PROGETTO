package it.unina.materialedidattico.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Insegnamento creato e gestito da un Docente, a cui gli Studenti si iscrivono
 * e all'interno del quale viene pubblicato il materiale didattico.
 */
@Entity
@Table(name = "CORSO")
public class Corso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titolo;

    @Column(length = 1000)
    private String descrizione;

    /** Codice comunicato dal Docente agli Studenti per l'iscrizione autonoma (RF06). */
    @Column(name = "CODICE_UNIVOCO", nullable = false, unique = true, length = 20)
    private String codiceUnivoco;

    @Column(name = "ANNO_ACCADEMICO", length = 9)
    private String annoAccademico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "DOCENTE_ID", nullable = false)
    private Docente docenteTitolare;

    @OneToMany(mappedBy = "corso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sezione> sezioni = new ArrayList<>();

    @OneToMany(mappedBy = "corso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contenuto> contenuti = new ArrayList<>();

    @OneToMany(mappedBy = "corso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Iscrizione> iscrizioni = new ArrayList<>();

    /** Costruttore vuoto richiesto da JPA. */
    public Corso() {
    }

    public Corso(String titolo, String descrizione, String codiceUnivoco,
                 String annoAccademico, Docente docenteTitolare) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.codiceUnivoco = codiceUnivoco;
        this.annoAccademico = annoAccademico;
        this.docenteTitolare = docenteTitolare;
    }

    // Le tre collezioni qui sopra descrivono le associazioni del modello di dominio e
    // sono mappate dall'ORM, ma non vengono navigate dal codice applicativo.
    //
    // Il motivo e' architetturale: ogni operazione di persistenza apre e chiude il
    // proprio EntityManager (si veda GestorePersistenza), quindi gli oggetti restituiti
    // sono detached e le loro collezioni lazy non sono inizializzate; percorrerle fuori
    // dalla sessione solleverebbe LazyInitializationException. Le interrogazioni sulle
    // collezioni sono percio' esposte dai Registri del layer entity sotto forma di
    // query - contaContenutiPubblicati, cercaStudentiIscritti, cercaSezioniPerCorso -
    // coerentemente con il BCED, dove e' il package Entity a chiedere i dati al
    // package Database.

    public Long getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCodiceUnivoco() {
        return codiceUnivoco;
    }

    public String getAnnoAccademico() {
        return annoAccademico;
    }

    public Docente getDocenteTitolare() {
        return docenteTitolare;
    }
}
