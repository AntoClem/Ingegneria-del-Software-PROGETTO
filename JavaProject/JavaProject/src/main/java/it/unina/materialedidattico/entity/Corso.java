package it.unina.materialedidattico.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Corso creato e gestito da un Docente.
 *
 * A livello di progettazione la classe conserva le liste sezioni,
 * contenuti e iscrizioni come cache in memoria dell'aggregato "Corso":
 * queste liste NON sono la fonte di verita' persistente (che e' il
 * database, gestito dalle classi DAO), ma vengono popolate dal Control
 * dopo una lettura dal DB, oppure aggiornate incrementalmente subito
 * dopo un salvataggio (vedi aggiungiContenuto/aggiungiIscrizione,
 * richiamati dai Controller nei diagrammi di sequenza di progetto).
 * I metodi pubblicaContenuto(...) e iscriviStudente(...) presenti nel
 * modello di analisi sono stati rimossi: creare un Contenuto o una
 * Iscrizione e salvarli sul DB e' ora compito del Control.
 */
public class Corso {

    private int id;
    private String titolo;
    private String descrizione;
    private String codiceUnivoco;
    private String annoAccademico;
    private Docente docenteTitolare;
    private List<Sezione> sezioni = new ArrayList<>();
    private List<Contenuto> contenuti = new ArrayList<>();
    private List<Iscrizione> iscrizioni = new ArrayList<>();

    public Corso(String titolo, String descrizione, String codiceUnivoco, String annoAccademico, Docente docenteTitolare) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.codiceUnivoco = codiceUnivoco;
        this.annoAccademico = annoAccademico;
        this.docenteTitolare = docenteTitolare;
    }

    /** Aggiunge un Contenuto gia' persistito alla cache in memoria del Corso. */
    public void aggiungiContenuto(Contenuto contenuto) {
        contenuti.add(contenuto);
    }

    /** Aggiunge una Sezione gia' persistita alla cache in memoria del Corso. */
    public void aggiungiSezione(Sezione sezione) {
        sezioni.add(sezione);
    }

    /** Aggiunge una Iscrizione gia' persistita alla cache in memoria del Corso. */
    public void aggiungiIscrizione(Iscrizione iscrizione) {
        iscrizioni.add(iscrizione);
    }

    /**
     * Filtra i Contenuti gia' caricati in memoria per Categoria.
     * Information Expert: il Corso e' l'oggetto che possiede la
     * collezione contenuti, quindi e' l'oggetto piu' indicato a
     * eseguire il filtro (in alternativa il filtro puo' essere
     * delegato direttamente alla query SQL in ContenutoDAO, vedi
     * ContenutoDAO.cercaConFiltri: scelta adottata nei diagrammi di
     * sequenza di progetto per evitare di caricare in memoria
     * l'intera lista di Contenuti quando non necessario).
     */
    public List<Contenuto> filtraContenuti(Categoria categoria) {
        List<Contenuto> risultato = new ArrayList<>();
        for (Contenuto c : contenuti) {
            if (c.getCategoria() == categoria) {
                risultato.add(c);
            }
        }
        return risultato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Sezione> getSezioni() {
        return sezioni;
    }

    public List<Contenuto> getContenuti() {
        return contenuti;
    }

    public List<Iscrizione> getIscrizioni() {
        return iscrizioni;
    }
}
