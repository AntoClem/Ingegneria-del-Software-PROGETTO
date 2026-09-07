package it.unina.materialedidattico.dto;

import java.util.Map;

/**
 * Data Transfer Object che aggrega i tre dati calcolati dal caso d'uso
 * MonitoraAndamentoCorso (UC15): numero di Contenuti pubblicati, numero di Studenti
 * iscritti e distribuzione dei Contenuti per Categoria.
 *
 * Aggregarli in un solo oggetto evita che la Boundary debba effettuare tre chiamate
 * separate alla Facade per riempire un'unica schermata. Le Categorie sono chiavi di
 * tipo String per non esporre alla Boundary l'enum del layer Entity.
 */
public class RiepilogoAndamentoDTO {

    private final String titoloCorso;
    private final int numeroContenutiPubblicati;
    private final int numeroStudentiIscritti;
    private final Map<String, Integer> distribuzionePerCategoria;

    public RiepilogoAndamentoDTO(String titoloCorso, int numeroContenutiPubblicati,
                                 int numeroStudentiIscritti,
                                 Map<String, Integer> distribuzionePerCategoria) {
        this.titoloCorso = titoloCorso;
        this.numeroContenutiPubblicati = numeroContenutiPubblicati;
        this.numeroStudentiIscritti = numeroStudentiIscritti;
        this.distribuzionePerCategoria = distribuzionePerCategoria;
    }

    public String getTitoloCorso() {
        return titoloCorso;
    }

    public int getNumeroContenutiPubblicati() {
        return numeroContenutiPubblicati;
    }

    public int getNumeroStudentiIscritti() {
        return numeroStudentiIscritti;
    }

    public Map<String, Integer> getDistribuzionePerCategoria() {
        return distribuzionePerCategoria;
    }
}
