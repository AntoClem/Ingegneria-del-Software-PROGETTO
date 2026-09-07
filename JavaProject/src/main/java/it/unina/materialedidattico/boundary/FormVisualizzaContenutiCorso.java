package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.SezioneDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Schermata di consultazione del materiale di un Corso (UC13), con i filtri per
 * categoria e sezione (UC18) e il dettaglio del Contenuto selezionato (UC14).
 */
public class FormVisualizzaContenutiCorso extends JFrame {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final CorsoDTO corso;

    private final DefaultListModel<ContenutoDTO> modelloContenuti = new DefaultListModel<>();
    private final JList<ContenutoDTO> listaContenuti = new JList<>(modelloContenuti);
    private final JTextArea txtDettaglio = new JTextArea();
    private final JComboBox<String> cmbCategoria = new JComboBox<>();
    private final JComboBox<SezioneDTO> cmbSezione = new JComboBox<>();
    private final JLabel lblConteggio = StileGUI.sottotitolo(" ");

    public FormVisualizzaContenutiCorso(CorsoDTO corso) {
        this.corso = corso;

        setTitle("Materiale del corso - " + corso.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Prima voce nulla in entrambi i filtri: significa "nessun filtro applicato".
        cmbCategoria.addItem(null);
        for (String categoria : facade.getCategorieDisponibili()) {
            cmbCategoria.addItem(categoria);
        }
        cmbCategoria.setRenderer(rendererConVoceVuota("Tutte le categorie"));

        cmbSezione.addItem(null);
        for (SezioneDTO sezione : facade.visualizzaSezioni(corso.getId())) {
            cmbSezione.addItem(sezione);
        }
        cmbSezione.setRenderer(rendererConVoceVuota("Tutte le sezioni"));

        JButton btnApplica = StileGUI.bottonePrimario("Applica filtri");
        JButton btnRimuovi = StileGUI.bottone("Rimuovi filtri");

        JPanel filtri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtri.setOpaque(false);
        filtri.add(new JLabel("Categoria"));
        filtri.add(cmbCategoria);
        filtri.add(Box.createHorizontalStrut(12));
        filtri.add(new JLabel("Sezione"));
        filtri.add(cmbSezione);
        filtri.add(Box.createHorizontalStrut(12));
        filtri.add(btnApplica);
        filtri.add(btnRimuovi);

        JPanel intestazione = new JPanel(new GridLayout(3, 1, 0, 6));
        intestazione.setOpaque(false);
        intestazione.add(StileGUI.titolo(corso.getTitolo()));
        intestazione.add(filtri);
        intestazione.add(lblConteggio);

        listaContenuti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaContenuti.setFixedCellHeight(26);
        JScrollPane scrollElenco = new JScrollPane(listaContenuti);
        scrollElenco.setPreferredSize(new Dimension(330, 340));
        scrollElenco.setBorder(BorderFactory.createTitledBorder("Materiali"));

        txtDettaglio.setEditable(false);
        txtDettaglio.setLineWrap(true);
        txtDettaglio.setWrapStyleWord(true);
        txtDettaglio.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scrollDettaglio = new JScrollPane(txtDettaglio);
        scrollDettaglio.setPreferredSize(new Dimension(360, 340));
        scrollDettaglio.setBorder(BorderFactory.createTitledBorder("Dettaglio"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollElenco, scrollDettaglio);
        split.setResizeWeight(0.45);
        split.setBorder(null);

        JPanel radice = StileGUI.pannelloBase(new BorderLayout(0, 16));
        radice.add(intestazione, BorderLayout.NORTH);
        radice.add(split, BorderLayout.CENTER);

        setContentPane(radice);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        btnApplica.addActionListener(e -> applicaFiltri());
        btnRimuovi.addActionListener(e -> {
            cmbCategoria.setSelectedIndex(0);
            cmbSezione.setSelectedIndex(0);
            caricaElenco(facade.visualizzaContenutiCorso(corso.getId()));
        });
        listaContenuti.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostraDettaglio();
            }
        });

        caricaElenco(facade.visualizzaContenutiCorso(corso.getId()));
    }

    private DefaultListCellRenderer rendererConVoceVuota(String testoVoceVuota) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valore, int indice,
                                                          boolean selezionato, boolean conFocus) {
                String testo = (valore == null) ? testoVoceVuota : valore.toString();
                return super.getListCellRendererComponent(lista, testo, indice, selezionato, conFocus);
            }
        };
    }

    private void applicaFiltri() {
        SezioneDTO sezioneScelta = (SezioneDTO) cmbSezione.getSelectedItem();
        Long idSezione = (sezioneScelta == null) ? null : sezioneScelta.getId();
        // Il filtro per data non e' esposto in questa schermata: si passa null.
        caricaElenco(facade.filtraContenuti(corso.getId(),
                (String) cmbCategoria.getSelectedItem(), null, idSezione));
    }

    /** Aggiorna l'elenco e mostra quanti materiali sono stati trovati. */
    private void caricaElenco(List<ContenutoDTO> contenuti) {
        modelloContenuti.clear();
        for (ContenutoDTO contenuto : contenuti) {
            modelloContenuti.addElement(contenuto);
        }
        txtDettaglio.setText("");

        if (contenuti.isEmpty()) {
            lblConteggio.setForeground(StileGUI.ROSSO);
            lblConteggio.setText("Nessun materiale corrisponde ai criteri impostati.");
        } else {
            lblConteggio.setForeground(StileGUI.GRIGIO_TESTO);
            lblConteggio.setText(contenuti.size() == 1
                    ? "1 materiale disponibile. Selezionalo per leggerne il dettaglio."
                    : contenuti.size() + " materiali disponibili. Selezionane uno per il dettaglio.");
        }
    }

    private void mostraDettaglio() {
        ContenutoDTO selezionato = listaContenuti.getSelectedValue();
        if (selezionato == null) {
            return;
        }
        ContenutoDTO dettaglio = facade.visualizzaDettaglioContenuto(selezionato.getId());
        txtDettaglio.setText(dettaglio.getTitolo() + "\n\n"
                + "Categoria: " + dettaglio.getCategoria() + "\n"
                + "Sezione: " + dettaglio.getSezione() + "\n"
                + "Data di pubblicazione: " + dettaglio.getDataPubblicazione() + "\n\n"
                + dettaglio.getDescrizione());
        txtDettaglio.setCaretPosition(0);
    }
}
