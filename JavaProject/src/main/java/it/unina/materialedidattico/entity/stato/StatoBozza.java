package it.unina.materialedidattico.entity.stato;

import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.StatoContenutoEnum;

/**
 * Contenuto salvato dal Docente ma non ancora reso visibile agli Studenti (RF10).
 * E' l'unico stato da cui la pubblicazione sia ammessa.
 */
public class StatoBozza implements StatoContenuto {

    private static StatoBozza istanza;

    private StatoBozza() {
    }

    public static synchronized StatoBozza getIstanza() {
        if (istanza == null) {
            istanza = new StatoBozza();
        }
        return istanza;
    }

    @Override
    public void pubblica(Contenuto contenuto) {
        contenuto.cambiaStato(StatoPubblicato.getIstanza());
        // La transizione verso "pubblicato" e' l'evento che fa scattare le Notifiche
        // agli Studenti iscritti: il Contenuto avvisa i propri osservatori (pattern Observer).
        contenuto.notificaObservers();
    }

    @Override
    public void rimuovi(Contenuto contenuto) {
        contenuto.cambiaStato(StatoRimosso.getIstanza());
    }

    @Override
    public boolean isVisibile() {
        return false;
    }

    @Override
    public StatoContenutoEnum getCodice() {
        return StatoContenutoEnum.BOZZA;
    }
}
