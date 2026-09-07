-- Ricostruzione completa dello schema del database.
--
-- QUANDO SERVE
-- Ogni volta che cambia la struttura delle classi Entity (un attributo aggiunto,
-- rimosso o rinominato). La proprieta' hibernate.hbm2ddl.auto=update aggiunge le
-- colonne nuove ma non rimuove mai quelle vecchie: dopo qualche modifica al modello
-- la tabella si ritrova colonne orfane, spesso dichiarate NOT NULL, che fanno
-- fallire gli inserimenti. Ricreare lo schema da zero e' piu' pulito, e non si perde
-- nulla perche' i dati di esempio stanno tutti in seed_data.sql.
--
-- COME SI USA (tre passaggi, in quest'ordine)
--   1. Workbench: eseguire questo script (fulmine giallo).
--   2. IntelliJ:  lanciare l'applicazione. Hibernate ricrea database e tabelle sulla
--                 base delle classi Entity aggiornate. L'accesso fallira': e' normale,
--                 il database e' ancora vuoto. Chiudere l'applicazione.
--   3. Workbench: aggiornare l'elenco degli schemi (icona di refresh nel pannello
--                 SCHEMAS) ed eseguire seed_data.sql.
-- Poi si puo' rilanciare l'applicazione e accedere.

DROP DATABASE IF EXISTS materialedidatticodb;

-- Il database viene ricreato automaticamente dall'applicazione: il parametro
-- createDatabaseIfNotExist=true nell'URL di connessione (persistence.xml) fa si'
-- che Hibernate lo crei al primo collegamento, insieme a tutte le tabelle.
