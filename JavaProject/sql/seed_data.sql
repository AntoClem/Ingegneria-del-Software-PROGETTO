-- Dati di esempio per la Piattaforma di Gestione e Condivisione di
-- Materiale Didattico, come richiesto dalla traccia ("si progetti la
-- base di dati in maniera completa popolandola con dei dati d'esempio").
--
-- Da eseguire DOPO schema.sql (le FOREIGN KEY richiedono le tabelle gia'
-- create). E' idempotente in pratica solo alla prima esecuzione: se il
-- database esiste gia' con questi dati, rieseguirlo fallira' sui vincoli
-- UNIQUE (EMAIL_ISTITUZIONALE, USERNAME, CODICE_UNICO, UQ_ISCRIZIONE).
-- Per ripartire da zero durante lo sviluppo, cancellare il file
-- data/materialedidattico.mv.db e rilanciare l'applicazione.

-- Utenti: 2 Docenti, 3 Studenti (ID generati da AUTO_INCREMENT: 1-5)
INSERT INTO UTENTE (NOME, COGNOME, EMAIL_ISTITUZIONALE, USERNAME, PASSWORD, TIPO) VALUES
('Anna', 'Fasolino', 'anna.fasolino@unina.it', 'afasolino', 'demo123', 'DOCENTE'),
('Marco', 'Russo', 'marco.russo@unina.it', 'mrusso', 'demo123', 'DOCENTE'),
('Giovanni', 'Aliperta', 'giovanni.aliperta@studenti.unina.it', 'galiperta', 'demo123', 'STUDENTE'),
('Laura', 'Verdi', 'laura.verdi@studenti.unina.it', 'lverdi', 'demo123', 'STUDENTE'),
('Paolo', 'Bianchi', 'paolo.bianchi@studenti.unina.it', 'pbianchi', 'demo123', 'STUDENTE');

-- Corsi (ID 1-2), ciascuno con Docente titolare
INSERT INTO CORSO (TITOLO, DESCRIZIONE, CODICE_UNIVOCO, ANNO_ACCADEMICO, DOCENTE_ID) VALUES
('Ingegneria del Software', 'Corso di Ingegneria del Software: metodologie, UML, pattern architetturali BCED.', 'CINF2024A', '2025/2026', 1),
('Basi di Dati', 'Corso di Basi di Dati: modello relazionale, SQL, progettazione concettuale e logica.', 'CINF2024B', '2025/2026', 2);

-- Sezioni (ID 1-3)
INSERT INTO SEZIONE (TITOLO, CORSO_ID) VALUES
('Modulo 1 - Analisi dei requisiti', 1),
('Modulo 2 - Progettazione BCED', 1),
('Modulo 1 - Modello relazionale', 2);

-- Contenuti (ID 1-5): alcuni pubblicati, uno non ancora, uno senza sezione
INSERT INTO CONTENUTO (TITOLO, DESCRIZIONE, CATEGORIA, DATA_PUBBLICAZIONE, PUBBLICATO, CORSO_ID, SEZIONE_ID) VALUES
('Slide Lezione 1 - Introduzione', 'Introduzione al corso e al ciclo di vita del software.', 'SLIDE', '2026-03-01', TRUE, 1, 1),
('Dispensa Requisiti', 'Dispensa su analisi e specifica dei requisiti.', 'DISPENSE', '2026-03-05', TRUE, 1, 1),
('Slide Pattern BCED', 'Slide sul pattern architetturale Boundary-Control-Entity-Database.', 'SLIDE', '2026-03-20', TRUE, 1, 2),
('Esercizi Modulo 2', 'Esercitazione pratica su BCED e DAO, non ancora resa visibile.', 'ESERCIZI', '2026-04-01', FALSE, 1, 2),
('Slide Modello Relazionale', 'Introduzione al modello relazionale.', 'SLIDE', '2026-03-10', TRUE, 2, NULL);

-- Iscrizioni (ID 1-3): Studenti 3 e 4 iscritti al Corso 1, Studente 5 al Corso 2
INSERT INTO ISCRIZIONE (STUDENTE_ID, CORSO_ID, DATA_ISCRIZIONE) VALUES
(3, 1, '2026-02-15'),
(4, 1, '2026-02-16'),
(5, 2, '2026-02-20');

-- Notifiche (ID 1-2): generate dalla pubblicazione dei Contenuti gia' visibili
INSERT INTO NOTIFICA (MESSAGGIO, DATA, LETTA, DESTINATARIO_ID, CONTENUTO_ID) VALUES
('E'' stato pubblicato un nuovo materiale in Ingegneria del Software: Slide Pattern BCED', '2026-03-20', FALSE, 3, 3),
('E'' stato pubblicato un nuovo materiale in Ingegneria del Software: Slide Pattern BCED', '2026-03-20', FALSE, 4, 3);
