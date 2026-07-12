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
 * All'avvio:
 * 1) inizializza DBManager (Singleton), che alla prima connessione crea
 *    automaticamente lo schema del database H2 se non gia' presente
 *    (vedi sql/schema.sql, copiato in src/main/resources/schema.sql);
 * 2) mostra un semplice menu di scelta per aprire una delle 4 GUI Swing
 *    dei casi d'uso sviluppati in dettaglio, "impersonando" uno degli
 *    utenti d'esempio caricati manualmente da sql/seed_data.sql
 *    (il caso d'uso Accesso/Login non fa parte dei 4 scelti dal gruppo
 *    per lo sviluppo di dettaglio, quindi qui non viene implementata
 *    una vera autenticazione).
 *
 * NOTA PER IL TEAM: prima di eseguire questa classe la prima volta,
 * eseguire manualmente sql/seed_data.sql sul database (es. dalla
 * H2 Console di IntelliJ, Database Tool Window), altrimenti il menu
 * qui sotto non trova utenti/corsi d'esempio da usare.
 *
 * Ciascuna Form* nel package boundary e', al momento, solo uno
 * scheletro (il campo contentPane non e' ancora inizializzato):
 * finche' non completate la costruzione grafica in IntelliJ con lo
 * Swing UI Designer (vedi 16_BCED_temp_1.pdf, "Creare la GUI Form nel
 * package boundary"), aprirla da qui restituisce un errore gestito
 * (messaggio in console), non un crash silenzioso.
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
            System.err.println("Impossibile aprire la GUI selezionata: " + e.getMessage());
            System.err.println("Verificare di aver completato la Form in IntelliJ (Swing UI Designer) "
                    + "e di aver eseguito sql/seed_data.sql sul database.");
        }
    }

    private static void terminaApplicazione() {
        DBManager.getIstanza().chiudi();
        System.exit(0);
    }
}
