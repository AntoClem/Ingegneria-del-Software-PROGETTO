package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PubblicaContenutoController;
import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;
import it.unina.materialedidattico.utility.Validazione;

import javax.swing.*;
import java.awt.*;

/**
 * SCHELETRO Boundary del caso d'uso PubblicaContenuto (UC7).
 * Vedi nota generale in FormIscrivitiCorso: la parte grafica va
 * completata con lo Swing UI Designer.
 */
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
    private final Corso corsoSelezionato; // corso del Docente, gia' scelto in una schermata precedente

    public FormPubblicaContenuto(Corso corsoSelezionato) {
        this.corsoSelezionato = corsoSelezionato;
        setTitle("Pubblica Contenuto - " + corsoSelezionato.getTitolo());
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        btnPubblica.addActionListener(e -> gestisciPubblicazione());
    }

    private void gestisciPubblicazione() {
        String titolo = txtTitolo.getText();
        String descrizione = txtDescrizione.getText();
        Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
        Sezione sezione = (Sezione) cmbSezione.getSelectedItem();
        boolean pubblicaSubito = chkPubblicaSubito.isSelected();

        // Validazione di formato lato Boundary: se fallisce, il Controller non viene chiamato
        if (!Validazione.verificaDatiPubblicazione(titolo, descrizione, categoria)) {
            lblEsito.setText("Titolo, descrizione e categoria sono obbligatori.");
            lblEsito.setForeground(Color.RED);
            return;
        }

        controller.pubblicaContenuto(corsoSelezionato, titolo, descrizione, categoria, sezione, pubblicaSubito);
        lblEsito.setText("Contenuto pubblicato con successo.");
        lblEsito.setForeground(new Color(0, 128, 0));
    }
}
