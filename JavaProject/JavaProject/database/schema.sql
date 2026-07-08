-- =========================================================
-- Schema DB - Piattaforma di Gestione e Condivisione di
-- Materiale Didattico
-- Motore: H2 Database
-- =========================================================

DROP TABLE IF EXISTS notifiche;
DROP TABLE IF EXISTS contenuti;
DROP TABLE IF EXISTS sezioni;
DROP TABLE IF EXISTS iscrizioni;
DROP TABLE IF EXISTS corsi;
DROP TABLE IF EXISTS utenti;

-- ---------------------------------------------------------
-- UTENTI (Docenti e Studenti, discriminati da "ruolo")
-- ---------------------------------------------------------
CREATE TABLE utenti (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(40)  NOT NULL,
    cognome   VARCHAR(40)  NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    ruolo     VARCHAR(10)  NOT NULL CHECK (ruolo IN ('DOCENTE', 'STUDENTE'))
);

-- ---------------------------------------------------------
-- CORSI
-- ---------------------------------------------------------
CREATE TABLE corsi (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    titolo           VARCHAR(100) NOT NULL,
    descrizione      VARCHAR(500),
    codice_univoco   VARCHAR(20)  NOT NULL UNIQUE,
    anno_accademico  VARCHAR(9),
    docente_id       INT NOT NULL,
    CONSTRAINT fk_corsi_docente FOREIGN KEY (docente_id) REFERENCES utenti(id)
);

-- ---------------------------------------------------------
-- ISCRIZIONI (associazione Studente <-> Corso)
-- ---------------------------------------------------------
CREATE TABLE iscrizioni (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    studente_id      INT NOT NULL,
    corso_id         INT NOT NULL,
    data_iscrizione  DATE NOT NULL,
    modalita         VARCHAR(20) NOT NULL CHECK (modalita IN ('DIRETTA_DOCENTE', 'AUTONOMA_CODICE')),
    CONSTRAINT fk_iscr_studente FOREIGN KEY (studente_id) REFERENCES utenti(id),
    CONSTRAINT fk_iscr_corso FOREIGN KEY (corso_id) REFERENCES corsi(id),
    CONSTRAINT uq_iscrizione UNIQUE (studente_id, corso_id)
);

-- ---------------------------------------------------------
-- SEZIONI (moduli didattici all'interno di un corso)
-- ---------------------------------------------------------
CREATE TABLE sezioni (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    titolo    VARCHAR(100) NOT NULL,
    corso_id  INT NOT NULL,
    CONSTRAINT fk_sezioni_corso FOREIGN KEY (corso_id) REFERENCES corsi(id)
);

-- ---------------------------------------------------------
-- MATERIALI (contenuti didattici pubblicati)
-- ---------------------------------------------------------
CREATE TABLE contenuti (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    titolo              VARCHAR(150) NOT NULL,
    descrizione         VARCHAR(500),
    categoria           VARCHAR(25) NOT NULL CHECK (categoria IN
                         ('SLIDE','DISPENSE','ESERCIZI','SOLUZIONI','AVVISI','MATERIALE_INTEGRATIVO')),
    data_pubblicazione  DATE NOT NULL,
    pubblicato          BOOLEAN NOT NULL DEFAULT TRUE,
    corso_id            INT NOT NULL,
    sezione_id          INT,
    CONSTRAINT fk_contenuti_corso FOREIGN KEY (corso_id) REFERENCES corsi(id),
    CONSTRAINT fk_contenuti_sezione FOREIGN KEY (sezione_id) REFERENCES sezioni(id)
);

-- ---------------------------------------------------------
-- NOTIFICHE
-- ---------------------------------------------------------
CREATE TABLE notifiche (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    messaggio     VARCHAR(300) NOT NULL,
    data          DATE NOT NULL,
    letta         BOOLEAN NOT NULL DEFAULT FALSE,
    studente_id   INT NOT NULL,
    contenuto_id  INT,
    CONSTRAINT fk_notifiche_studente FOREIGN KEY (studente_id) REFERENCES utenti(id),
    CONSTRAINT fk_notifiche_contenuto FOREIGN KEY (contenuto_id) REFERENCES contenuti(id) ON DELETE SET NULL
);

-- =========================================================
-- DATI DI ESEMPIO
-- =========================================================

-- Docenti (password in chiaro solo a scopo dimostrativo)
INSERT INTO utenti (nome, cognome, email, username, password, ruolo) VALUES
('Anna', 'Fasolino', 'a.fasolino@unina.it', 'afasolino', 'pwd123', 'DOCENTE'),
('Marco', 'Bianchi', 'm.bianchi@unina.it', 'mbianchi', 'pwd123', 'DOCENTE');

-- Studenti
INSERT INTO utenti (nome, cognome, email, username, password, ruolo) VALUES
('Luca', 'Verdi', 'l.verdi@studenti.unina.it', 'lverdi', 'pwd123', 'STUDENTE'),
('Giulia', 'Rossi', 'g.rossi@studenti.unina.it', 'grossi', 'pwd123', 'STUDENTE'),
('Paolo', 'Neri', 'p.neri@studenti.unina.it', 'pneri', 'pwd123', 'STUDENTE'),
('Sara', 'Colombo', 's.colombo@studenti.unina.it', 'scolombo', 'pwd123', 'STUDENTE');

-- Corsi (docente_id 1 = Fasolino, 2 = Bianchi)
INSERT INTO corsi (titolo, descrizione, codice_univoco, anno_accademico, docente_id) VALUES
('Ingegneria del Software', 'Corso su analisi, progettazione e testing del software', 'ISW2526', '2025/26', 1),
('Basi di Dati', 'Corso di progettazione e gestione di basi di dati relazionali', 'BDD2526', '2025/26', 2);

-- Iscrizioni
INSERT INTO iscrizioni (studente_id, corso_id, data_iscrizione, modalita) VALUES
(3, 1, '2025-10-01', 'DIRETTA_DOCENTE'),
(4, 1, '2025-10-02', 'AUTONOMA_CODICE'),
(5, 1, '2025-10-03', 'AUTONOMA_CODICE'),
(3, 2, '2025-10-05', 'DIRETTA_DOCENTE'),
(6, 2, '2025-10-06', 'AUTONOMA_CODICE');

-- Sezioni del corso "Ingegneria del Software"
INSERT INTO sezioni (titolo, corso_id) VALUES
('Modulo 1 - Analisi dei Requisiti', 1),
('Modulo 2 - Progettazione', 1),
('Modulo 3 - Testing', 1);

-- Sezioni del corso "Basi di Dati"
INSERT INTO sezioni (titolo, corso_id) VALUES
('Modulo 1 - Modello Relazionale', 2);

-- Materiali del corso "Ingegneria del Software"
INSERT INTO contenuti (titolo, descrizione, categoria, data_pubblicazione, pubblicato, corso_id, sezione_id) VALUES
('Slide - Introduzione ai Requisiti', 'Slide della prima lezione', 'SLIDE', '2025-10-05', TRUE, 1, 1),
('Dispensa - Tecniche di Analisi', 'Dispensa di approfondimento', 'DISPENSE', '2025-10-06', TRUE, 1, 1),
('Esercizi - Diagrammi dei Casi d''Uso', 'Set di esercizi guidati', 'ESERCIZI', '2025-10-10', TRUE, 1, 2),
('Soluzioni - Diagrammi dei Casi d''Uso', 'Soluzioni degli esercizi', 'SOLUZIONI', '2025-10-12', FALSE, 1, 2),
('Avviso - Rinvio Lezione', 'La lezione di giovedì è rinviata', 'AVVISI', '2025-10-14', TRUE, 1, NULL);

-- Materiali del corso "Basi di Dati"
INSERT INTO contenuti (titolo, descrizione, categoria, data_pubblicazione, pubblicato, corso_id, sezione_id) VALUES
('Slide - Modello Relazionale', 'Introduzione al modello relazionale', 'SLIDE', '2025-10-07', TRUE, 2, 4),
('Materiale Integrativo - Normalizzazione', 'Approfondimento facoltativo', 'MATERIALE_INTEGRATIVO', '2025-10-09', TRUE, 2, 4);

-- Notifiche
INSERT INTO notifiche (messaggio, data, letta, studente_id, contenuto_id) VALUES
('Nuovo contenuto pubblicato: Slide - Introduzione ai Requisiti', '2025-10-05', TRUE, 3, 1),
('Nuovo contenuto pubblicato: Dispensa - Tecniche di Analisi', '2025-10-06', FALSE, 3, 2),
('Avviso dal docente: Rinvio Lezione', '2025-10-14', FALSE, 4, 5),
('Nuovo contenuto pubblicato: Slide - Modello Relazionale', '2025-10-07', FALSE, 3, 6);
