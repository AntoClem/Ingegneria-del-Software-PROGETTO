package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.RiepilogoAndamentoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Schermata di monitoraggio dell'andamento di un Corso (UC15): numero di materiali
 * visibili (UC19), numero di studenti iscritti e distribuzione per categoria (UC20).
 */
public class FormMonitoraAndamentoCorso extends JFrame {

    private static final String[] COLONNE = {"Categoria", "Numero materiali"};

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final CorsoDTO corso;

    private final JLabel lblContenuti = new JLabel();
    private final JLabel lblStudenti = new JLabel();
    private final JTable tabella = new JTable(new DefaultTableModel(new Object[0][2], COLONNE));

    public FormMonitoraAndamentoCorso(CorsoDTO corso) {
        this.corso = corso;

        setTitle("Andamento del corso - " + corso.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel intestazione = new JPanel(new GridLayout(2, 1, 0, 4));
        intestazione.setOpaque(false);
        intestazione.add(StileGUI.titolo("Andamento del corso"));
        intestazione.add(StileGUI.sottotitolo(corso.getTitolo()));

        lblContenuti.setFont(lblContenuti.getFont().deriveFont(Font.BOLD, 15f));
        lblStudenti.setFont(lblStudenti.getFont().deriveFont(Font.BOLD, 15f));

        JPanel riepilogo = new JPanel(new GridLayout(1, 2, 16, 0));
        riepilogo.setOpaque(false);
        riepilogo.add(riquadro(lblContenuti));
        riepilogo.add(riquadro(lblStudenti));

        tabella.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabella);
        scroll.setPreferredSize(new Dimension(440, 220));
        scroll.setBorder(BorderFactory.createTitledBorder("Distribuzione per categoria"));

        JButton btnAggiorna = StileGUI.bottonePrimario("Aggiorna");
        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnAggiorna);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(riepilogo, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        JPanel radice = StileGUI.pannelloBase(new BorderLayout(0, 16));
        radice.add(intestazione, BorderLayout.NORTH);
        radice.add(centro, BorderLayout.CENTER);
        radice.add(bottoni, BorderLayout.SOUTH);

        setContentPane(radice);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        btnAggiorna.addActionListener(e -> mostraRiepilogo());
        mostraRiepilogo();
    }

    /** Riquadro con bordo attorno a un dato di riepilogo. */
    private JPanel riquadro(JLabel contenuto) {
        JPanel pannello = new JPanel(new BorderLayout());
        pannello.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD0D0D0)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        pannello.setBackground(Color.WHITE);
        pannello.add(contenuto, BorderLayout.CENTER);
        return pannello;
    }

    /**
     * Ricarica il riepilogo: una sola chiamata alla Facade restituisce tutti e tre i
     * dati della schermata, aggregati nel RiepilogoAndamentoDTO.
     */
    private void mostraRiepilogo() {
        RiepilogoAndamentoDTO riepilogo = facade.monitoraAndamentoCorso(corso.getId());

        lblContenuti.setText("Materiali pubblicati: " + riepilogo.getNumeroContenutiPubblicati());
        lblStudenti.setText("Studenti iscritti: " + riepilogo.getNumeroStudentiIscritti());

        Map<String, Integer> distribuzione = riepilogo.getDistribuzionePerCategoria();
        Object[][] righe = new Object[distribuzione.size()][2];
        int indice = 0;
        for (Map.Entry<String, Integer> voce : distribuzione.entrySet()) {
            righe[indice][0] = voce.getKey();
            righe[indice][1] = voce.getValue();
            indice++;
        }
        tabella.setModel(new DefaultTableModel(righe, COLONNE));
    }
}
