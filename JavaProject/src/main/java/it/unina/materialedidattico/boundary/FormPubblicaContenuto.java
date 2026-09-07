package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.SezioneDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Schermata di pubblicazione di nuovo materiale didattico (UC7).
 *
 * Le categorie e le sezioni disponibili arrivano dalla Facade come stringhe e come
 * SezioneDTO: nella versione precedente del progetto questa schermata interrogava
 * direttamente il DAO delle Sezioni, saltando il layer Control.
 */
public class FormPubblicaContenuto extends JFrame {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final CorsoDTO corso;

    private final JTextField txtTitolo = new JTextField(26);
    private final JTextArea txtDescrizione = new JTextArea(5, 26);
    private final JComboBox<String> cmbCategoria = new JComboBox<>();
    private final JComboBox<SezioneDTO> cmbSezione = new JComboBox<>();
    private final JCheckBox chkPubblicaSubito =
            new JCheckBox("Rendi visibile immediatamente agli studenti iscritti", true);

    public FormPubblicaContenuto(CorsoDTO corso) {
        this.corso = corso;

        setTitle("Pubblica materiale - " + corso.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        for (String categoria : facade.getCategorieDisponibili()) {
            cmbCategoria.addItem(categoria);
        }

        // La prima voce, null, rappresenta l'assenza di Sezione.
        cmbSezione.addItem(null);
        for (SezioneDTO sezione : facade.visualizzaSezioni(corso.getId())) {
            cmbSezione.addItem(sezione);
        }
        cmbSezione.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valore, int indice,
                                                          boolean selezionato, boolean conFocus) {
                String testo = (valore == null) ? "(nessuna sezione)" : valore.toString();
                return super.getListCellRendererComponent(lista, testo, indice, selezionato, conFocus);
            }
        });

        JPanel pannello = StileGUI.pannelloBase(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        pannello.add(StileGUI.titolo("Nuovo materiale"), gbc);

        gbc.gridy = 1;
        pannello.add(StileGUI.sottotitolo("Corso: " + corso.getTitolo()), gbc);
        gbc.gridwidth = 1;

        int riga = 2;
        gbc.gridx = 0;
        gbc.gridy = riga;
        pannello.add(new JLabel("Titolo"), gbc);
        gbc.gridx = 1;
        pannello.add(txtTitolo, gbc);
        riga++;

        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        pannello.add(new JLabel("Descrizione"), gbc);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pannello.add(new JScrollPane(txtDescrizione), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        riga++;

        gbc.gridx = 0;
        gbc.gridy = riga;
        pannello.add(new JLabel("Categoria"), gbc);
        gbc.gridx = 1;
        pannello.add(cmbCategoria, gbc);
        riga++;

        gbc.gridx = 0;
        gbc.gridy = riga;
        pannello.add(new JLabel("Sezione"), gbc);
        gbc.gridx = 1;
        pannello.add(cmbSezione, gbc);
        riga++;

        gbc.gridx = 0;
        gbc.gridy = riga;
        gbc.gridwidth = 2;
        pannello.add(chkPubblicaSubito, gbc);
        riga++;

        JButton btnPubblica = StileGUI.bottonePrimario("Pubblica");
        JButton btnChiudi = StileGUI.bottone("Chiudi");
        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnChiudi);
        bottoni.add(btnPubblica);

        gbc.gridy = riga;
        gbc.insets = new Insets(16, 8, 0, 8);
        pannello.add(bottoni, gbc);

        setContentPane(pannello);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnPubblica);

        btnPubblica.addActionListener(e -> gestisciPubblicazione());
        btnChiudi.addActionListener(e -> dispose());
    }

    /**
     * Pubblica il materiale e conferma esplicitamente l'esito, indicando se sia stato
     * reso visibile subito oppure salvato come bozza: sono due risultati diversi e
     * l'utente deve poterli distinguere senza andare a controllare altrove.
     */
    private void gestisciPubblicazione() {
        try {
            SezioneDTO sezioneScelta = (SezioneDTO) cmbSezione.getSelectedItem();
            Long idSezione = (sezioneScelta == null) ? null : sezioneScelta.getId();

            ContenutoDTO contenuto = facade.pubblicaContenuto(corso.getId(),
                    txtTitolo.getText(), txtDescrizione.getText(),
                    (String) cmbCategoria.getSelectedItem(), idSezione,
                    chkPubblicaSubito.isSelected());

            String esito = contenuto.isVisibile()
                    ? "Materiale pubblicato: e' ora visibile agli studenti iscritti,\n"
                      + "che hanno ricevuto una notifica."
                    : "Materiale salvato come bozza: non e' ancora visibile agli studenti.\n"
                      + "Potrai renderlo visibile da \"Gestisci materiale\".";
            StileGUI.conferma(this, esito + "\n\nTitolo: " + contenuto.getTitolo());

            txtTitolo.setText("");
            txtDescrizione.setText("");
            txtTitolo.requestFocusInWindow();

        } catch (IllegalArgumentException | IllegalStateException e) {
            StileGUI.errore(this, e.getMessage());
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
        }
    }
}
