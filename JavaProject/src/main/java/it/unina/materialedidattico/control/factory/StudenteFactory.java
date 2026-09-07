package it.unina.materialedidattico.control.factory;

import it.unina.materialedidattico.entity.Studente;
import it.unina.materialedidattico.entity.Utente;

/** Factory concreta che istanzia uno Studente (UC1). */
public class StudenteFactory extends UtenteFactory {

    @Override
    public Utente creaUtente(String nome, String cognome, String emailIstituzionale, String password) {
        return new Studente(nome, cognome, emailIstituzionale, password);
    }
}
