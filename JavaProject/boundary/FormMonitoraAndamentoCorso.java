package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.MonitoraAndamentoCorsoController;
import it.unina.materialedidattico.dto.RiepilogoAndamentoDTO;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;

import javax.swing.*;
import java.util.Map;

/**
 * SCHELETRO Boundary del caso d'uso MonitoraAndamentoCorso (UC15).
 * Vedi nota generale in FormIscrivitiCorso: la parte grafica va
 * completata con lo Swing UI Designer.
 */
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
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnAggiorna.addActionListener(e -> mostraRiepilogo());
        mostraRiepilogo();
    }

    private void mostraRiepilogo() {
        RiepilogoAndamentoDTO riepilogo = controller.monitoraAndamentoCorso(corsoSelezionato);

        lblNumeroContenuti.setText("Contenuti pubblicati: " + riepilogo.getNumeroContenutiPubblicati());
        lblNumeroStudenti.setText("Studenti iscritti: " + riepilogo.getNumeroStudentiIscritti());

        // popolamento della tabella a partire dalla mappa categoria -> conteggio
        String[] colonne = {"Categoria", "Numero Contenuti"};
        Object[][] righe = new Object[Categoria.values().length][2];
        int i = 0;
        for (Map.Entry<Categoria, Integer> voce : riepilogo.getDistribuzionePerCategoria().entrySet()) {
            righe[i][0] = voce.getKey();
            righe[i][1] = voce.getValue();
            i++;
        }
        tabellaDistribuzione.setModel(new javax.swing.table.DefaultTableModel(righe, colonne));
    }
}
