package it.unina.materialedidattico.boundary;

import it.unina.materialedidattico.control.PiattaformaFacade;
import it.unina.materialedidattico.dto.CorsoDTO;
import it.unina.materialedidattico.dto.UtenteDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Schermata principale dello Studente autenticato: elenca i Corsi a cui e' iscritto
 * (UC12) e da' accesso all'iscrizione a un nuovo Corso (UC6) e alla consultazione del
 * materiale (UC13).
 */
public class DashboardStudente extends JFrame {

    private final PiattaformaFacade facade = PiattaformaFacade.getIstanza();
    private final UtenteDTO studente;

    private final DefaultListModel<CorsoDTO> modelloCorsi = new DefaultListModel<>();
    private final JList<CorsoDTO> listaCorsi = new JList<>(modelloCorsi);
    private final JButton btnNotifiche = StileGUI.bottone("Notifiche");

    public DashboardStudente(UtenteDTO studente) {
        this.studente = studente;

        setTitle("Area Studente - " + studente.getNome() + " " + studente.getCognome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel intestazione = new JPanel(new GridLayout(2, 1, 0, 4));
        intestazione.setOpaque(false);
        intestazione.add(StileGUI.titolo("I tuoi corsi"));
        intestazione.add(StileGUI.sottotitolo(
                "Seleziona un corso per consultarne il materiale didattico"));

        listaCorsi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCorsi.setFixedCellHeight(28);
        JScrollPane scroll = new JScrollPane(listaCorsi);
        scroll.setPreferredSize(new Dimension(520, 300));

        JButton btnApri = StileGUI.bottonePrimario("Apri corso");
        JButton btnIscriviti = StileGUI.bottone("Iscriviti a un corso");
        JButton btnAggiorna = StileGUI.bottone("Aggiorna");
        JButton btnEsci = StileGUI.bottone("Esci");

        JPanel bottoni = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottoni.setOpaque(false);
        bottoni.add(btnApri);
        bottoni.add(btnIscriviti);
        bottoni.add(btnNotifiche);
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

        btnApri.addActionListener(e -> apriCorsoSelezionato());
        btnIscriviti.addActionListener(e -> new FormIscrivitiCorso(studente, this).setVisible(true));
        btnNotifiche.addActionListener(e -> new FormNotifiche(studente, this).setVisible(true));
        btnAggiorna.addActionListener(e -> {
            aggiornaElencoCorsi();
            aggiornaContatoreNotifiche();
        });
        btnEsci.addActionListener(e -> {
            facade.logout();
            new FormLogin().setVisible(true);
            dispose();
        });

        // Doppio clic sulla riga: scorciatoia equivalente al bottone "Apri corso".
        listaCorsi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evento) {
                if (evento.getClickCount() == 2) {
                    apriCorsoSelezionato();
                }
            }
        });

        aggiornaElencoCorsi();
        aggiornaContatoreNotifiche();
    }

    /**
     * Aggiorna l'etichetta del bottone con il numero di notifiche non lette.
     * E' il riscontro visibile del pattern Observer: le notifiche contate qui sono
     * state generate dal Contenuto nel momento in cui il Docente lo ha reso visibile.
     */
    public void aggiornaContatoreNotifiche() {
        int nonLette = facade.contaNotificheNonLette(studente.getId());
        btnNotifiche.setText(nonLette == 0 ? "Notifiche" : "Notifiche (" + nonLette + ")");
        btnNotifiche.setForeground(nonLette == 0 ? null : StileGUI.BLU);
    }

    /** Ricarica l'elenco dei Corsi: all'apertura, dopo un'iscrizione e su richiesta. */
    public void aggiornaElencoCorsi() {
        modelloCorsi.clear();
        List<CorsoDTO> corsi = facade.visualizzaCorsiIscritti(studente.getId());
        for (CorsoDTO corso : corsi) {
            modelloCorsi.addElement(corso);
        }
        if (corsi.isEmpty()) {
            StileGUI.avviso(this, "Non risulti iscritto a nessun corso.\n"
                    + "Usa \"Iscriviti a un corso\" e inserisci il codice fornito dal docente.");
        }
    }

    private void apriCorsoSelezionato() {
        CorsoDTO selezionato = listaCorsi.getSelectedValue();
        if (selezionato == null) {
            StileGUI.avviso(this, "Seleziona prima un corso dall'elenco.");
            return;
        }
        new FormVisualizzaContenutiCorso(selezionato).setVisible(true);
    }
}
