package controller;

import database.CorsoDAO;
import database.CorsoDAOImpl;
import database.IscrizioneDAO;
import database.IscrizioneDAOImpl;
import database.ContenutoDAO;
import database.ContenutoDAOImpl;
import database.UtenteDAO;
import database.UtenteDAOImpl;
import dto.AndamentoCorsoDTO;
import dto.ProfiloDocenteDTO;
import entity.Categoria;
import entity.Corso;
import entity.Contenuto;
import entity.Utente;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller responsabile del monitoraggio dell'andamento dei corsi da
 * parte del docente e della visualizzazione del profilo pubblico di un docente.
 *
 * Corrisponde ai casi d'uso:
 * - UC15 MonitoraAndamentoCorso
 * - VisualizzaProfiloDocente
 *
 * La struttura di monitoraAndamentoCorso rispecchia fedelmente il diagramma
 * di sequenza di UC15: tre auto-deleghe in sequenza (nessun combined
 * fragment, flusso lineare) seguite dal riepilogo finale. Se il corso non
 * ha ancora contenuti o iscritti, i valori calcolati sono semplicemente
 * zero (nessuna gestione speciale necessaria, come da nota del team).
 */
public class ControllerMonitoraggio {

    private final CorsoDAO corsoDAO;
    private final IscrizioneDAO iscrizioneDAO;
    private final ContenutoDAO contenutoDAO;
    private final UtenteDAO utenteDAO;

    public ControllerMonitoraggio() {
        this.corsoDAO = new CorsoDAOImpl();
        this.iscrizioneDAO = new IscrizioneDAOImpl();
        this.contenutoDAO = new ContenutoDAOImpl();
        this.utenteDAO = new UtenteDAOImpl();
    }

    /**
     * 1: monitoraAndamentoCorso(corso).
     * Esegue in sequenza le tre auto-deleghe del diagramma e restituisce
     * il riepilogoAndamento(nContenuti, nStudenti, distribuzione).
     */
    public AndamentoCorsoDTO monitoraAndamentoCorso(int corsoId) {
        Corso corso = corsoDAO.findById(corsoId);
        if (corso == null) {
            throw new IllegalArgumentException("Corso non trovato.");
        }

        // 1.1: calcolaContenutiPubblicati(corso) - <<include>> VisualizzaContenutiPubblicati
        int nContenuti = calcolaContenutiPubblicati(corsoId);

        // 1.2: calcolaStudentiIscritti(corso)
        int nStudenti = calcolaStudentiIscritti(corsoId);

        // 1.3: calcolaDistribuzioneCategoria(corso) - <<include>> VisualizzaDistribuzioneCategoria
        Map<String, Integer> distribuzione = calcolaDistribuzioneCategoria(corsoId);

        // 7: riepilogoAndamento(nContenuti, nStudenti, distribuzione)
        return new AndamentoCorsoDTO(corso.getTitolo(), nContenuti, nStudenti, distribuzione);
    }

    /** Auto-delega 1.1: numero di contenuti pubblicati nel corso. */
    private int calcolaContenutiPubblicati(int corsoId) {
        return contenutoDAO.contaContenutiPerCorso(corsoId);
    }

    /** Auto-delega 1.2: numero di studenti iscritti al corso. */
    private int calcolaStudentiIscritti(int corsoId) {
        return iscrizioneDAO.contaIscrittiPerCorso(corsoId);
    }

    /** Auto-delega 1.3: distribuzione dei contenuti pubblicati per categoria. */
    private Map<String, Integer> calcolaDistribuzioneCategoria(int corsoId) {
        List<Contenuto> contenuti = contenutoDAO.findPubblicatiByCorso(corsoId);
        Map<String, Integer> distribuzione = new LinkedHashMap<>();
        for (Categoria cat : Categoria.values()) {
            long count = contenuti.stream().filter(c -> c.getCategoria() == cat).count();
            if (count > 0) {
                distribuzione.put(cat.name(), (int) count);
            }
        }
        return distribuzione;
    }

    /** Restituisce il profilo pubblico di un docente: nome ed elenco dei corsi gestiti. */
    public ProfiloDocenteDTO profiloDocente(int docenteId) {
        Utente docente = utenteDAO.findById(docenteId);
        if (docente == null) {
            throw new IllegalArgumentException("Docente non trovato.");
        }
        List<String> titoliCorsi = corsoDAO.findByDocente(docenteId).stream()
                .map(Corso::getTitolo)
                .collect(Collectors.toList());

        return new ProfiloDocenteDTO(docente.getNome() + " " + docente.getCognome(), titoliCorsi);
    }
}
