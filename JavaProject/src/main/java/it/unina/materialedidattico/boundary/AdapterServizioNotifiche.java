package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.ServizioNotifiche;

/**
 * Adattatore fra l'interfaccia attesa dal control (ServizioNotifiche) e quella offerta
 * dal servizio esterno di messaggistica (GatewayNotificheEsterno).
 *
 * E' un **object adapter**: usa la composizione, non l'ereditarieta', quindi non
 * dipende dai dettagli implementativi del servizio adattato e potrebbe adattarne
 * altri in futuro. Traduce la chiamata invia(...) nella send(...) del gateway e
 * converte il codice numerico di ritorno nel booleano atteso dalla logica di business.
 *
 * Grazie a questa classe, GestoreNotifiche dipende soltanto dall'astrazione
 * ServizioNotifiche e resta isolato dal fornitore concreto del servizio.
 */
public class AdapterServizioNotifiche implements ServizioNotifiche {

    private final GatewayNotificheEsterno gateway = new GatewayNotificheEsterno();

    @Override
    public boolean invia(String destinatario, String oggetto, String messaggio) {
        int esito = gateway.send(destinatario, oggetto, messaggio);
        return esito == GatewayNotificheEsterno.ESITO_ACCETTATO;
    }
}
