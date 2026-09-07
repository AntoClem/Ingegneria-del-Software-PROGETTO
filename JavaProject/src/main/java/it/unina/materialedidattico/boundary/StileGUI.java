package it.unina.materialedidattico.boundary;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Impostazioni grafiche condivise dalle schermate e riscontri all'utente.
 *
 * E' una classe di sola presentazione e vive percio' nel package boundary: non
 * contiene alcuna regola applicativa, si limita a stabilire come l'interfaccia appare
 * e come comunica gli esiti. Centralizzarla evita che ogni form ripeta colori, margini
 * e dimensioni, e garantisce che tutte le schermate si somiglino.
 */
public final class StileGUI {

    /** Colore istituzionale usato per intestazioni e azioni principali. */
    public static final Color BLU = new Color(0x1F4E79);
    public static final Color VERDE = new Color(0x1E7B34);
    public static final Color ROSSO = new Color(0xB00020);
    public static final Color GRIGIO_TESTO = new Color(0x5A5A5A);

    private static final int MARGINE = 24;

    private StileGUI() {
        // classe di sole utilita' di presentazione, non istanziabile
    }

    /**
     * Applica il look and feel a tutta l'applicazione. Va invocato una sola volta,
     * prima di costruire qualunque finestra (si veda Main).
     */
    public static void applica() {
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("TableHeader.height", 30);
        UIManager.put("Table.rowHeight", 26);
        FlatLightLaf.setup();
    }

    /** Pannello con i margini standard delle schermate. */
    public static JPanel pannelloBase(LayoutManager layout) {
        JPanel pannello = new JPanel(layout);
        pannello.setBorder(BorderFactory.createEmptyBorder(MARGINE, MARGINE, MARGINE, MARGINE));
        return pannello;
    }

    /** Titolo di schermata. */
    public static JLabel titolo(String testo) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setFont(etichetta.getFont().deriveFont(Font.BOLD, 20f));
        etichetta.setForeground(BLU);
        return etichetta;
    }

    /** Riga esplicativa sotto il titolo. */
    public static JLabel sottotitolo(String testo) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setForeground(GRIGIO_TESTO);
        return etichetta;
    }

    /** Bottone dell'azione principale della schermata. */
    public static JButton bottonePrimario(String testo) {
        JButton bottone = new JButton(testo);
        bottone.setBackground(BLU);
        bottone.setForeground(Color.WHITE);
        bottone.setFocusPainted(false);
        bottone.setPreferredSize(new Dimension(bottone.getPreferredSize().width + 24, 34));
        return bottone;
    }

    /** Bottone di azione secondaria. */
    public static JButton bottone(String testo) {
        JButton bottone = new JButton(testo);
        bottone.setFocusPainted(false);
        bottone.setPreferredSize(new Dimension(bottone.getPreferredSize().width + 20, 34));
        return bottone;
    }

    /** Bottone testuale, senza cornice, per le azioni di navigazione. */
    public static JButton collegamento(String testo) {
        JButton bottone = new JButton(testo);
        bottone.setBorderPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setFocusPainted(false);
        bottone.setForeground(BLU);
        bottone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return bottone;
    }

    // ------------------------------------------------------------------ riscontri

    /**
     * Conferma di un'operazione riuscita.
     *
     * I riscontri passano da finestre di dialogo e non da etichette in fondo alla
     * schermata: un'etichetta puo' restare tagliata quando il testo e' piu' largo della
     * finestra gia' dimensionata, e l'utente non si accorge dell'esito.
     */
    public static void conferma(Component genitore, String messaggio) {
        JOptionPane.showMessageDialog(genitore, messaggio, "Operazione completata",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Errore applicativo: mostra il messaggio prodotto dal Control. */
    public static void errore(Component genitore, String messaggio) {
        JOptionPane.showMessageDialog(genitore, messaggio, "Operazione non riuscita",
                JOptionPane.ERROR_MESSAGE);
    }

    /** Avviso per un'azione incompleta (es. nessuna riga selezionata). */
    public static void avviso(Component genitore, String messaggio) {
        JOptionPane.showMessageDialog(genitore, messaggio, "Attenzione",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Errore tecnico non previsto dal caso d'uso (per esempio un guasto della
     * connessione al database).
     *
     * E' la rete di sicurezza prevista dal principio di progettazione difensiva: senza
     * di essa un'eccezione sollevata dentro un gestore di evento morirebbe sull'Event
     * Dispatch Thread stampando soltanto sulla console, e l'utente resterebbe davanti a
     * una schermata che non risponde e non spiega nulla.
     */
    public static void erroreImprevisto(Component genitore, RuntimeException errore) {
        errore.printStackTrace();
        JOptionPane.showMessageDialog(genitore,
                "Si e' verificato un errore imprevisto durante l'operazione.\n\n"
                        + errore.getClass().getSimpleName()
                        + (errore.getMessage() == null ? "" : ": " + errore.getMessage()),
                "Errore imprevisto", JOptionPane.ERROR_MESSAGE);
    }

    /** Richiesta di conferma prima di un'operazione non reversibile. */
    public static boolean chiediConferma(Component genitore, String messaggio) {
        int scelta = JOptionPane.showConfirmDialog(genitore, messaggio, "Confermi?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return scelta == JOptionPane.YES_OPTION;
    }
}
