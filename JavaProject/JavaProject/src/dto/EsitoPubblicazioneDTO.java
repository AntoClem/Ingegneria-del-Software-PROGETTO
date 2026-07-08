package dto;

/**
 * Esito dell'operazione di pubblicazione di un contenuto (UC7 - PubblicaContenuto).
 * Riflette i due possibili messaggi di ritorno del diagramma di sequenza:
 * "erroreDatiObbligatori" oppure "confermaPubblicazione".
 */
public class EsitoPubblicazioneDTO {

    private final boolean successo;
    private final String messaggio;
    private final Integer contenutoId; // valorizzato solo se successo == true

    private EsitoPubblicazioneDTO(boolean successo, String messaggio, Integer contenutoId) {
        this.successo = successo;
        this.messaggio = messaggio;
        this.contenutoId = contenutoId;
    }

    public static EsitoPubblicazioneDTO errore(String messaggio) {
        return new EsitoPubblicazioneDTO(false, messaggio, null);
    }

    public static EsitoPubblicazioneDTO confermato(int contenutoId, String messaggio) {
        return new EsitoPubblicazioneDTO(true, messaggio, contenutoId);
    }

    public boolean isSuccesso() {
        return successo;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public Integer getContenutoId() {
        return contenutoId;
    }
}
