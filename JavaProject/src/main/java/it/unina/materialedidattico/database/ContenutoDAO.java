package it.unina.materialedidattico.database;

import it.unina.materialedidattico.entity.Categoria;
import it.unina.materialedidattico.entity.Contenuto;
import it.unina.materialedidattico.entity.Corso;
import it.unina.materialedidattico.entity.Sezione;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Interfaccia DAO per la persistenza dell'Entity Contenuto. */
public interface ContenutoDAO {
    void salva(Contenuto contenuto);
    Contenuto cercaPerId(int id);

    /** Usato da VisualizzaContenutiCorsoController per l'elenco iniziale (nessun filtro applicato). */
    List<Contenuto> cercaPerCorso(Corso corso);

    /**
     * Usato da VisualizzaContenutiCorsoController quando lo Studente imposta i
     * filtri (caso d'uso incluso FiltraContenuti). I parametri categoria,
     * data e sezione possono essere null: in tal caso il relativo filtro non
     * viene applicato (query costruita dinamicamente in ContenutoDAOH2Impl).
     */
    List<Contenuto> cercaConFiltri(Corso corso, Categoria categoria, LocalDate data, Sezione sezione);

    /** Usato da MonitoraAndamentoCorsoController (caso d'uso incluso VisualizzaContenutiPubblicati). */
    int contaPerCorso(Corso corso);

    /** Usato da MonitoraAndamentoCorsoController (caso d'uso incluso VisualizzaDistribuzioneCategoria). */
    Map<Categoria, Integer> distribuzionePerCategoria(Corso corso);
}
