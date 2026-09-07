package it.unina.materialedidattico.entity;

import it.unina.materialedidattico.entity.stato.StatoBozza;
import it.unina.materialedidattico.entity.stato.StatoContenuto;
import it.unina.materialedidattico.entity.stato.StatoPubblicato;
import it.unina.materialedidattico.entity.stato.StatoRimosso;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Materiale didattico pubblicato dal Docente all'interno di un Corso (RD04).
 *
 * Applica due pattern:
 * - **State**: il ciclo di vita del materiale (bozza, pubblicato, rimosso) e' governato
 *   dalle classi del package entity.stato, invece che da un flag booleano con i
 *   controlli sparsi nei metodi. Le operazioni pubblica() e rimuovi() delegano allo
 *   stato corrente, che sa quali transizioni siano ammesse a partire da se'.
 * - **Observer**: estendendo Subject, il Contenuto avvisa i propri osservatori quando
 *   diventa visibile, ed e' cosi' che partono le Notifiche agli Studenti (RF22).
 *
 * Sul database viene memorizzato il solo codice dello stato (colonna STATO): l'oggetto
 * stato corrispondente viene ricostruito alla prima richiesta.
 */
@Entity
@Table(name = "CONTENUTO")
public class Contenuto extends Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titolo;

    @Column(length = 2000)
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Categoria categoria;

    @Column(name = "DATA_PUBBLICAZIONE", nullable = false)
    private LocalDate dataPubblicazione;

    /** Codice dello stato corrente: e' l'unica parte dello stato che viene persistita. */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATO", nullable = false, length = 20)
    private StatoContenutoEnum codiceStato;

    /** Oggetto-stato corrispondente al codice: ricostruito in memoria, non persistito. */
    @Transient
    private StatoContenuto stato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CORSO_ID", nullable = false)
    private Corso corso;

    /** Puo' essere null: un Contenuto non deve appartenere per forza a una Sezione (RF09). */
    @ManyToOne
    @JoinColumn(name = "SEZIONE_ID")
    private Sezione sezione;

    /** Costruttore vuoto richiesto da JPA. */
    public Contenuto() {
    }

    /**
     * Un nuovo Contenuto nasce sempre come bozza: la pubblicazione e' una transizione
     * esplicita, richiesta dal Docente, e non un valore passato al costruttore.
     */
    public Contenuto(String titolo, String descrizione, Categoria categoria,
                     Corso corso, Sezione sezione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.corso = corso;
        this.sezione = sezione;
        this.dataPubblicazione = LocalDate.now();
        this.codiceStato = StatoContenutoEnum.BOZZA;
        this.stato = StatoBozza.getIstanza();
    }

    // ------------------------------------------------------------ pattern State

    /** Stato corrente, ricostruito dal codice persistito alla prima richiesta. */
    public StatoContenuto getStato() {
        if (stato == null) {
            stato = risolviStato(codiceStato);
        }
        return stato;
    }

    /**
     * Rende visibile il materiale agli Studenti e avvisa gli osservatori.
     * @throws IllegalStateException se il materiale e' gia' pubblicato o rimosso.
     */
    public void pubblica() {
        getStato().pubblica(this);
    }

    /**
     * Rimuove il materiale: non sara' piu' visibile agli Studenti (RF12).
     * @throws IllegalStateException se il materiale risulta gia' rimosso.
     */
    public void rimuovi() {
        getStato().rimuovi(this);
    }

    /** True se, nello stato corrente, il materiale e' visibile agli Studenti. */
    public boolean isVisibile() {
        return getStato().isVisibile();
    }

    /**
     * Esegue la transizione di stato. E' invocato soltanto dalle classi del package
     * entity.stato: sono loro a stabilire quali transizioni siano ammesse, mentre qui
     * si registrano il nuovo stato e la data in cui il materiale e' diventato visibile.
     */
    public void cambiaStato(StatoContenuto nuovoStato) {
        this.stato = nuovoStato;
        this.codiceStato = nuovoStato.getCodice();
        if (nuovoStato.isVisibile()) {
            this.dataPubblicazione = LocalDate.now();
        }
    }

    /** Rende pubblica la notifica agli osservatori, invocata dallo stato dopo la transizione. */
    public void notificaObservers() {
        notifica(this);
    }

    private StatoContenuto risolviStato(StatoContenutoEnum codice) {
        if (codice == null) {
            return StatoBozza.getIstanza();
        }
        switch (codice) {
            case PUBBLICATO:
                return StatoPubblicato.getIstanza();
            case RIMOSSO:
                return StatoRimosso.getIstanza();
            default:
                return StatoBozza.getIstanza();
        }
    }

    // ------------------------------------------------------------ accessor

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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataPubblicazione() {
        return dataPubblicazione;
    }

    public StatoContenutoEnum getCodiceStato() {
        return codiceStato;
    }

    public Corso getCorso() {
        return corso;
    }

    public Sezione getSezione() {
        return sezione;
    }

    public void setSezione(Sezione sezione) {
        this.sezione = sezione;
    }
}
