package it.unina.materialedidattico.control;

import it.unina.materialedidattico.dto.ContenutoDTO;
import it.unina.materialedidattico.dto.RiepilogoAndamentoDTO;
import it.unina.materialedidattico.entity.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestore dei casi d'uso che riguardano il materiale didattico:
 * PubblicaContenuto (UC7, che include InviaNotifica), AttivaContenuto (UC9),
 * RimuoviContenuto (UC11), VisualizzaContenutiCorso (UC13) con il suo incluso
 * FiltraContenuti (UC18), VisualizzaDettaglioContenuto (UC14), CercaContenuti (UC16)
 * e MonitoraAndamentoCorso (UC15) con i suoi inclusi UC19 e UC20.
 */
public class GestoreContenuti {

    private static final int TITOLO_LUNGHEZZA_MAX = 150;
    private static final int DESCRIZIONE_LUNGHEZZA_MAX = 2000;

    private static GestoreContenuti istanza;

    private final RegistroContenuti registroContenuti = RegistroContenuti.getIstanza();
    private final RegistroCorsi registroCorsi = RegistroCorsi.getIstanza();

    private GestoreContenuti() {
    }

    public static synchronized GestoreContenuti getIstanza() {
        if (istanza == null) {
            istanza = new GestoreContenuti();
        }
        return istanza;
    }

    // ---------------------------------------------------------------- UC7

    /**
     * Crea un nuovo Contenuto nel Corso. Il materiale nasce sempre come bozza; se il
     * Docente chiede di renderlo visibile subito, viene eseguita la transizione di
     * stato, che a sua volta fa scattare le Notifiche agli iscritti (UC21).
     *
     * L'ordine delle operazioni non e' casuale: il Contenuto viene prima persistito,
     * cosi' ha un identificatore, e solo dopo pubblicato, perche' le Notifiche generate
     * dalla transizione devono poterlo referenziare. Il Corso non viene aggiornato in
     * memoria: la collezione dei suoi Contenuti e' gestita dall'ORM e viene riletta dal
     * database alla prossima interrogazione.
     *
     * @throws IllegalArgumentException se i dati obbligatori non sono validi.
     */
    public ContenutoDTO pubblicaContenuto(Long idCorso, String titolo, String descrizione,
                                          String categoria, Long idSezione, boolean pubblicaSubito) {
        verificaDatiPubblicazione(titolo, descrizione, categoria);

        Corso corso = GestoreCorsi.getIstanza().trovaCorso(idCorso);
        Sezione sezione = trovaSezioneDelCorso(corso, idSezione);

        Contenuto contenuto = new Contenuto(titolo.trim(), descrizione.trim(),
                Categoria.valueOf(categoria), corso, sezione);

        registroContenuti.registraContenuto(contenuto);

        if (pubblicaSubito) {
            rendiVisibile(contenuto);
        }

        return toDTO(contenuto);
    }

    // ---------------------------------------------------------------- UC9

    /**
     * Rende visibile agli Studenti un materiale rimasto in bozza (RF10).
     *
     * @throws IllegalStateException se il materiale e' gia' pubblicato o e' stato rimosso.
     */
    public ContenutoDTO attivaContenuto(Long idContenuto) {
        Contenuto contenuto = trovaContenuto(idContenuto);
        rendiVisibile(contenuto);
        return toDTO(contenuto);
    }

    // ---------------------------------------------------------------- UC11

    /**
     * Rimuove un materiale: da quel momento non e' piu' visibile agli Studenti (RF12).
     *
     * @throws IllegalStateException se il materiale risulta gia' rimosso.
     */
    public ContenutoDTO rimuoviContenuto(Long idContenuto) {
        Contenuto contenuto = trovaContenuto(idContenuto);
        contenuto.rimuovi();
        registroContenuti.aggiorna(contenuto);
        return toDTO(contenuto);
    }

    // ---------------------------------------------------------------- UC13 / UC18

    /** Elenco dei Contenuti visibili nel Corso, senza filtri. */
    public List<ContenutoDTO> visualizzaContenutiCorso(Long idCorso) {
        Corso corso = GestoreCorsi.getIstanza().trovaCorso(idCorso);
        return toDTO(registroContenuti.cercaContenutiPubblicati(corso));
    }

    /**
     * Elenco di tutti i Contenuti del Corso in qualunque stato, riservato al Docente
     * titolare per gestire bozze e rimozioni.
     */
    public List<ContenutoDTO> visualizzaTuttiIContenuti(Long idCorso) {
        Corso corso = GestoreCorsi.getIstanza().trovaCorso(idCorso);
        return toDTO(registroContenuti.cercaContenutiPerCorso(corso));
    }

    /**
     * Elenco dei Contenuti visibili che rispettano i filtri indicati (UC18).
     * I tre filtri sono opzionali: se null, il relativo criterio non viene applicato.
     */
    public List<ContenutoDTO> filtraContenuti(Long idCorso, String categoria,
                                              LocalDate data, Long idSezione) {
        Corso corso = GestoreCorsi.getIstanza().trovaCorso(idCorso);
        Categoria categoriaFiltro = (categoria == null || categoria.isBlank())
                ? null : Categoria.valueOf(categoria);
        Sezione sezioneFiltro = trovaSezioneDelCorso(corso, idSezione);
        return toDTO(registroContenuti.cercaContenutiConFiltri(corso, categoriaFiltro, data, sezioneFiltro));
    }

    // ---------------------------------------------------------------- UC14 / UC16

    /** Dettaglio di un singolo Contenuto. */
    public ContenutoDTO visualizzaDettaglioContenuto(Long idContenuto) {
        return toDTO(trovaContenuto(idContenuto));
    }

    /** Ricerca di Contenuti per titolo o descrizione (RF20). */
    public List<ContenutoDTO> cercaContenuti(String testo) {
        if (testo == null || testo.isBlank()) {
            throw new IllegalArgumentException("Inserire un testo da cercare.");
        }
        return toDTO(registroContenuti.cercaContenutiPerTesto(testo.trim()));
    }

    // ---------------------------------------------------------------- UC15

    /**
     * Riepilogo dell'andamento del Corso: numero di Contenuti visibili (UC19), numero
     * di Studenti iscritti e distribuzione per Categoria (UC20).
     *
     * Se il Corso non ha ancora Contenuti ne' iscritti, le interrogazioni restituiscono
     * naturalmente zero e una mappa vuota: non serve alcun controllo esplicito.
     */
    public RiepilogoAndamentoDTO monitoraAndamentoCorso(Long idCorso) {
        Corso corso = GestoreCorsi.getIstanza().trovaCorso(idCorso);

        int contenutiPubblicati = registroContenuti.contaContenutiPubblicati(corso);
        int studentiIscritti = registroCorsi.contaIscrittiPerCorso(corso);

        Map<Categoria, Integer> distribuzione = registroContenuti.distribuzionePerCategoria(corso);
        Map<String, Integer> distribuzioneDTO = new LinkedHashMap<>();
        for (Map.Entry<Categoria, Integer> voce : distribuzione.entrySet()) {
            distribuzioneDTO.put(voce.getKey().name(), voce.getValue());
        }

        return new RiepilogoAndamentoDTO(corso.getTitolo(), contenutiPubblicati,
                studentiIscritti, distribuzioneDTO);
    }

    /** Categorie ammesse, esposte come stringhe per i menu a tendina delle Boundary. */
    public List<String> getCategorieDisponibili() {
        List<String> categorie = new ArrayList<>();
        for (Categoria categoria : Categoria.values()) {
            categorie.add(categoria.name());
        }
        return categorie;
    }

    // ---------------------------------------------------------------- helper privati

    /**
     * Esegue la transizione verso lo stato "pubblicato" e ne rende persistente l'esito.
     * Prima della transizione registra GestoreNotifiche fra gli osservatori del
     * Contenuto: sara' il Contenuto stesso, cambiando stato, ad avvisarlo (pattern
     * Observer), invece di essere questo gestore a chiamare direttamente le notifiche.
     */
    private void rendiVisibile(Contenuto contenuto) {
        contenuto.registraObserver(GestoreNotifiche.getIstanza());
        contenuto.pubblica();
        registroContenuti.aggiorna(contenuto);
        contenuto.rimuoviObserver(GestoreNotifiche.getIstanza());
    }

    private Contenuto trovaContenuto(Long idContenuto) {
        Contenuto contenuto = registroContenuti.trovaContenutoPerId(idContenuto);
        if (contenuto == null) {
            throw new IllegalArgumentException("Contenuto non trovato.");
        }
        return contenuto;
    }

    private void verificaDatiPubblicazione(String titolo, String descrizione, String categoria) {
        if (titolo == null || titolo.isBlank()) {
            throw new IllegalArgumentException("Il titolo del Contenuto e' obbligatorio.");
        }
        if (titolo.trim().length() > TITOLO_LUNGHEZZA_MAX) {
            throw new IllegalArgumentException(
                    "Il titolo non puo' superare i " + TITOLO_LUNGHEZZA_MAX + " caratteri.");
        }
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione del Contenuto e' obbligatoria.");
        }
        if (descrizione.trim().length() > DESCRIZIONE_LUNGHEZZA_MAX) {
            throw new IllegalArgumentException(
                    "La descrizione non puo' superare i " + DESCRIZIONE_LUNGHEZZA_MAX + " caratteri.");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("La categoria del Contenuto e' obbligatoria.");
        }
        try {
            Categoria.valueOf(categoria);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoria non ammessa: " + categoria);
        }
    }

    /**
     * Sezione appartenente al Corso indicato; null se idSezione e' null (Contenuto senza
     * Sezione oppure filtro non applicato).
     *
     * @throws IllegalArgumentException se la Sezione non appartiene a quel Corso:
     *                                  impedisce di associare materiale alla Sezione di
     *                                  un altro insegnamento.
     */
    private Sezione trovaSezioneDelCorso(Corso corso, Long idSezione) {
        if (idSezione == null) {
            return null;
        }
        for (Sezione sezione : registroCorsi.cercaSezioniPerCorso(corso)) {
            if (sezione.getId().equals(idSezione)) {
                return sezione;
            }
        }
        throw new IllegalArgumentException("La Sezione indicata non appartiene a questo Corso.");
    }

    private List<ContenutoDTO> toDTO(List<Contenuto> contenuti) {
        List<ContenutoDTO> risultato = new ArrayList<>();
        for (Contenuto contenuto : contenuti) {
            risultato.add(toDTO(contenuto));
        }
        return risultato;
    }

    private ContenutoDTO toDTO(Contenuto contenuto) {
        String titoloSezione = contenuto.getSezione() == null ? "-" : contenuto.getSezione().getTitolo();
        return new ContenutoDTO(contenuto.getId(), contenuto.getTitolo(), contenuto.getDescrizione(),
                contenuto.getCategoria().name(), contenuto.getDataPubblicazione(),
                titoloSezione, contenuto.getCodiceStato().name());
    }
}
