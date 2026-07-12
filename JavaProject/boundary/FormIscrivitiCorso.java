package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.IscrivitiCorsoController;
import it.unina.materialedidattico.control.IscrivitiCorsoController.EsitoIscrizione;
import it.unina.materialedidattico.entity.Studente;
import it.unina.materialedidattico.utility.Validazione;

import javax.swing.*;
import java.awt.*;

/**
 * SCHELETRO Boundary del caso d'uso IscrivitiCorso (UC6).
 *
 * Questo file contiene solo la struttura Java minima (dichiarazione dei
 * campi e collegamento al Controller): la costruzione grafica vera e
 * propria (posizionamento di componenti, margini, layout) va completata
 * in IntelliJ con lo Swing UI Designer, seguendo la procedura descritta
 * nella slide 16_BCED_temp_1.pdf ("Creare la GUI Form nel package
 * boundary"). Una volta fatto, il designer generera' qui il codice
 * di inizializzazione dei componenti (contentPane, ecc.): non
 * cancellare questa struttura, va estesa.
 */
public class FormIscrivitiCorso extends JFrame {

    private JPanel contentPane;
    private JTextField txtCodiceCorso;
    private JButton btnIscriviti;
    private JLabel lblEsito;

    private final IscrivitiCorsoController controller = new IscrivitiCorsoController();
    private final Studente studenteLoggato; // valorizzato dopo il login (fuori scope di questo UC)

    public FormIscrivitiCorso(Studente studenteLoggato) {
        this.studenteLoggato = studenteLoggato;
        setTitle("Iscriviti a un Corso");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnIscriviti.addActionListener(e -> gestisciIscrizione());
    }

    private void gestisciIscrizione() {
        String codiceCorso = txtCodiceCorso.getText();

        // Validazione di formato lato Boundary (vedi utility.Validazione)
        if (!Validazione.verificaCodiceCorso(codiceCorso)) {
            lblEsito.setText("Inserire il codice del Corso.");
            lblEsito.setForeground(Color.RED);
            return;
        }

        EsitoIscrizione esito = controller.iscrivitiCorso(studenteLoggato, codiceCorso);
        switch (esito) {
            case SUCCESSO:
                lblEsito.setText("Iscrizione effettuata con successo.");
                lblEsito.setForeground(new Color(0, 128, 0));
                break;
            case STUDENTE_GIA_ISCRITTO:
                lblEsito.setText("Risulti gia' iscritto a questo Corso.");
                lblEsito.setForeground(Color.ORANGE);
                break;
            case CORSO_INESISTENTE:
                lblEsito.setText("Codice Corso non valido: nessun Corso trovato.");
                lblEsito.setForeground(Color.RED);
                break;
        }
    }
}
