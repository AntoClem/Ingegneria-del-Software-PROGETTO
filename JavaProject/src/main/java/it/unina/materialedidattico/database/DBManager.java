package it.unina.materialedidattico.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unico punto di accesso al database H2 di persistenza.
 *
 * Implementa il pattern Singleton: un'unica istanza per l'intera
 * applicazione mantiene una connessione JDBC verso il database H2 in
 * modalita' embedded (il DB e' un file locale, non serve installare
 * ne' avviare alcun server separato - a differenza di MySQL).
 *
 * Tutte le classi DAO del package database dipendono da questa classe
 * per ottenere la Connection: in questo modo nessuna altra classe
 * dell'applicazione (e in particolare nessuna classe dei package
 * superiori, Control e Boundary) conosce i dettagli della stringa di
 * connessione, delle credenziali o del driver JDBC utilizzato: questo
 * e' esattamente il ruolo del package Database descritto dal template
 * ("unico punto di accesso vero e proprio al DB").
 *
 * Alla prima connessione, il costruttore esegue automaticamente lo
 * script schema.sql, incluso come risorsa sul classpath
 * (src/main/resources/schema.sql) e letto con getResourceAsStream.
 * Le istruzioni CREATE TABLE IF NOT EXISTS lo rendono idempotente:
 * si puo' rieseguire ad ogni avvio senza cancellare i dati gia'
 * presenti.
 */
public class DBManager {

    // Il file materialedidattico.mv.db viene creato automaticamente
    // da H2 alla prima connessione, nella cartella di esecuzione del
    // programma. AUTO_SERVER=TRUE consente a piu' processi (utile in
    // fase di sviluppo/debug) di accedere allo stesso file.
    private static final String URL = "jdbc:h2:./data/materialedidattico;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String SCHEMA_RESOURCE = "/schema.sql";

    private static DBManager istanza;
    private Connection connessione;

    private DBManager() {
        try {
            Class.forName("org.h2.Driver");
            connessione = DriverManager.getConnection(URL, USER, PASSWORD);
            eseguiSchema();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Impossibile inizializzare la connessione al database H2", e);
        }
    }

    /**
     * Restituisce l'unica istanza di DBManager (pattern Singleton),
     * creandola alla prima invocazione e riutilizzandola per tutte
     * quelle successive.
     */
    public static synchronized DBManager getIstanza() {
        if (istanza == null) {
            istanza = new DBManager();
        }
        return istanza;
    }

    /**
     * Restituisce la connessione JDBC condivisa verso il database H2.
     * E' il metodo che tutte le classi DAO del package invocano per
     * ottenere la Connection su cui eseguire le proprie query.
     */
    public Connection getConnessione() {
        return connessione;
    }

    /**
     * Chiude la connessione al database. Va invocato alla terminazione
     * dell'applicazione (vedi Main.java).
     */
    public void chiudi() {
        try {
            if (connessione != null && !connessione.isClosed()) {
                connessione.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la chiusura della connessione al database", e);
        }
    }

    /**
     * Legge lo script schema.sql dal classpath ed esegue tutte le
     * istruzioni SQL in esso contenute (una per ogni blocco separato
     * da ';'), per creare lo schema delle tabelle se non gia'
     * esistente.
     */
    private void eseguiSchema() throws SQLException {
        StringBuilder contenuto = new StringBuilder();
        try (InputStream is = DBManager.class.getResourceAsStream(SCHEMA_RESOURCE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String riga;
            while ((riga = reader.readLine()) != null) {
                contenuto.append(riga).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossibile leggere lo script schema.sql dal classpath", e);
        }

        try (Statement stmt = connessione.createStatement()) {
            for (String comando : contenuto.toString().split(";")) {
                String comandoPulito = comando.trim();
                if (!comandoPulito.isEmpty()) {
                    stmt.execute(comandoPulito);
                }
            }
        }
    }
}
