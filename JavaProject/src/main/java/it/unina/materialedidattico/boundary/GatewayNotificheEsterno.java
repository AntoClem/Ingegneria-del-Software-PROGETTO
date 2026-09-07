package it.unina.materialedidattico.boundary;

/**
 * Servizio esterno di messaggistica (simulato).
 *
 * Rappresenta un componente di terze parti la cui interfaccia e' **incompatibile** con
 * quella attesa dalla logica di business: i parametri hanno nomi e ordine diversi e il
 * risultato e' un codice numerico invece di un valore booleano. Non puo' essere
 * modificato, perche' non e' codice nostro: e' esattamente la situazione che il pattern
 * Adapter risolve.
 *
 * In questa versione il recapito e' simulato con una stampa su console, cosi' il
 * comportamento resta osservabile durante la dimostrazione senza dipendere da un
 * server di posta reale.
 */
public class GatewayNotificheEsterno {

    /** Codice restituito quando il messaggio viene preso in carico. */
    public static final int ESITO_ACCETTATO = 0;

    /** Codice restituito quando il destinatario non e' valido. */
    public static final int ESITO_DESTINATARIO_NON_VALIDO = 2;

    /**
     * Firma del servizio esterno, diversa da quella attesa dal control.
     *
     * @param to   indirizzo del destinatario
     * @param subject oggetto del messaggio
     * @param body corpo del messaggio
     * @return codice di esito numerico
     */
    public int send(String to, String subject, String body) {
        if (to == null || !to.contains("@")) {
            return ESITO_DESTINATARIO_NON_VALIDO;
        }
        System.out.println("[servizio notifiche] a: " + to + " | " + subject + " | " + body);
        return ESITO_ACCETTATO;
    }
}
