package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.UtenteDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Schermata principale del Docente autenticato: elenca i Corsi di cui e' titolare e
 * da' accesso alla pubblicazione di materiale (UC7), alla gestione del ciclo di vita
 * dei materiali (UC9, UC11) e al monitoraggio dell'andamento del Corso (UC15).
 */
public class DashboardDocente extends JFrame {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final UtenteDTO docente;

    private final DefaultListModel<CorsoDTO> modelloCorsi = new DefaultListModel<>();
    private final JList<CorsoDTO> listaCorsi = new JList<>(modelloCorsi);

    /** Schermate raggiungibili dalla dashboard, una per caso d'uso del Docente. */
    private enum Schermata { PUBBLICA, GESTISCI, ANDAMENTO }

    public DashboardDocente(UtenteDTO docente) {
        this.docente = docente;

        setTitle("Area Docente - " + docente.getNome() + " " + docente.getCognome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel intestazione = new JPanel(new GridLayout(2, 1, 0, 4));
        intestazione.setOpaque(false);
        intestazione.add(StileGUI.titolo("I corsi che gestisci"));
        intestazione.add(StileGUI.sottotitolo(
                "Seleziona un corso, poi scegli l'operazione da svolgere"));

        listaCorsi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCorsi.setFixedCellHeight(28);
        JScrollPane scroll = new JScrollPane(listaCorsi);
        scroll.setPreferredSize(new Dimension(560, 300));

        JButton btnPubblica = StileGUI.bottonePrimario("Pubblica materiale");
        JButton btnGestisci = StileGUI.bottone("Gestisci materiale");
        JButton btnAndamento = StileGUI.bottone("Andamento corso");
        JButton btnAggiorna = StileGUI.bottone("Aggiorna");
        JButton btnEsci = StileGUI.bottone("Esci");

        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnPubblica);
        bottoni.add(btnGestisci);
        bottoni.add(btnAndamento);
        bottoni.add(btnAggiorna);
        bottoni.add(btnEsci);

        JPanel radice = StileGUI.pannelloBase(new BorderLayout(0, 16));
        radice.add(intestazione, BorderLayout.NORTH);
        radice.add(scroll, BorderLayout.CENTER);
        radice.add(bottoni, BorderLayout.SOUTH);

        setContentPane(radice);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        btnPubblica.addActionListener(e -> apri(Schermata.PUBBLICA));
        btnGestisci.addActionListener(e -> apri(Schermata.GESTISCI));
        btnAndamento.addActionListener(e -> apri(Schermata.ANDAMENTO));
        btnAggiorna.addActionListener(e -> aggiornaElencoCorsi());
        btnEsci.addActionListener(e -> {
            facade.logout();
            new FormLogin().setVisible(true);
            dispose();
        });

        aggiornaElencoCorsi();
    }

    public void aggiornaElencoCorsi() {
        modelloCorsi.clear();
        List<CorsoDTO> corsi = facade.visualizzaCorsiGestiti(docente.getId());
        for (CorsoDTO corso : corsi) {
            modelloCorsi.addElement(corso);
        }
    }

    private void apri(Schermata schermata) {
        CorsoDTO selezionato = listaCorsi.getSelectedValue();
        if (selezionato == null) {
            StileGUI.avviso(this, "Seleziona prima un corso dall'elenco.");
            return;
        }
        switch (schermata) {
            case PUBBLICA:
                new FormPubblicaContenuto(selezionato).setVisible(true);
                break;
            case GESTISCI:
                new FormGestisciContenuti(selezionato).setVisible(true);
                break;
            case ANDAMENTO:
                new FormMonitoraAndamentoCorso(selezionato).setVisible(true);
                break;
        }
    }
}
