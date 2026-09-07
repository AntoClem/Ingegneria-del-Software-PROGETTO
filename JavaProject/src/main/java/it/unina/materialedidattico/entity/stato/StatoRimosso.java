package it.unina.materialedidattico.entity.stato;

import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.StatoContenutoEnum;

/**
 * Contenuto rimosso dal Docente: non e' piu' visibile agli Studenti (RF12).
 * E' uno stato finale: la traccia stabilisce che un materiale eliminato non torni
 * disponibile, quindi da qui non e' ammessa alcuna transizione.
 */
public class StatoRimosso implements StatoContenuto {

    private static StatoRimosso istanza;

    private StatoRimosso() {
    }

    public static synchronized StatoRimosso getIstanza() {
        if (istanza == null) {
            istanza = new StatoRimosso();
        }
        return istanza;
    }

    @Override
    public void pubblica(Contenuto contenuto) {
        throw new IllegalStateException("Il materiale e' stato rimosso e non puo' essere ripubblicato.");
    }

    @Override
    public void rimuovi(Contenuto contenuto) {
        throw new IllegalStateException("Il materiale risulta gia' rimosso.");
    }

    @Override
    public boolean isVisibile() {
        return false;
    }

    @Override
    public StatoContenutoEnum getCodice() {
        return StatoContenutoEnum.RIMOSSO;
    }
}
