package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.UtenteDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Schermata di registrazione di un nuovo Utente (UC1).
 * Il ruolo scelto nel menu a tendina determina, nel Control, quale Factory istanziera'
 * la sottoclasse concreta di Utente.
 */
public class FormRegistrazione extends JFrame {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();

    private final JComboBox<String> cmbRuolo = new JComboBox<>(new String[]{"Studente", "Docente"});
    private final JTextField txtNome = new JTextField(24);
    private final JTextField txtCognome = new JTextField(24);
    private final JTextField txtEmail = new JTextField(24);
    private final JPasswordField txtPassword = new JPasswordField(24);

    public FormRegistrazione() {
        setTitle("Piattaforma di Materiale Didattico - Registrazione");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel pannello = StileGUI.pannelloBase(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        pannello.add(StileGUI.titolo("Crea un nuovo account"), gbc);

        gbc.gridy = 1;
        pannello.add(StileGUI.sottotitolo("Serve un'email istituzionale del dominio unina.it"), gbc);

        gbc.gridwidth = 1;
        int riga = 2;
        riga = campo(pannello, gbc, riga, "Ruolo", cmbRuolo);
        riga = campo(pannello, gbc, riga, "Nome", txtNome);
        riga = campo(pannello, gbc, riga, "Cognome", txtCognome);
        riga = campo(pannello, gbc, riga, "Email istituzionale", txtEmail);
        riga = campo(pannello, gbc, riga, "Password (8-32 caratteri)", txtPassword);

        JButton btnRegistrati = StileGUI.bottonePrimario("Registrati");
        gbc.gridx = 0;
        gbc.gridy = riga++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        pannello.add(btnRegistrati, gbc);

        JButton btnTorna = StileGUI.collegamento("Torna all'accesso");
        gbc.gridy = riga;
        gbc.insets = new Insets(0, 8, 0, 8);
        pannello.add(btnTorna, gbc);

        setContentPane(pannello);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnRegistrati);

        btnRegistrati.addActionListener(e -> gestisciRegistrazione());
        btnTorna.addActionListener(e -> tornaAlLogin());
    }

    private int campo(JPanel pannello, GridBagConstraints gbc, int riga,
                      String etichetta, JComponent componente) {
        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.gridwidth = 1;
        pannello.add(new JLabel(etichetta), gbc);
        gbc.gridx = 1;
        pannello.add(componente, gbc);
        return riga + 1;
    }

    private void gestisciRegistrazione() {
        try {
            UtenteDTO utente = facade.registrazione(
                    (String) cmbRuolo.getSelectedItem(),
                    txtNome.getText(),
                    txtCognome.getText(),
                    txtEmail.getText(),
                    new String(txtPassword.getPassword()));

            StileGUI.conferma(this, "Registrazione completata per " + utente + ".\n"
                    + "Ora puoi effettuare l'accesso con le tue credenziali.");
            tornaAlLogin();

        } catch (IllegalArgumentException | IllegalStateException e) {
            StileGUI.errore(this, e.getMessage());
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
        }
    }

    private void tornaAlLogin() {
        new FormLogin().setVisible(true);
        dispose();
    }
}
