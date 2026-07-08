package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un docente, che può creare e gestire uno o più corsi.
 */
public class Docente extends Utente {

    private static final long serialVersionUID = 1L;

    private List<Corso> corsiGestiti = new ArrayList<>();

    public Docente() {
        super();
    }

    public Docente(int id, String nome, String cognome, String emailIstituzionale,
                   String username, String password) {
        super(id, nome, cognome, emailIstituzionale, username, password);
    }

    @Override
    public RuoloUtente getRuolo() {
        return RuoloUtente.DOCENTE;
    }

    public List<Corso> getCorsiGestiti() {
        return corsiGestiti;
    }

    public void setCorsiGestiti(List<Corso> corsiGestiti) {
        this.corsiGestiti = corsiGestiti;
    }

    public void aggiungiCorso(Corso corso) {
        this.corsiGestiti.add(corso);
    }
}
