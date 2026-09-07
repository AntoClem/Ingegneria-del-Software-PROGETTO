package it.unina.materialedidattico.entity.stato;

import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.StatoContenutoEnum;

/**
 * Contenuto visibile agli Studenti iscritti al Corso.
 * Da qui e' ammessa solo la rimozione: ripubblicare un materiale gia' pubblicato
 * non ha significato e genererebbe una seconda ondata di Notifiche.
 */
public class StatoPubblicato implements StatoContenuto {

    private static StatoPubblicato istanza;

    private StatoPubblicato() {
    }

    public static synchronized StatoPubblicato getIstanza() {
        if (istanza == null) {
            istanza = new StatoPubblicato();
        }
        return istanza;
    }

    @Override
    public void pubblica(Contenuto contenuto) {
        throw new IllegalStateException("Il materiale risulta gia' pubblicato.");
    }

    @Override
    public void rimuovi(Contenuto contenuto) {
        contenuto.cambiaStato(StatoRimosso.getIstanza());
    }

    @Override
    public boolean isVisibile() {
        return true;
    }

    @Override
    public StatoContenutoEnum getCodice() {
        return StatoContenutoEnum.PUBBLICATO;
    }
}
