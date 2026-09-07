package it.unina.materialedidattico.entity;

import it.unina.materialedidattico.database.GestorePersistenza;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facciata del layer Entity per l'accesso ai Contenuti e alle Notifiche.
 * Espone ai Gestori del package Control le ricerche a grana grossa richieste dai
 * casi d'uso VisualizzaContenutiCorso (UC13), FiltraContenuti (UC18) e
 * MonitoraAndamentoCorso (UC15), nascondendo del tutto la tecnologia di persistenza.
 */
public class RegistroContenuti {

    private static RegistroContenuti istanza;

    private final GestorePersistenza gestorePersistenza;

    private RegistroContenuti() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public static synchronized RegistroContenuti getIstanza() {
        if (istanza == null) {
            istanza = new RegistroContenuti();
        }
        return istanza;
    }

    public void registraContenuto(Contenuto contenuto) {
        gestorePersistenza.salva(contenuto);
    }

    public Contenuto trovaContenutoPerId(Long id) {
        return gestorePersistenza.trovaPerId(Contenuto.class, id);
    }

    public Contenuto aggiorna(Contenuto contenuto) {
        return gestorePersistenza.aggiorna(contenuto);
    }

    /**
     * Tutti i Contenuti del Corso, in qualunque stato: serve al Docente titolare per
     * gestire anche le bozze non ancora attivate e i materiali rimossi.
     */
    public List<Contenuto> cercaContenutiPerCorso(Corso corso) {
        return gestorePersistenza.cercaPerCampo(Contenuto.class, "corso", corso);
    }

    /** Contenuti pubblicati del Corso, senza filtri (UC13). */
    public List<Contenuto> cercaContenutiPubblicati(Corso corso) {
        return gestorePersistenza.eseguiQuery(
                "SELECT c FROM Contenuto c WHERE c.corso = :corso AND c.codiceStato = :stato",
                Contenuto.class, Map.of("corso", corso, "stato", StatoContenutoEnum.PUBBLICATO));
    }

    /**
     * Contenuti pubblicati del Corso che rispettano i filtri indicati (UC18).
     * I parametri categoria, data e sezione sono opzionali: se null, il relativo
     * filtro non viene applicato e la clausola corrispondente non entra nella query.
     */
    public List<Contenuto> cercaContenutiConFiltri(Corso corso, Categoria categoria,
                                                   LocalDate data, Sezione sezione) {
        StringBuilder jpql = new StringBuilder(
                "SELECT c FROM Contenuto c WHERE c.corso = :corso AND c.codiceStato = :stato");
        Map<String, Object> parametri = new HashMap<>();
        parametri.put("corso", corso);
        parametri.put("stato", StatoContenutoEnum.PUBBLICATO);

        if (categoria != null) {
            jpql.append(" AND c.categoria = :categoria");
            parametri.put("categoria", categoria);
        }
        if (data != null) {
            jpql.append(" AND c.dataPubblicazione = :data");
            parametri.put("data", data);
        }
        if (sezione != null) {
            jpql.append(" AND c.sezione = :sezione");
            parametri.put("sezione", sezione);
        }

        return gestorePersistenza.eseguiQuery(jpql.toString(), Contenuto.class, parametri);
    }

    /** Numero di Contenuti pubblicati nel Corso (UC19, incluso in UC15). */
    public int contaContenutiPubblicati(Corso corso) {
        return cercaContenutiPubblicati(corso).size();
    }

    /**
     * Distribuzione dei Contenuti pubblicati per Categoria (UC20, incluso in UC15).
     * Il raggruppamento e' calcolato dal DBMS con una GROUP BY, senza caricare in
     * memoria l'intera collezione dei Contenuti.
     */
    public Map<Categoria, Integer> distribuzionePerCategoria(Corso corso) {
        List<Object[]> righe = gestorePersistenza.eseguiQuery(
                "SELECT c.categoria, COUNT(c) FROM Contenuto c "
                        + "WHERE c.corso = :corso AND c.codiceStato = :stato GROUP BY c.categoria",
                Object[].class, Map.of("corso", corso, "stato", StatoContenutoEnum.PUBBLICATO));

        Map<Categoria, Integer> distribuzione = new EnumMap<>(Categoria.class);
        for (Object[] riga : righe) {
            distribuzione.put((Categoria) riga[0], ((Number) riga[1]).intValue());
        }
        return distribuzione;
    }

    /** Contenuti pubblicati il cui titolo o descrizione contiene il testo cercato (RF20). */
    public List<Contenuto> cercaContenutiPerTesto(String testo) {
        return gestorePersistenza.eseguiQuery(
                "SELECT c FROM Contenuto c WHERE c.codiceStato = :stato AND "
                        + "(LOWER(c.titolo) LIKE :testo OR LOWER(c.descrizione) LIKE :testo)",
                Contenuto.class, Map.of("stato", StatoContenutoEnum.PUBBLICATO,
                        "testo", "%" + testo.toLowerCase() + "%"));
    }

    public void registraNotifica(Notifica notifica) {
        gestorePersistenza.salva(notifica);
    }

    /** Tutte le Notifiche ricevute dallo Studente, dalla piu' recente. */
    public List<Notifica> cercaNotifichePerStudente(Studente destinatario) {
        return gestorePersistenza.eseguiQuery(
                "SELECT n FROM Notifica n WHERE n.destinatario = :destinatario ORDER BY n.data DESC, n.id DESC",
                Notifica.class, Map.of("destinatario", destinatario));
    }

    public Notifica trovaNotificaPerId(Long id) {
        return gestorePersistenza.trovaPerId(Notifica.class, id);
    }

    public Notifica aggiornaNotifica(Notifica notifica) {
        return gestorePersistenza.aggiorna(notifica);
    }

    /** Notifiche non ancora lette dello Studente indicato. */
    public List<Notifica> cercaNotificheNonLette(Studente destinatario) {
        List<Notifica> risultato = new ArrayList<>();
        for (Notifica notifica : gestorePersistenza.cercaPerCampo(
                Notifica.class, "destinatario", destinatario)) {
            if (!notifica.isLetta()) {
                risultato.add(notifica);
            }
        }
        return risultato;
    }
}
