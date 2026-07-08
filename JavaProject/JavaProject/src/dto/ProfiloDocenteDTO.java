package dto;

import java.util.List;

/**
 * DTO per la visualizzazione del profilo di un docente associato a un corso,
 * comprensivo di nome e dell'elenco dei corsi gestiti.
 */
public class ProfiloDocenteDTO {

    private String nomeCompleto;
    private List<String> corsiGestiti;

    public ProfiloDocenteDTO(String nomeCompleto, List<String> corsiGestiti) {
        this.nomeCompleto = nomeCompleto;
        this.corsiGestiti = corsiGestiti;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public List<String> getCorsiGestiti() {
        return corsiGestiti;
    }
}
