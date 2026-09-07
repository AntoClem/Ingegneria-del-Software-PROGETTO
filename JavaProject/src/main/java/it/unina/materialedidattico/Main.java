package it.unina.materialedidattico;

import it.unina.materialedidattico.boundary.AdapterServizioNotifiche;
import it.unina.materialedidattico.boundary.FormLogin;
import it.unina.materialedidattico.boundary.StileGUI;
import it.unina.materialedidattico.control.GestoreNotifiche;

import javax.swing.SwingUtilities;

/**
 * Punto di ingresso dell'applicazione.
 *
 * A differenza della versione precedente del progetto, qui non esiste alcun menu che
 * "impersona" un utente prelevato dai dati di esempio: l'unica schermata che si apre
 * all'avvio e' quella di accesso, e nessuna funzionalita' e' raggiungibile senza che
 * l'Accesso (UC2) sia andato a buon fine (vincolo V05).
 *
 * La connessione al database viene aperta da JpaUtil alla prima operazione di
 * persistenza e chiusa dallo shutdown hook registrato al suo interno.
 */
public class Main {

    public static void main(String[] args) {
        // Composizione delle dipendenze: il control dichiara l'astrazione
        // ServizioNotifiche, il boundary ne fornisce la realizzazione concreta
        // adattando il servizio esterno di messaggistica (pattern Adapter).
        GestoreNotifiche.getIstanza().setServizioNotifiche(new AdapterServizioNotifiche());

        // Aspetto uniforme di tutte le schermate: va impostato prima di costruirne una.
        StileGUI.applica();

        // Swing non e' thread-safe: la GUI va costruita sull'Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
    }
}
