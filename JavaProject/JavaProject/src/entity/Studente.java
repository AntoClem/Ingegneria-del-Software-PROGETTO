package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta uno studente, che può iscriversi a uno o più corsi
 * e consultarne i contenuti didattici.
 */
public class Studente extends Utente {

    private static final long serialVersionUID = 1L;

    private List<Corso> corsiIscritti = new ArrayList<>();

    public Studente() {
        super();
    }

    public Studente(int id, String nome, String cognome, String emailIstituzionale,
                    String username, String password) {
        super(id, nome, cognome, emailIstituzionale, username, password);
    }

    @Override
    public RuoloUtente getRuolo() {
        return RuoloUtente.STUDENTE;
    }

    public List<Corso> getCorsiIscritti() {
        return corsiIscritti;
    }

    public void setCorsiIscritti(List<Corso> corsiIscritti) {
        this.corsiIscritti = corsiIscritti;
    }

    public void aggiungiCorso(Corso corso) {
        this.corsiIscritti.add(corso);
    }
}
