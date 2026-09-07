package it.unina.materialedidattico.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Incapsula la EntityManagerFactory della persistence unit "materialeDidatticoPU"
 * dichiarata in META-INF/persistence.xml.
 *
 * E' un Singleton perche' la factory e' una risorsa costosa, che va creata una sola
 * volta e condivisa da tutta l'applicazione; gli EntityManager, al contrario, sono
 * usa e getta: ne viene creato uno nuovo per ogni operazione e chiuso da chi lo usa.
 *
 * E' l'unica classe del progetto che conosce il nome della persistence unit: ne'
 * l'Entity, ne' il Control, ne' la Boundary sanno quale ORM o quale DBMS sia in uso.
 */
public class JpaUtil {

    private static JpaUtil istanza;

    private final EntityManagerFactory emf;

    private JpaUtil() {
        this.emf = Persistence.createEntityManagerFactory("materialeDidatticoPU");
        // Chiusura pulita della factory alla terminazione della JVM.
        Runtime.getRuntime().addShutdownHook(new Thread(this::chiudi));
    }

    public static synchronized JpaUtil getIstanza() {
        if (istanza == null) {
            istanza = new JpaUtil();
        }
        return istanza;
    }

    /** Nuovo EntityManager: rappresenta la singola sessione di lavoro col database. */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void chiudi() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
