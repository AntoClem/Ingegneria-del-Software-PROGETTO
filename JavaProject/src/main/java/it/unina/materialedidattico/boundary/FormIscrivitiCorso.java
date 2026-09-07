package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.UtenteDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Schermata di iscrizione autonoma a un Corso tramite codice univoco (UC6).
 *
 * Lo Studente non viene piu' "impersonato": arriva dalla sessione aperta con l'Accesso
 * ed e' passato dalla dashboard che apre questa schermata.
 */
public class FormIscrivitiCorso extends JDialog {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final UtenteDTO studente;
    private final DashboardStudente dashboard;

    private final JTextField txtCodiceCorso = new JTextField(18);

    public FormIscrivitiCorso(UtenteDTO studente, DashboardStudente dashboard) {
        // Finestra modale: finche' e' aperta, la dashboard sottostante non e' utilizzabile.
        // Evita che lo Studente lavori su un elenco di corsi non ancora aggiornato.
        super(dashboard, "Iscriviti a un corso", true);
        this.studente = studente;
        this.dashboard = dashboard;

        JPanel pannello = StileGUI.pannelloBase(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        pannello.add(StileGUI.titolo("Iscrizione a un corso"), gbc);

        gbc.gridy = 1;
        pannello.add(StileGUI.sottotitolo(
                "Inserisci il codice comunicato dal docente (8-12 caratteri alfanumerici)"), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        pannello.add(new JLabel("Codice del corso"), gbc);
        gbc.gridx = 1;
        pannello.add(txtCodiceCorso, gbc);

        JButton btnIscriviti = StileGUI.bottonePrimario("Iscriviti");
        JButton btnChiudi = StileGUI.bottone("Chiudi");
        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnChiudi);
        bottoni.add(btnIscriviti);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 0, 8);
        pannello.add(bottoni, gbc);

        setContentPane(pannello);
        pack();
        setResizable(false);
        setLocationRelativeTo(dashboard);
        getRootPane().setDefaultButton(btnIscriviti);

        btnIscriviti.addActionListener(e -> gestisciIscrizione());
        btnChiudi.addActionListener(e -> dispose());
    }

    /**
     * Esegue l'iscrizione. In caso di successo aggiorna subito l'elenco dei corsi della
     * dashboard e chiude la finestra: lo Studente vede il nuovo corso comparire senza
     * dover uscire e rientrare.
     */
    private void gestisciIscrizione() {
        try {
            CorsoDTO corso = facade.iscrivitiCorso(studente.getId(), txtCodiceCorso.getText());

            dashboard.aggiornaElencoCorsi();
            StileGUI.conferma(this, "Iscrizione effettuata al corso:\n" + corso.getTitolo());
            dispose();

        } catch (IllegalArgumentException e) {
            StileGUI.errore(this, e.getMessage());
            txtCodiceCorso.requestFocusInWindow();
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
        }
    }
}
