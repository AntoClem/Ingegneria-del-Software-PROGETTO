package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PubblicaContenutoController;
import it.unina.materialedidattico.database.SezioneDAO;
import it.unina.materialedidattico.database.SezioneDAOH2Impl;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;
import it.unina.materialedidattico.utility.Validazione;

import javax.swing.*;
import java.awt.*;

public class FormPubblicaContenuto extends JFrame {

    private JPanel contentPane;
    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<Sezione> cmbSezione;
    private JCheckBox chkPubblicaSubito;
    private JButton btnPubblica;
    private JLabel lblEsito;

    private final PubblicaContenutoController controller = new PubblicaContenutoController();
    private final Corso corsoSelezionato;

    public FormPubblicaContenuto(Corso corsoSelezionato) {
        this.corsoSelezionato = corsoSelezionato;
        setTitle("Pubblica Contenuto - " + corsoSelezionato.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int riga = 0;

        gbc.gridx = 0; gbc.gridy = riga; gbc.gridwidth = 1;
        contentPane.add(new JLabel("Titolo:"), gbc);
        txtTitolo = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = riga;
        contentPane.add(txtTitolo, gbc);
        riga++;

        gbc.gridx = 0; gbc.gridy = riga; gbc.anchor = GridBagConstraints.NORTHWEST;
        contentPane.add(new JLabel("Descrizione:"), gbc);
        txtDescrizione = new JTextArea(4, 25);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        gbc.gridx = 1; gbc.gridy = riga; gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(new JScrollPane(txtDescrizione), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        riga++;

        gbc.gridx = 0; gbc.gridy = riga;
        contentPane.add(new JLabel("Categoria:"), gbc);
        cmbCategoria = new JComboBox<>(Categoria.values());
        gbc.gridx = 1; gbc.gridy = riga;
        contentPane.add(cmbCategoria, gbc);
        riga++;

        gbc.gridx = 0; gbc.gridy = riga;
        contentPane.add(new JLabel("Sezione:"), gbc);
        SezioneDAO sezioneDAO = new SezioneDAOH2Impl();
        java.util.List<Sezione> sezioniCorso = sezioneDAO.cercaPerCorso(corsoSelezionato);
        Sezione[] opzioniSezione = new Sezione[sezioniCorso.size() + 1];
        opzioniSezione[0] = null;
        for (int i = 0; i < sezioniCorso.size(); i++) {
            opzioniSezione[i + 1] = sezioniCorso.get(i);
        }
        cmbSezione = new JComboBox<>(opzioniSezione);
        cmbSezione.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                String testo = (value == null) ? "(nessuna sezione)" : ((Sezione) value).getTitolo();
                return super.getListCellRendererComponent(list, testo, index, isSelected, cellHasFocus);
            }
        });
        gbc.gridx = 1; gbc.gridy = riga;
        contentPane.add(cmbSezione, gbc);
        riga++;

        chkPubblicaSubito = new JCheckBox("Rendi visibile immediatamente agli studenti");
        chkPubblicaSubito.setSelected(true);
        gbc.gridx = 0; gbc.gridy = riga; gbc.gridwidth = 2;
        contentPane.add(chkPubblicaSubito, gbc);
        riga++;

        btnPubblica = new JButton("Pubblica");
        gbc.gridx = 0; gbc.gridy = riga; gbc.gridwidth = 2;
        contentPane.add(btnPubblica, gbc);
        riga++;

        lblEsito = new JLabel(" ");
        gbc.gridx = 0; gbc.gridy = riga; gbc.gridwidth = 2;
        contentPane.add(lblEsito, gbc);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);

        btnPubblica.addActionListener(e -> gestisciPubblicazione());
    }

    private void gestisciPubblicazione() {
        String titolo = txtTitolo.getText();
        String descrizione = txtDescrizione.getText();
        Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
        Sezione sezione = (Sezione) cmbSezione.getSelectedItem();
        boolean pubblicaSubito = chkPubblicaSubito.isSelected();

        if (!Validazione.verificaDatiPubblicazione(titolo, descrizione, categoria)) {
            lblEsito.setText("Titolo (<=100 caratteri) e descrizione (<=500) sono obbligatori.");
            lblEsito.setForeground(Color.RED);
            return;
        }

        controller.pubblicaContenuto(corsoSelezionato, titolo, descrizione, categoria, sezione, pubblicaSubito);
        lblEsito.setText("Contenuto pubblicato con successo.");
        lblEsito.setForeground(new Color(0, 128, 0));
        txtTitolo.setText("");
        txtDescrizione.setText("");
    }
}