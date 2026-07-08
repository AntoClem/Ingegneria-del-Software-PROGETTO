package controller;

import database.ContenutoDAO;
import database.ContenutoDAOImpl;
import dto.ContenutoDTO;
import dto.EsitoPubblicazioneDTO;
import entity.Categoria;
import entity.Contenuto;
import entity.Sezione;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsabile della pubblicazione e della consultazione dei
 * contenuti didattici.
 *
 * Corrisponde ai casi d'uso:
 * - UC7  PubblicaContenuto
 * - UC13 VisualizzaContenutiCorso (con inclusione FiltraContenuti)
 *
 * I nomi dei metodi e la struttura dei controlli (alt/opt) rispecchiano
 * fedelmente i diagrammi di sequenza di analisi concordati dal team.
 */
public class ControllerGestioneContenuti {

    private final ContenutoDAO contenutoDAO;
    private final ControllerGestioneCorsi controllerCorsi;
    private final ControllerNotifiche controllerNotifiche;

    public ControllerGestioneContenuti() {
        this.contenutoDAO = new ContenutoDAOImpl();
        this.controllerCorsi = new ControllerGestioneCorsi();
        this.controllerNotifiche = new ControllerNotifiche();
    }

    // =====================================================================
    // UC7 - PubblicaContenuto
    // =====================================================================

    /**
     * Pubblica un nuovo contenuto didattico in un corso.
     * Riflette passo-passo il diagramma di sequenza di UC7:
     * 1) verifica dei dati obbligatori (alt dati validi / non validi);
     * 2) se validi, creazione del Contenuto e inserimento (aggiungiContenuto);
     * 3) se il contenuto e' impostato come visibile immediatamente (opt),
     *    invio della notifica agli studenti iscritti (<<include>> InviaNotifica);
     * 4) conferma finale della pubblicazione.
     *
     * Nota: il parametro corsoId non compare esplicitamente nella firma del
     * messaggio nel diagramma (il corso e' presumibilmente gia' selezionato
     * nel contesto della schermata del docente), ma e' necessario a livello
     * di persistenza ed e' stato quindi aggiunto come primo parametro.
     */
    public EsitoPubblicazioneDTO pubblicaContenuto(int corsoId, String titolo, String descrizione,
                                                    Categoria categoria, Integer sezione,
                                                    boolean statoPubblicazione) {
        // 1.1: verificaDatiObbligatori(titolo, descrizione, categoria)
        boolean datiValidi = verificaDatiObbligatori(titolo, descrizione, categoria);

        // alt [dati validi] / else [dati non validi]
        if (!datiValidi) {
            return EsitoPubblicazioneDTO.errore("erroreDatiObbligatori");
        }

        // ramo "dati validi": create ":Contenuto"
        Contenuto contenuto = new Contenuto(0, titolo, descrizione, categoria,
                LocalDate.now(), statoPubblicazione, corsoId, sezione);

        // 5.1: aggiungiContenuto(contenuto)
        int contenutoId = aggiungiContenuto(contenuto);

        // opt [contenuto visibile immediatamente]
        if (statoPubblicazione) {
            contenuto.setId(contenutoId);
            inviaNotifica(contenuto);
        }

        // 5.3: confermaPubblicazione
        return EsitoPubblicazioneDTO.confermato(contenutoId, "confermaPubblicazione");
    }

    /** Auto-delega 1.1 del diagramma UC7: verifica che i campi obbligatori siano valorizzati. */
    private boolean verificaDatiObbligatori(String titolo, String descrizione, Categoria categoria) {
        return titolo != null && !titolo.isBlank()
                && descrizione != null && !descrizione.isBlank()
                && categoria != null;
    }

    /** Auto-delega 5.1 del diagramma UC7: inserisce il contenuto nel repository. */
    private int aggiungiContenuto(Contenuto contenuto) {
        return contenutoDAO.inserisci(contenuto);
    }

    /**
     * Auto-delega 5.2.1 del diagramma UC7 (<<include>> InviaNotifica):
     * notifica tutti gli studenti iscritti al corso della pubblicazione del nuovo contenuto.
     */
    private void inviaNotifica(Contenuto contenuto) {
        String messaggio = "Nuovo contenuto pubblicato: " + contenuto.getTitolo();
        controllerNotifiche.notificaStudentiIscritti(contenuto.getCorsoId(), contenuto.getId(), messaggio);
    }

    /** Rende visibile agli studenti un contenuto precedentemente non pubblicato. */
    public void attivaContenuto(int contenutoId) {
        Contenuto contenuto = contenutoDAO.findById(contenutoId);
        if (contenuto == null) {
            throw new IllegalArgumentException("Contenuto non trovato.");
        }
        contenuto.setPubblicato(true);
        contenutoDAO.aggiorna(contenuto);
        inviaNotifica(contenuto);
    }

    /** Modifica le informazioni di un contenuto gia' pubblicato. */
    public void modificaContenuto(int contenutoId, String nuovoTitolo, String nuovaDescrizione,
                                   Categoria nuovaCategoria) {
        Contenuto contenuto = contenutoDAO.findById(contenutoId);
        if (contenuto == null) {
            throw new IllegalArgumentException("Contenuto non trovato.");
        }
        contenuto.setTitolo(nuovoTitolo);
        contenuto.setDescrizione(nuovaDescrizione);
        contenuto.setCategoria(nuovaCategoria);
        contenutoDAO.aggiorna(contenuto);
    }

    /** Rimuove un contenuto: dopo la rimozione non e' piu' visibile agli studenti. */
    public void rimuoviContenuto(int contenutoId) {
        contenutoDAO.elimina(contenutoId);
    }

    // =====================================================================
    // UC13 - VisualizzaContenutiCorso (<<include>> FiltraContenuti)
    // =====================================================================

    /**
     * 1: visualizzaContenutiCorso(corso) -> elencoContenuti.
     * Elenco completo (non filtrato) dei contenuti pubblicati di un corso.
     */
    public List<ContenutoDTO> visualizzaContenutiCorso(int corsoId) {
        return mappaContenuti(contenutoDAO.findPubblicatiByCorso(corsoId), corsoId);
    }

    /**
     * Ramo opt [Studente imposta filtri]: filtraContenuti(categoria, data, sezione).
     * Si auto-delega ad applicaFiltri (<<include>> FiltraContenuti) e restituisce
     * elencoContenutiFiltrato.
     */
    public List<ContenutoDTO> filtraContenuti(int corsoId, Categoria categoria,
                                               LocalDate data, Integer sezioneId) {
        List<Contenuto> filtrati = applicaFiltri(corsoId, categoria, data, sezioneId);
        return mappaContenuti(filtrati, corsoId);
    }

    /** Auto-delega interna richiamata da filtraContenuti (<<include>> FiltraContenuti). */
    private List<Contenuto> applicaFiltri(int corsoId, Categoria categoria,
                                           LocalDate data, Integer sezioneId) {
        return contenutoDAO.findByCorso(corsoId, categoria, data, sezioneId);
    }

    /**
     * 5: selezionaContenuto(contenuto) -> dettaglioContenuto.
     * Dettaglio completo di un singolo contenuto.
     */
    public Contenuto selezionaContenuto(int contenutoId) {
        return contenutoDAO.findById(contenutoId);
    }

    /** Ricerca dei contenuti per titolo, descrizione o categoria (funzione trasversale). */
    public List<ContenutoDTO> ricercaContenuti(String testo) {
        return mappaContenuti(contenutoDAO.ricerca(testo), null);
    }

    private List<ContenutoDTO> mappaContenuti(List<Contenuto> contenuti, Integer corsoId) {
        List<Sezione> sezioni = (corsoId != null) ? controllerCorsi.visualizzaSezioni(corsoId) : null;
        return contenuti.stream().map(c -> toDTO(c, sezioni)).collect(Collectors.toList());
    }

    private ContenutoDTO toDTO(Contenuto c, List<Sezione> sezioniCorso) {
        String nomeSezione = null;
        if (c.getSezioneId() != null && sezioniCorso != null) {
            nomeSezione = sezioniCorso.stream()
                    .filter(s -> s.getId() == c.getSezioneId())
                    .map(Sezione::getTitolo)
                    .findFirst()
                    .orElse(null);
        }
        return new ContenutoDTO(c.getId(), c.getTitolo(), c.getDescrizione(),
                c.getCategoria().name(), c.getDataPubblicazione(), nomeSezione);
    }
}
