package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.Main;
import it.unina.materialedidattico.control.IscrivitiCorsoController;
import it.unina.materialedidattico.control.IscrivitiCorsoController.EsitoIscrizione;
import it.unina.materialedidattico.entity.Studente;
import it.unina.materialedidattico.utility.Validazione;

import javax.swing.*;
import java.awt.*;

public class FormIscrivitiCorso extends JFrame {

    private JPanel contentPane;
    private JTextField txtCodiceCorso;
    private JButton btnIscriviti;
    private JLabel lblEsito;

    private final IscrivitiCorsoController controller = new IscrivitiCorsoController();
    private final Studente studenteLoggato;

    public FormIscrivitiCorso(Studente studenteLoggato) {
        this.studenteLoggato = studenteLoggato;
        setTitle("Iscriviti a un Corso");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblIntro = new JLabel("Studente: " + studenteLoggato.getNome() + " " + studenteLoggato.getCognome());
        lblIntro.setFont(lblIntro.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPane.add(lblIntro, gbc);

        JLabel lblCodice = new JLabel("Codice del Corso:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        contentPane.add(lblCodice, gbc);

        txtCodiceCorso = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPane.add(txtCodiceCorso, gbc);

        btnIscriviti = new JButton("Iscriviti");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPane.add(btnIscriviti, gbc);

        lblEsito = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        contentPane.add(lblEsito, gbc);

        JButton btnHome = new JButton("Home");
        btnHome.addActionListener(e -> Main.mostraMenu());
        JPanel pannelloToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pannelloToolbar.add(btnHome);

        JPanel radice = new JPanel(new BorderLayout());
        radice.add(pannelloToolbar, BorderLayout.NORTH);
        radice.add(contentPane, BorderLayout.CENTER);

        setContentPane(radice);
        pack();
        setLocationRelativeTo(null);

        btnIscriviti.addActionListener(e -> gestisciIscrizione());
        txtCodiceCorso.addActionListener(e -> gestisciIscrizione());
    }

    private void gestisciIscrizione() {
        String codiceCorso = txtCodiceCorso.getText();

        if (!Validazione.verificaCodiceCorso(codiceCorso)) {
            lblEsito.setText("Inserire un codice Corso valido (8-12 caratteri alfanumerici).");
            lblEsito.setForeground(Color.RED);
            return;
        }

        EsitoIscrizione esito = controller.iscrivitiCorso(studenteLoggato, codiceCorso);
        switch (esito) {
            case SUCCESSO:
                lblEsito.setText("Iscrizione effettuata con successo.");
                lblEsito.setForeground(new Color(0, 128, 0));
                txtCodiceCorso.setText("");
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
