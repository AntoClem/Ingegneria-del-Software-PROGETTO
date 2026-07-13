package it.unina.materialedidattico;

import it.unina.materialedidattico.boundary.FormIscrivitiCorso;
import it.unina.materialedidattico.boundary.FormMonitoraAndamentoCorso;
import it.unina.materialedidattico.boundary.FormPubblicaContenuto;
import it.unina.materialedidattico.boundary.FormVisualizzaContenutiCorso;
import it.unina.materialedidattico.database.CorsoDAO;
import it.unina.materialedidattico.database.CorsoDAOH2Impl;
import it.unina.materialedidattico.database.DBManager;
import it.unina.materialedidattico.database.StudenteDAO;
import it.unina.materialedidattico.database.StudenteDAOH2Impl;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Studente;

import javax.swing.*;

/**
 * Punto di ingresso dell'applicazione.
 *
 * All'avvio inizializza DBManager (Singleton), che alla prima connessione
 * crea automaticamente lo schema del database H2 se non gia' presente, poi
 * mostra un menu per aprire una delle 4 GUI Swing dei casi d'uso sviluppati,
 * "impersonando" uno degli utenti d'esempio caricati da sql/seed_data.sql
 * (il caso d'uso Accesso/Login non fa parte dei 4 sviluppati in dettaglio,
 * quindi qui non viene implementata una vera autenticazione).
 *
 * Prima del primo avvio va eseguito sql/seed_data.sql sul database (dalla
 * H2 Console di IntelliJ), altrimenti il menu non trova utenti e Corsi
 * di esempio da usare.
 */
public class Main {

    public static void main(String[] args) {
        DBManager.getIstanza();
        System.out.println("Connessione al database H2 stabilita, schema verificato/creato.");

        SwingUtilities.invokeLater(Main::apriMenu);
    }

    private static void apriMenu() {
        String[] opzioni = {
                "IscrivitiCorso (Studente)",
                "PubblicaContenuto (Docente)",
                "MonitoraAndamentoCorso (Docente)",
                "VisualizzaContenutiCorso (Studente)"
        };
        int scelta = JOptionPane.showOptionDialog(null,
                "Seleziona la funzionalita' da aprire.\n" +
                        "(richiede i dati d'esempio di sql/seed_data.sql gia' caricati)",
                "Piattaforma di Gestione e Condivisione di Materiale Didattico",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opzioni, opzioni[0]);

        if (scelta < 0) {
            terminaApplicazione();
            return;
        }

        StudenteDAO studenteDAO = new StudenteDAOH2Impl();
        CorsoDAO corsoDAO = new CorsoDAOH2Impl();

        try {
            switch (scelta) {
                case 0:
                    Studente studenteDemo = studenteDAO.cercaPerId(3); // Giovanni Aliperta, vedi seed_data.sql
                    new FormIscrivitiCorso(studenteDemo).setVisible(true);
                    break;
                case 1:
                    Corso corsoDocente = corsoDAO.cercaPerId(1); // Ingegneria del Software, vedi seed_data.sql
                    new FormPubblicaContenuto(corsoDocente).setVisible(true);
                    break;
                case 2:
                    Corso corsoMonitorato = corsoDAO.cercaPerId(1);
                    new FormMonitoraAndamentoCorso(corsoMonitorato).setVisible(true);
                    break;
                case 3:
                    Corso corsoVisualizzato = corsoDAO.cercaPerId(1);
                    new FormVisualizzaContenutiCorso(corsoVisualizzato).setVisible(true);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Impossibile aprire la schermata selezionata: " + e.getMessage());
            System.err.println("Verificare di aver eseguito sql/seed_data.sql sul database.");
        }
    }

    private static void terminaApplicazione() {
        DBManager.getIstanza().chiudi();
        System.exit(0);
    }
}
