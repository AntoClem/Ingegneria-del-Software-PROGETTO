package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unico punto di accesso al database di persistenza (H2).
 * Le classi DAO si appoggiano a questa classe per ottenere la connessione,
 * cosi' da rendere le classi della Business Logic (Entity) indipendenti
 * dalla tecnologia di persistenza utilizzata.
 */
public class DBManager {

    private static final String DB_URL = "jdbc:h2:./data/materialidb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static DBManager instance;
    private Connection connection;

    private DBManager() {
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Impossibile inizializzare la connessione al DB", e);
        }
    }

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Esegue lo script SQL di creazione schema e popolamento dati d'esempio.
     * Utile in fase di sviluppo/test per ripartire da un DB pulito.
     *
     * @param schemaPath percorso del file .sql (es. "database/schema.sql")
     */
    public void inizializzaSchema(String schemaPath) {
        try {
            String script = new String(Files.readAllBytes(Paths.get(schemaPath)));
            try (Statement stmt = connection.createStatement()) {
                for (String sqlStatement : script.split(";")) {
                    String trimmed = sqlStatement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Errore durante l'inizializzazione dello schema", e);
        }
    }

    public void chiudiConnessione() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
