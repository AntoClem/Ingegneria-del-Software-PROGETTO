package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.MonitoraAndamentoCorsoController;
import it.unina.materialedidattico.dto.RiepilogoAndamentoDTO;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class FormMonitoraAndamentoCorso extends JFrame {

    private JPanel contentPane;
    private JLabel lblNumeroContenuti;
    private JLabel lblNumeroStudenti;
    private JTable tabellaDistribuzione;
    private JButton btnAggiorna;

    private final MonitoraAndamentoCorsoController controller = new MonitoraAndamentoCorsoController();
    private final Corso corsoSelezionato;

    public FormMonitoraAndamentoCorso(Corso corsoSelezionato) {
        this.corsoSelezionato = corsoSelezionato;
        setTitle("Andamento Corso - " + corsoSelezionato.getTitolo());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane = new JPanel(new BorderLayout(12, 12));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel pannelloRiepilogo = new JPanel(new GridLayout(2, 1, 4, 4));
        lblNumeroContenuti = new JLabel("Contenuti pubblicati: -");
        lblNumeroStudenti = new JLabel("Studenti iscritti: -");
        lblNumeroContenuti.setFont(lblNumeroContenuti.getFont().deriveFont(Font.BOLD, 14f));
        lblNumeroStudenti.setFont(lblNumeroStudenti.getFont().deriveFont(Font.BOLD, 14f));
        pannelloRiepilogo.add(lblNumeroContenuti);
        pannelloRiepilogo.add(lblNumeroStudenti);
        contentPane.add(pannelloRiepilogo, BorderLayout.NORTH);

        tabellaDistribuzione = new JTable();
        tabellaDistribuzione.setModel(new javax.swing.table.DefaultTableModel(
                new Object[0][2], new String[]{"Categoria", "Numero Contenuti"}));
        JScrollPane scrollTabella = new JScrollPane(tabellaDistribuzione);
        scrollTabella.setPreferredSize(new Dimension(360, 180));
        contentPane.add(scrollTabella, BorderLayout.CENTER);

        btnAggiorna = new JButton("Aggiorna");
        JPanel pannelloBottone = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pannelloBottone.add(btnAggiorna);
        contentPane.add(pannelloBottone, BorderLayout.SOUTH);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);

        btnAggiorna.addActionListener(e -> mostraRiepilogo());
        mostraRiepilogo();
    }

    private void mostraRiepilogo() {
        RiepilogoAndamentoDTO riepilogo = controller.monitoraAndamentoCorso(corsoSelezionato);

        lblNumeroContenuti.setText("Contenuti pubblicati: " + riepilogo.getNumeroContenutiPubblicati());
        lblNumeroStudenti.setText("Studenti iscritti: " + riepilogo.getNumeroStudentiIscritti());

        Map<Categoria, Integer> distribuzione = riepilogo.getDistribuzionePerCategoria();
        Object[][] righe = new Object[distribuzione.size()][2];
        int i = 0;
        for (Map.Entry<Categoria, Integer> voce : distribuzione.entrySet()) {
            righe[i][0] = voce.getKey();
            righe[i][1] = voce.getValue();
            i++;
        }
        String[] colonne = {"Categoria", "Numero Contenuti"};
        tabellaDistribuzione.setModel(new javax.swing.table.DefaultTableModel(righe, colonne));
    }
}