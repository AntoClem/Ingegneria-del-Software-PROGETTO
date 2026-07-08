package dto;

import java.util.Map;

/**
 * DTO che riassume l'andamento di un corso, cosi' come mostrato al
 * docente nell'interfaccia dedicata di monitoraggio.
 */
public class AndamentoCorsoDTO {

    private String titoloCorso;
    private int numeroContenutiPubblicati;
    private int numeroStudentiIscritti;
    private Map<String, Integer> distribuzionePerCategoria;

    public AndamentoCorsoDTO(String titoloCorso, int numeroContenutiPubblicati,
                              int numeroStudentiIscritti, Map<String, Integer> distribuzionePerCategoria) {
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
