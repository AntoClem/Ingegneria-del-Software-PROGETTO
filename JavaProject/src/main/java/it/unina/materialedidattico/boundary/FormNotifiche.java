package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.NotificaDTO;
import it.unina.materialedidattico.dto.UtenteDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Schermata con cui lo Studente consulta le Notifiche ricevute (RF22, RF23).
 *
 * E' il lato visibile del pattern Observer: le righe che si vedono qui sono state
 * create dal Contenuto nel momento in cui e' diventato visibile, senza che il caso
 * d'uso di pubblicazione dovesse occuparsene.
 */
public class FormNotifiche extends JDialog {

    private static final String[] COLONNE = {"Data", "Corso", "Materiale", "Stato"};

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final UtenteDTO studente;
    private final DashboardStudente dashboard;

    private final JTable tabella = new JTable(new DefaultTableModel(new Object[0][4], COLONNE));
    private final JTextArea txtMessaggio = new JTextArea(4, 40);

    private List<NotificaDTO> notifiche;

    public FormNotifiche(UtenteDTO studente, DashboardStudente dashboard) {
        super(dashboard, "Le tue notifiche", true);
        this.studente = studente;
        this.dashboard = dashboard;

        JPanel intestazione = new JPanel(new GridLayout(2, 1, 0, 4));
        intestazione.setOpaque(false);
        intestazione.add(StileGUI.titolo("Notifiche"));
        intestazione.add(StileGUI.sottotitolo(
                "Seleziona una notifica per leggerla: verra' segnata come letta"));

        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.getColumnModel().getColumn(3).setCellRenderer(new RendererStato());
        JScrollPane scrollTabella = new JScrollPane(tabella);
        scrollTabella.setPreferredSize(new Dimension(620, 220));

        txtMessaggio.setEditable(false);
        txtMessaggio.setLineWrap(true);
        txtMessaggio.setWrapStyleWord(true);
        txtMessaggio.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scrollMessaggio = new JScrollPane(txtMessaggio);
        scrollMessaggio.setBorder(BorderFactory.createTitledBorder("Messaggio"));

        JButton btnChiudi = StileGUI.bottonePrimario("Chiudi");
        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnChiudi);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.add(scrollTabella, BorderLayout.CENTER);
        centro.add(scrollMessaggio, BorderLayout.SOUTH);

        JPanel radice = StileGUI.pannelloBase(new BorderLayout(0, 16));
        radice.add(intestazione, BorderLayout.NORTH);
        radice.add(centro, BorderLayout.CENTER);
        radice.add(bottoni, BorderLayout.SOUTH);

        setContentPane(radice);
        pack();
        setLocationRelativeTo(dashboard);

        tabella.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                apriSelezionata();
            }
        });
        btnChiudi.addActionListener(e -> {
            dashboard.aggiornaContatoreNotifiche();
            dispose();
        });

        caricaElenco();
    }

    private void caricaElenco() {
        try {
            notifiche = facade.visualizzaNotifiche(studente.getId());
        } catch (RuntimeException e) {
            StileGUI.erroreImprevisto(this, e);
            return;
        }

        Object[][] righe = new Object[notifiche.size()][4];
        for (int i = 0; i < notifiche.size(); i++) {
            NotificaDTO notifica = notifiche.get(i);
            righe[i][0] = notifica.getData();
            righe[i][1] = notifica.getTitoloCorso();
            righe[i][2] = notifica.getTitoloContenuto();
            righe[i][3] = notifica.isLetta() ? "Letta" : "Da leggere";
        }
        tabella.setModel(new DefaultTableModel(righe, COLONNE));
        tabella.getColumnModel().getColumn(3).setCellRenderer(new RendererStato());

        if (notifiche.isEmpty()) {
            txtMessaggio.setText("Non hai ancora ricevuto notifiche.");
        }
    }

    /** Mostra il messaggio della Notifica selezionata e la segna come letta. */
    private void apriSelezionata() {
        int riga = tabella.getSelectedRow();
        if (riga < 0 || riga >= notifiche.size()) {
            return;
        }
        NotificaDTO notifica = notifiche.get(riga);
        txtMessaggio.setText(notifica.getMessaggio());
        txtMessaggio.setCaretPosition(0);

        if (!notifica.isLetta()) {
            try {
                facade.segnaNotificaComeLetta(notifica.getId());
                caricaElenco();
                tabella.setRowSelectionInterval(riga, riga);
            } catch (RuntimeException e) {
                StileGUI.erroreImprevisto(this, e);
            }
        }
    }

    /** Evidenzia le notifiche non ancora lette. */
    private static class RendererStato extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabella, Object valore, boolean selezionato,
                                                       boolean conFocus, int riga, int colonna) {
            Component componente = super.getTableCellRendererComponent(
                    tabella, valore, selezionato, conFocus, riga, colonna);
            if (!selezionato) {
                componente.setForeground("Da leggere".equals(valore) ? StileGUI.BLU : StileGUI.GRIGIO_TESTO);
            }
            return componente;
        }
    }
}
