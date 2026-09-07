-- Dati di esempio per la Piattaforma di Gestione e Condivisione di Materiale Didattico.
--
-- La traccia richiede di progettare la base di dati "in maniera completa popolandola
-- con dei dati d'esempio". Lo schema NON va creato a mano: lo genera Hibernate al primo
-- avvio dell'applicazione (hibernate.hbm2ddl.auto=update in persistence.xml).
--
-- ORDINE DI ESECUZIONE
--   1. avviare una prima volta l'applicazione (crea il database e le tabelle);
--   2. eseguire questo script una sola volta da MySQL Workbench;
--   3. riavviare l'applicazione e accedere con una delle credenziali qui sotto.
--
-- Lo script si puo' rieseguire quante volte si vuole: la prima parte svuota le tabelle
-- (nell'ordine imposto dalle chiavi esterne), la seconda le ripopola. Gli identificatori
-- sono espliciti per rendere leggibili i collegamenti fra le tabelle.
--
-- CREDENZIALI DI PROVA (password in chiaro, coerenti con il modello dati del progetto)
--   Docente  : a.fasolino@unina.it            / Password01
--   Docente  : m.russo@unina.it               / Password01
--   Studente : g.aliperta@studenti.unina.it   / Password01
--   Studente : l.verdi@studenti.unina.it      / Password01
--   Studente : p.bianchi@studenti.unina.it    / Password01

USE materialedidatticodb;

-- ---------------------------------------------------------------------------
-- 1. Pulizia. La modalita' "safe update" di Workbench blocca le DELETE prive di
--    condizione su una chiave, quindi viene disattivata per la durata dello script.
--    L'ordine rispetta le chiavi esterne: prima le tabelle che referenziano le altre.
-- ---------------------------------------------------------------------------
SET SQL_SAFE_UPDATES = 0;

DELETE FROM NOTIFICA;
DELETE FROM ISCRIZIONE;
DELETE FROM CONTENUTO;
DELETE FROM SEZIONE;
DELETE FROM CORSO;
DELETE FROM UTENTE;

SET SQL_SAFE_UPDATES = 1;

-- ---------------------------------------------------------------------------
-- 2. Popolamento.
-- ---------------------------------------------------------------------------

-- Utenti: 2 Docenti (id 1-2) e 3 Studenti (id 3-5).
-- Tabella unica per tutta la gerarchia, con colonna discriminatrice TIPO.
INSERT INTO UTENTE (ID, NOME, COGNOME, EMAIL_ISTITUZIONALE, PASSWORD, TENTATIVI_FALLITI, TIPO) VALUES
(1, 'Anna',     'Fasolino', 'a.fasolino@unina.it',          'Password01', 0, 'DOCENTE'),
(2, 'Marco',    'Russo',    'm.russo@unina.it',             'Password01', 0, 'DOCENTE'),
(3, 'Giovanni', 'Aliperta', 'g.aliperta@studenti.unina.it', 'Password01', 0, 'STUDENTE'),
(4, 'Laura',    'Verdi',    'l.verdi@studenti.unina.it',    'Password01', 0, 'STUDENTE'),
(5, 'Paolo',    'Bianchi',  'p.bianchi@studenti.unina.it',  'Password01', 0, 'STUDENTE');

-- Corsi (id 1-2), ciascuno con il proprio Docente titolare.
INSERT INTO CORSO (ID, TITOLO, DESCRIZIONE, CODICE_UNIVOCO, ANNO_ACCADEMICO, DOCENTE_ID) VALUES
(1, 'Ingegneria del Software', 'Metodologie di sviluppo, UML, pattern architetturali BCED.', 'CINF2026A', '2025/2026', 1),
(2, 'Basi di Dati',            'Modello relazionale, SQL, progettazione concettuale e logica.', 'CINF2026B', '2025/2026', 2);

-- Sezioni (id 1-3).
INSERT INTO SEZIONE (ID, TITOLO, CORSO_ID) VALUES
(1, 'Modulo 1 - Analisi e specifica dei requisiti', 1),
(2, 'Modulo 2 - Progettazione BCED',                1),
(3, 'Modulo 1 - Modello relazionale',               2);

-- Contenuti (id 1-6): quattro pubblicati, uno ancora in bozza, uno senza Sezione.
-- La colonna STATO contiene il codice dello stato gestito dal pattern State.
INSERT INTO CONTENUTO (ID, TITOLO, DESCRIZIONE, CATEGORIA, DATA_PUBBLICAZIONE, STATO, CORSO_ID, SEZIONE_ID) VALUES
(1, 'Slide Lezione 1 - Introduzione',  'Introduzione al corso e al ciclo di vita del software.',            'SLIDE',    '2026-03-02', 'PUBBLICATO', 1, 1),
(2, 'Dispensa sui requisiti',          'Analisi dei requisiti: nomi-verbi, glossario, classificazione.',    'DISPENSE', '2026-03-09', 'PUBBLICATO', 1, 1),
(3, 'Slide sul pattern BCED',          'Boundary, Control, Entity, Database e regole di dipendenza.',       'SLIDE',    '2026-03-23', 'PUBBLICATO', 1, 2),
(4, 'Esercizi su BCED e persistenza',  'Esercitazione non ancora resa visibile agli studenti.',             'ESERCIZI', '2026-04-07', 'BOZZA',      1, 2),
(5, 'Avviso: ricevimento sospeso',     'Il ricevimento di giovedi e sospeso.',                              'AVVISI',   '2026-03-30', 'PUBBLICATO', 1, NULL),
(6, 'Slide sul modello relazionale',   'Relazioni, chiavi, vincoli di integrita referenziale.',             'SLIDE',    '2026-03-12', 'PUBBLICATO', 2, 3);

-- Iscrizioni (id 1-3): Giovanni e Laura a Ingegneria del Software, Paolo a Basi di Dati.
INSERT INTO ISCRIZIONE (ID, STUDENTE_ID, CORSO_ID, DATA_ISCRIZIONE) VALUES
(1, 3, 1, '2026-02-16'),
(2, 4, 1, '2026-02-17'),
(3, 5, 2, '2026-02-20');

-- Notifiche (id 1-2) gia' recapitate agli iscritti per l'ultimo materiale pubblicato.
INSERT INTO NOTIFICA (ID, MESSAGGIO, DATA, LETTA, DESTINATARIO_ID, CONTENUTO_ID) VALUES
(1, 'Nuovo materiale pubblicato nel corso Ingegneria del Software: Slide sul pattern BCED (SLIDE).', '2026-03-23', FALSE, 3, 3),
(2, 'Nuovo materiale pubblicato nel corso Ingegneria del Software: Slide sul pattern BCED (SLIDE).', '2026-03-23', FALSE, 4, 3);

-- Verifica rapida: deve restituire 5 utenti, 2 corsi, 3 sezioni, 6 contenuti, 3 iscrizioni.
SELECT (SELECT COUNT(*) FROM UTENTE)     AS utenti,
       (SELECT COUNT(*) FROM CORSO)      AS corsi,
       (SELECT COUNT(*) FROM SEZIONE)    AS sezioni,
       (SELECT COUNT(*) FROM CONTENUTO)  AS contenuti,
       (SELECT COUNT(*) FROM ISCRIZIONE) AS iscrizioni;
