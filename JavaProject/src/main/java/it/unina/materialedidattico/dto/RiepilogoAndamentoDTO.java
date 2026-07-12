package it.unina.materialedidattico.dto;

import it.unina.materialedidattico.entity.Categoria;

import java.util.Map;

/**
 * Data Transfer Object che aggrega i risultati del calcolo effettuato
 * da MonitoraAndamentoCorsoController, evitando che la Boundary debba
 * effettuare piu' chiamate separate al Controller o conoscere le
 * classi DAO da cui i dati provengono.
 */
public class RiepilogoAndamentoDTO {

    private final int numeroContenutiPubblicati;
    private final int numeroStudentiIscritti;
    private final Map<Categoria, Integer> distribuzionePerCategoria;

    public RiepilogoAndamentoDTO(int numeroContenutiPubblicati, int numeroStudentiIscritti,
                                  Map<Categoria, Integer> distribuzionePerCategoria) {
        this.numeroContenutiPubblicati = numeroContenutiPubblicati;
        this.numeroStudentiIscritti = numeroStudentiIscritti;
        this.distribuzionePerCategoria = distribuzionePerCategoria;
    }

    public int getNumeroContenutiPubblicati() {
        return numeroContenutiPubblicati;
    }

    public int getNumeroStudentiIscritti() {
        return numeroStudentiIscritti;
    }

    public Map<Categoria, Integer> getDistribuzionePerCategoria() {
        return distribuzionePerCategoria;
    }
}
