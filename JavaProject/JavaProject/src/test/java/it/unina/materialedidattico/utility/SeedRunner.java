package it.unina.materialedidattico.utility;

import it.unina.materialedidattico.database.DBManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;

/**
 * Esegue una tantum sql/seed_data.sql sul database.
 * Lanciare una sola volta (Run): se rilanciato su un DB già popolato
 * dà errore sui vincoli UNIQUE, il che è normale e significa che i
 * dati ci sono già.
 */
public class SeedRunner {
    public static void main(String[] args) throws Exception {
        DBManager db = DBManager.getIstanza();
        String seed = new String(Files.readAllBytes(Paths.get("sql/seed_data.sql")));
        try (Statement stmt = db.getConnessione().createStatement()) {
            for (String comando : seed.split(";")) {
                String pulito = comando.replaceAll("--.*", "").trim();
                if (!pulito.isEmpty()) {
                    stmt.execute(pulito);
                }
            }
        }
        System.out.println("Dati di esempio caricati con successo.");
        db.chiudi();
    }
}
