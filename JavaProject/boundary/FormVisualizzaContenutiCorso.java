package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.VisualizzaContenutiCorsoController;
import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import javax.swing.*;
import java.util.List;

/**
 * SCHELETRO Boundary del caso d'uso VisualizzaContenutiCorso (UC13).
 * Vedi nota generale in FormIscrivitiCorso: la parte grafica va
 * completata con lo Swing UI Designer.
 */
public class FormVisualizzaContenutiCorso extends JFrame {

    private JPanel contentPane;
    private JList<ContenutoDTO> listaContenuti;
    private JComboBox<Categoria> cmbFiltroCategoria;
    private JComboBox<Sezione> cmbFiltroSezione;
    private JButton btnApplicaFiltri;
    private JTextArea txtDettaglioContenuto;

    private final VisualizzaContenutiCorsoController controller = new VisualizzaContenutiCorsoController();
    private final Corso corsoSelezionato;

    public FormVisualizzaContenutiCorso(Corso corsoSelezionato) {
        this.corsoSelezionato = corsoSelezionato;
        setTitle("Contenuti del Corso - " + corsoSelezionato.getTitolo());
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        caricaElenco(controller.visualizzaContenutiCorso(corsoSelezionato));

        btnApplicaFiltri.addActionListener(e -> {
            Categoria categoria = (Categoria) cmbFiltroCategoria.getSelectedItem();
            Sezione sezione = (Sezione) cmbFiltroSezione.getSelectedItem();
            // «include» FiltraContenuti: il filtro sulla data e' omesso in questo scheletro
            // per semplicita' e va aggiunto con un componente data (es. JSpinner/JDateChooser)
            caricaElenco(controller.filtraContenuti(corsoSelezionato, categoria, null, sezione));
        });

        listaContenuti.addListSelectionListener(e -> {
            ContenutoDTO selezionato = listaContenuti.getSelectedValue();
            if (selezionato != null) {
                ContenutoDTO dettaglio = controller.selezionaContenuto(selezionato.getId());
                txtDettaglioContenuto.setText(dettaglio.getTitolo() + "\n\n" + dettaglio.getDescrizione());
            }
        });
    }

    private void caricaElenco(List<ContenutoDTO> contenuti) {
        DefaultListModel<ContenutoDTO> modello = new DefaultListModel<>();
        for (ContenutoDTO c : contenuti) {
            modello.addElement(c);
        }
        listaContenuti.setModel(modello);
    }
}
