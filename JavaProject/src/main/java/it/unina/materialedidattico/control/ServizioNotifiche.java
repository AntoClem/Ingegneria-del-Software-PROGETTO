package it.unina.materialedidattico.control;

/**
 * Servizio di recapito delle notifiche agli Studenti (vincolo V04).
 *
 * L'interfaccia e' dichiarata qui, nel layer che ne ha bisogno, ed espone la firma
 * che serve alla logica di business. Chi la realizza concretamente - e con quale
 * tecnologia il messaggio venga poi recapitato - resta fuori dal control: e' la
 * Boundary a fornirne una realizzazione (pattern Adapter).
 */
public interface ServizioNotifiche {

    /**
     * Recapita un messaggio a un destinatario.
     *
     * @param destinatario email istituzionale dello Studente
     * @param oggetto      oggetto del messaggio
     * @param messaggio    corpo del messaggio
     * @return true se il servizio ha preso in carico il messaggio
     */
    boolean invia(String destinatario, String oggetto, String messaggio);
}
