package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.dto.CorsoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Schermata con cui il Docente titolare gestisce il ciclo di vita del materiale:
 * attiva una bozza (UC9) oppure rimuove un materiale pubblicato (UC11).
 *
 * A differenza della schermata dello Studente, l'elenco mostra i Contenuti in
 * qualunque stato, con lo stato in colonna: e' il modo piu' diretto per rendere
 * visibile all'utente il ciclo di vita governato dal pattern State.
 */
public class FormGestisciContenuti extends JFrame {

    private static final String[] COLONNE = {"Titolo", "Categoria", "Sezione", "Stato"};

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final CorsoDTO corso;

    private final JTable tabella = new JTable(new DefaultTableModel(new Object[0][4], COLONNE));

    private List<ContenutoDTO> contenuti;

    public FormGestisciContenuti(CorsoDTO corso) {
        this.corso = corso;

        setTitle("Gestione materiale - " + corso.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel intestazione = new JPanel(new GridLayout(2, 1, 0, 4));
        intestazione.setOpaque(false);
        intestazione.add(StileGUI.titolo("Gestione del materiale"));
        intestazione.add(StileGUI.sottotitolo(
                "Le bozze non sono visibili agli studenti finche' non le rendi visibili"));

        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.getColumnModel().getColumn(3).setCellRenderer(new RendererStato());
        JScrollPane scroll = new JScrollPane(tabella);
        scroll.setPreferredSize(new Dimension(660, 280));

        JButton btnAttiva = StileGUI.bottonePrimario("Rendi visibile");
        JButton btnRimuovi = StileGUI.bottone("Rimuovi");
        JButton btnAggiorna = StileGUI.bottone("Aggiorna");

        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnAttiva);
        bottoni.add(btnRimuovi);
        bottoni.add(btnAggiorna);

        JPanel radice = StileGUI.pannelloBase(new BorderLayout(0, 16));
        radice.add(intestazione, BorderLayout.NORTH);
        radice.add(scroll, BorderLayout.CENTER);
        radice.add(bottoni, BorderLayout.SOUTH);

        setContentPane(radice);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        btnAttiva.addActionListener(e -> attivaSelezionato());
        btnRimuovi.addActionListener(e -> rimuoviSelezionato());
        btnAggiorna.addActionListener(e -> caricaElenco());

        caricaElenco();
    }

    private void caricaElenco() {
        contenuti = facade.visualizzaTuttiIContenuti(corso.getId());

        Object[][] righe = new Object[contenuti.size()][4];
        for (int i = 0; i < contenuti.size(); i++) {
            ContenutoDTO contenuto = contenuti.get(i);
            righe[i][0] = contenuto.getTitolo();
            righe[i][1] = contenuto.getCategoria();
            righe[i][2] = contenuto.getSezione();
            righe[i][3] = contenuto.getStato();
        }
        tabella.setModel(new DefaultTableModel(righe, COLONNE));
        tabella.getColumnModel().getColumn(3).setCellRenderer(new RendererStato());
    }

    private ContenutoDTO selezionato() {
        int riga = tabella.getSelectedRow();
        if (riga < 0) {
            StileGUI.avviso(this, "Seleziona prima un materiale dall'elenco.");
            return null;
        }
        return contenuti.get(riga);
    }

    private void attivaSelezionato() {
        ContenutoDTO contenuto = selezionato();
        if (contenuto == null) {
            return;
        }
        try {
            facade.attivaContenuto(contenuto.getId());
            caricaElenco();
            StileGUI.conferma(this, "Materiale reso visibile agli studenti iscritti,\n"
                    + "che hanno ricevuto una notifica.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            StileGUI.errore(this, e.getMessage());
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
        }
    }

    /**
     * La rimozione e' irreversibile (lo stato Rimosso e' finale), quindi viene chiesta
     * una conferma esplicita prima di eseguirla.
     */
    private void rimuoviSelezionato() {
        ContenutoDTO contenuto = selezionato();
        if (contenuto == null) {
            return;
        }
        if (!StileGUI.chiediConferma(this, "Rimuovere \"" + contenuto.getTitolo() + "\"?\n"
                + "Non sara' piu' visibile agli studenti e l'operazione non e' reversibile.")) {
            return;
        }
        try {
            facade.rimuoviContenuto(contenuto.getId());
            caricaElenco();
            StileGUI.conferma(this, "Materiale rimosso: non e' piu' visibile agli studenti.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            StileGUI.errore(this, e.getMessage());
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
        }
    }

    /** Colora la colonna dello stato, per distinguere a colpo d'occhio bozze e rimossi. */
    private static class RendererStato extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabella, Object valore, boolean selezionato,
                                                       boolean conFocus, int riga, int colonna) {
            Component componente = super.getTableCellRendererComponent(
                    tabella, valore, selezionato, conFocus, riga, colonna);
            if (!selezionato) {
                String stato = String.valueOf(valore);
                if ("PUBBLICATO".equals(stato)) {
                    componente.setForeground(StileGUI.VERDE);
                } else if ("BOZZA".equals(stato)) {
                    componente.setForeground(new Color(0xB26A00));
                } else {
                    componente.setForeground(StileGUI.ROSSO);
                }
            }
            return componente;
        }
    }
}
