package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.VisualizzaContenutiCorsoController;
import it.unina.materialedidattico.database.SezioneDAO;
import it.unina.materialedidattico.database.SezioneDAOH2Impl;
import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Boundary del caso d'uso VisualizzaContenutiCorso (UC13).
 * GUI costruita "a mano" con GridBagLayout/BorderLayout, vedi nota in FormIscrivitiCorso.
 *
 * Il filtro sulla data e' omesso per semplicita' (andrebbe aggiunto con
 * un componente data, es. JSpinner con SpinnerDateModel).
 */
public class FormVisualizzaContenutiCorso extends JFrame {

    private JPanel contentPane;
    private JList<ContenutoDTO> listaContenuti;
    private JComboBox<Categoria> cmbFiltroCategoria;
    private JComboBox<Sezione> cmbFiltroSezione;
    private JButton btnApplicaFiltri;
    private JButton btnRimuoviFiltri;
    private JTextArea txtDettaglioContenuto;

    private final VisualizzaContenutiCorsoController controller = new VisualizzaContenutiCorsoController();
    private final Corso corsoSelezionato;

    public FormVisualizzaContenutiCorso(Corso corsoSelezionato) {
        this.corsoSelezionato = corsoSelezionato;
        setTitle("Contenuti del Corso - " + corsoSelezionato.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane = new JPanel(new BorderLayout(12, 12));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // --- pannello filtri (nord) ---
        JPanel pannelloFiltri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pannelloFiltri.add(new JLabel("Categoria:"));

        Categoria[] categorieConVuoto = new Categoria[Categoria.values().length + 1];
        System.arraycopy(Categoria.values(), 0, categorieConVuoto, 1, Categoria.values().length);
        cmbFiltroCategoria = new JComboBox<>(categorieConVuoto);
        cmbFiltroCategoria.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                String testo = (value == null) ? "(tutte)" : value.toString();
                return super.getListCellRendererComponent(list, testo, index, isSelected, cellHasFocus);
            }
        });
        pannelloFiltri.add(cmbFiltroCategoria);

        pannelloFiltri.add(new JLabel("Sezione:"));
        SezioneDAO sezioneDAO = new SezioneDAOH2Impl();
        List<Sezione> sezioniCorso = sezioneDAO.cercaPerCorso(corsoSelezionato);
        Sezione[] opzioniSezione = new Sezione[sezioniCorso.size() + 1];
        for (int i = 0; i < sezioniCorso.size(); i++) {
            opzioniSezione[i + 1] = sezioniCorso.get(i);
        }
        cmbFiltroSezione = new JComboBox<>(opzioniSezione);
        cmbFiltroSezione.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                String testo = (value == null) ? "(tutte)" : ((Sezione) value).getTitolo();
                return super.getListCellRendererComponent(list, testo, index, isSelected, cellHasFocus);
            }
        });
        pannelloFiltri.add(cmbFiltroSezione);

        btnApplicaFiltri = new JButton("Applica filtri");
        pannelloFiltri.add(btnApplicaFiltri);

        btnRimuoviFiltri = new JButton("Rimuovi filtri");
        pannelloFiltri.add(btnRimuoviFiltri);

        contentPane.add(pannelloFiltri, BorderLayout.NORTH);

        // --- elenco + dettaglio (centro, split) ---
        listaContenuti = new JList<>();
        listaContenuti.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                ContenutoDTO c = (ContenutoDTO) value;
                String testo = c.getTitolo() + "  [" + c.getCategoria() + "]";
                return super.getListCellRendererComponent(list, testo, index, isSelected, cellHasFocus);
            }
        });
        JScrollPane scrollElenco = new JScrollPane(listaContenuti);
        scrollElenco.setPreferredSize(new Dimension(260, 300));

        txtDettaglioContenuto = new JTextArea();
        txtDettaglioContenuto.setEditable(false);
        txtDettaglioContenuto.setLineWrap(true);
        txtDettaglioContenuto.setWrapStyleWord(true);
        JScrollPane scrollDettaglio = new JScrollPane(txtDettaglioContenuto);
        scrollDettaglio.setPreferredSize(new Dimension(260, 300));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollElenco, scrollDettaglio);
        splitPane.setResizeWeight(0.5);
        contentPane.add(splitPane, BorderLayout.CENTER);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);

        caricaElenco(controller.visualizzaContenutiCorso(corsoSelezionato));

        btnApplicaFiltri.addActionListener(e -> {
            Categoria categoria = (Categoria) cmbFiltroCategoria.getSelectedItem();
            Sezione sezione = (Sezione) cmbFiltroSezione.getSelectedItem();
            // «include» FiltraContenuti
            caricaElenco(controller.filtraContenuti(corsoSelezionato, categoria, null, sezione));
        });

        btnRimuoviFiltri.addActionListener(e -> {
            cmbFiltroCategoria.setSelectedIndex(0);
            cmbFiltroSezione.setSelectedIndex(0);
            caricaElenco(controller.visualizzaContenutiCorso(corsoSelezionato));
        });

        listaContenuti.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            ContenutoDTO selezionato = listaContenuti.getSelectedValue();
            if (selezionato != null) {
                ContenutoDTO dettaglio = controller.selezionaContenuto(selezionato.getId());
                txtDettaglioContenuto.setText(
                        dettaglio.getTitolo() + "\n"
                                + "Categoria: " + dettaglio.getCategoria() + "\n"
                                + "Sezione: " + dettaglio.getSezione() + "\n"
                                + "Data pubblicazione: " + dettaglio.getDataPubblicazione() + "\n\n"
                                + dettaglio.getDescrizione());
            }
        });
    }

    private void caricaElenco(List<ContenutoDTO> contenuti) {
        DefaultListModel<ContenutoDTO> modello = new DefaultListModel<>();
        for (ContenutoDTO c : contenuti) {
            modello.addElement(c);
        }
        listaContenuti.setModel(modello);
        txtDettaglioContenuto.setText("");
    }
}
