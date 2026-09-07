package it.unina.materialedidattico.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;

/**
 * Facciata del package Database: unico punto di accesso alla persistenza.
 *
 * Espone un'interfaccia CRUD generica, che lavora su Class&lt;T&gt; e non conosce
 * alcuna classe di dominio: il layer Database non dipende quindi da nessun altro
 * layer, e un eventuale cambio di ORM o di DBMS resterebbe confinato qui dentro.
 *
 * Sostituisce le classi DAO per entita' della versione precedente del progetto:
 * con un ORM le operazioni di traduzione oggetto-tabella sono a carico di
 * Hibernate, quindi non serve piu' una classe di accesso dedicata per ogni Entity.
 *
 * Ogni operazione apre il proprio EntityManager (chiuso nel finally) e la propria
 * transazione; in caso di errore in scrittura viene eseguito il rollback.
 */
public class GestorePersistenza {

    /**
     * Rende persistente un nuovo oggetto (e gli oggetti associati via cascade).
     * In caso di errore esegue il rollback e propaga l'eccezione al chiamante, che
     * e' l'unico a conoscere il caso d'uso in corso e quindi il messaggio da mostrare.
     */
    public void salva(Object oggetto) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(oggetto);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Salva piu' oggetti in un'unica transazione: o tutti o nessuno. */
    public void salvaTutti(Object... oggetti) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            em.getTransaction().begin();
            for (Object oggetto : oggetti) {
                em.persist(oggetto);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Ricerca per chiave primaria; null se non esiste. */
    public <T> T trovaPerId(Class<T> classe, Long id) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            return em.find(classe, id);
        } finally {
            em.close();
        }
    }

    /** Tutti gli oggetti della classe indicata. */
    public <T> List<T> trovaTutti(Class<T> classe) {
        return cercaPerCampi(classe, Map.of());
    }

    /** Oggetti della classe con campo = valore; il campo ammette percorsi annidati (es. "corso.id"). */
    public <T> List<T> cercaPerCampo(Class<T> classe, String nomeCampo, Object valore) {
        return cercaPerCampi(classe, Map.of(nomeCampo, valore));
    }

    /**
     * Ricerca con piu' condizioni in AND. La JPQL viene costruita dinamicamente,
     * ma i valori sono sempre passati come parametri della query, mai concatenati
     * nella stringa: nessun rischio di injection.
     */
    public <T> List<T> cercaPerCampi(Class<T> classe, Map<String, Object> campi) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder();
            jpql.append("SELECT e FROM ").append(classe.getSimpleName()).append(" e");

            if (!campi.isEmpty()) {
                jpql.append(" WHERE ");
                int contatore = 0;
                for (String nomeCampo : campi.keySet()) {
                    if (contatore > 0) {
                        jpql.append(" AND ");
                    }
                    // I punti dei percorsi annidati non sono ammessi nei nomi dei parametri JPQL.
                    jpql.append("e.").append(nomeCampo).append(" = :").append(nomeCampo.replace(".", "_"));
                    contatore++;
                }
            }

            TypedQuery<T> query = em.createQuery(jpql.toString(), classe);
            for (Map.Entry<String, Object> campo : campi.entrySet()) {
                query.setParameter(campo.getKey().replace(".", "_"), campo.getValue());
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Primo risultato di cercaPerCampi; null se non ce ne sono. */
    public <T> T cercaPrimoPerCampi(Class<T> classe, Map<String, Object> campi) {
        List<T> risultati = cercaPerCampi(classe, campi);
        return risultati.isEmpty() ? null : risultati.get(0);
    }

    /** Esegue una JPQL arbitraria, per le interrogazioni non esprimibili con cercaPerCampi. */
    public <T> List<T> eseguiQuery(String jpql, Class<T> classeRisultato, Map<String, Object> parametri) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(jpql, classeRisultato);
            if (parametri != null) {
                for (Map.Entry<String, Object> parametro : parametri.entrySet()) {
                    query.setParameter(parametro.getKey(), parametro.getValue());
                }
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Aggiorna un oggetto gia' persistente e ne restituisce la versione gestita. */
    public <T> T aggiorna(T oggetto) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            em.getTransaction().begin();
            T aggiornato = em.merge(oggetto);
            em.getTransaction().commit();
            return aggiornato;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Elimina per chiave primaria; false se l'oggetto non esiste. */
    public <T> boolean elimina(Class<T> classe, Long id) {
        EntityManager em = JpaUtil.getIstanza().getEntityManager();
        try {
            em.getTransaction().begin();
            T oggetto = em.find(classe, id);
            if (oggetto == null) {
                em.getTransaction().commit();
                return false;
            }
            em.remove(oggetto);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
