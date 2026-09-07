package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.UtenteDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Schermata di accesso alla piattaforma (UC2).
 *
 * Come tutte le Boundary del progetto, importa soltanto la Facade del Control e i DTO:
 * non conosce alcuna classe del package entity ne' la persistenza. Raccoglie l'input,
 * invoca l'operazione della Facade e comunica l'esito all'utente.
 */
public class FormLogin extends JFrame {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();

    private final JTextField txtEmail = new JTextField(24);
    private final JPasswordField txtPassword = new JPasswordField(24);

    public FormLogin() {
        setTitle("Piattaforma di Materiale Didattico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pannello = StileGUI.pannelloBase(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        pannello.add(StileGUI.titolo("Accedi alla piattaforma"), gbc);

        gbc.gridy = 1;
        pannello.add(StileGUI.sottotitolo("Materiale didattico dei corsi a cui sei iscritto"), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        pannello.add(new JLabel("Email istituzionale"), gbc);
        gbc.gridx = 1;
        pannello.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        pannello.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        pannello.add(txtPassword, gbc);

        JButton btnAccedi = StileGUI.bottonePrimario("Accedi");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        pannello.add(btnAccedi, gbc);

        JButton btnRegistrati = StileGUI.collegamento("Non hai un account? Registrati");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 8, 0, 8);
        pannello.add(btnRegistrati, gbc);

        setContentPane(pannello);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnAccedi);

        btnAccedi.addActionListener(e -> gestisciAccesso());
        btnRegistrati.addActionListener(e -> {
            new FormRegistrazione().setVisible(true);
            dispose();
        });
    }

    /**
     * Invoca l'Accesso e, se va a buon fine, apre la schermata corrispondente al ruolo.
     * I messaggi di errore mostrati sono quelli prodotti dal Control: la Boundary non
     * li reinterpreta e non decide da se' cosa sia andato storto.
     */
    private void gestisciAccesso() {
        try {
            UtenteDTO utente = facade.accesso(txtEmail.getText(), new String(txtPassword.getPassword()));

            if (utente.isDocente()) {
                new DashboardDocente(utente).setVisible(true);
            } else {
                new DashboardStudente(utente).setVisible(true);
            }
            dispose();

        } catch (IllegalArgumentException | SecurityException e) {
            StileGUI.errore(this, e.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
        }
    }
}
