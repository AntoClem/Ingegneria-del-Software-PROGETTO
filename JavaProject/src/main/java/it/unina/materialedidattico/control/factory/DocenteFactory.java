package it.unina.materialedidattico.control.factory;

import it.unina.materialedidattico.entity.Docente;
import it.unina.materialedidattico.entity.Utente;

/** Factory concreta che istanzia un Docente (UC1). */
public class DocenteFactory extends UtenteFactory {

    @Override
    public Utente creaUtente(String nome, String cognome, String emailIstituzionale, String password) {
        return new Docente(nome, cognome, emailIstituzionale, password);
    }
}
