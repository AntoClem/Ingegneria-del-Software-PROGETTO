# JavaProject - Piattaforma di Gestione e Condivisione di Materiale Didattico

Base di progetto condivisa (Entity + Database + Controller + DTO), secondo il
pattern architetturale **BCED** richiesto dal template del corso. Manca
volutamente il livello **Boundary** (GUI): ciascun membro del team lo
implementerà per il proprio caso d'uso.

Testato end-to-end: compilazione e runtime verificati contro un DB H2 reale
(vedi sezione "Stato / cosa è stato testato" in fondo).

## Allineamento con il class diagram di analisi (reverse-engineered)

Il codice è stato allineato al class diagram che il team ha in repo
(reverse-engineered da Visual Paradigm) e ai tre sequence diagram di analisi
di UC7/UC13/UC15. Punti chiave:

- L'entità di dominio si chiama **`Contenuto`**, l'enum si chiama **`Categoria`**
  con valori `SLIDE`, `DISPENSE`, `ESERCIZI`, `SOLUZIONI`, `AVVISI`, `MATERIALE_INTEGRATIVO`.
- Login basato su **`username`** (distinto dall'email istituzionale), come da
  `Piattaforma.accesso(username, password)` nel class diagram.
- `Notifica` non ha un riferimento diretto al corso: memorizza solo
  `destinatario` (studente) e `contenutoCorrelato`. "Avviso" è infatti una
  categoria di `Contenuto` come le altre, quindi il corso è sempre derivabile
  tramite `contenutoCorrelato -> Contenuto -> Corso`.
- `Contenuto` porta con sé due comportamenti di dominio come da class diagram:
  `attiva()` e `modifica(titolo, descrizione, categoria)`.

**Discrepanze risolte con il team** (class diagram vs sequence diagram):
tra i due artifact il class diagram e i sequence diagram non erano perfettamente
allineati per due operazioni. Sono state seguite le indicazioni del team:

| Operazione | Scelta implementata |
|---|---|
| `filtraContenuti` | Filtra per **categoria + data + sezione** (versione ricca del sequence diagram UC13, non solo categoria) |
| `monitoraAndamento` | Restituisce **dati strutturati separati** (nContenuti, nStudenti, distribuzione) invece di una singola stringa |

**Nota su GRASP / responsabilità**: nel class diagram di analisi, operazioni
come `pubblicaContenuto`, `iscriviStudente`, `visualizzaContenuti`,
`monitoraAndamento` sono assegnate a `Corso` (Information Expert). A livello
di progettazione BCED, questa logica è stata spostata nei Controller perché
richiede accesso al Database (i DAO). Questa è esattamente la "Traduzione
classi ed associazioni" richiesta dal template (sezione 4.1.1) — può essere
riusata come contenuto per quella sezione della documentazione, spiegando
che l'Entity resta responsabile del comportamento "puro" (es. `Contenuto.attiva()`),
mentre le operazioni che coinvolgono persistenza sono state demandate al
Controller corrispondente.

## Mappatura Casi d'Uso -> Codice

| Caso d'uso | Metodo pubblico | Classe |
|---|---|---|
| **UC7** - PubblicaContenuto | `pubblicaContenuto(...)` | `ControllerGestioneContenuti` |
| **UC13** - VisualizzaContenutiCorso | `visualizzaContenutiCorso(...)`, `filtraContenuti(...)`, `selezionaContenuto(...)` | `ControllerGestioneContenuti` |
| **UC15** - MonitoraAndamentoCorso | `monitoraAndamentoCorso(...)` | `ControllerMonitoraggio` |
| **UC6** - *(non ancora definito dal team)* | - | - |

I nomi dei metodi (comprese le auto-deleghe private come `verificaDatiObbligatori`,
`aggiungiContenuto`, `inviaNotifica`, `calcolaContenutiPubblicati`, `applicaFiltri`,
ecc.) e la struttura dei blocchi `alt`/`opt` ricalcano i diagrammi di sequenza
di analisi concordati dal team (vedi i commenti nel codice con riferimento ai
passi del diagramma, es. `// 1.1: calcolaContenutiPubblicati(...)`).

## Struttura

```
JavaProject/
├── src/
│   ├── entity/       -> Utente (astratta), Docente, Studente, Corso, Sezione,
│   │                    Iscrizione, Contenuto, Categoria, Notifica, RuoloUtente
│   ├── database/      -> DBManager (singleton, unico punto di accesso al DB)
│   │                    + interfacce/implementazioni DAO (UtenteDAO, CorsoDAO,
│   │                    IscrizioneDAO, SezioneDAO, ContenutoDAO, NotificaDAO)
│   ├── controller/    -> ControllerAutenticazione, ControllerGestioneCorsi,
│   │                    ControllerGestioneContenuti, ControllerMonitoraggio,
│   │                    ControllerNotifiche
│   └── dto/           -> ContenutoDTO, AndamentoCorsoDTO, ProfiloDocenteDTO,
│                          EsitoPubblicazioneDTO
├── database/
│   └── schema.sql     -> DDL + dati di esempio (2 docenti, 4 studenti, 2 corsi,
│                          iscrizioni, sezioni, contenuti, notifiche)
├── lib/
│   └── h2.jar         -> driver JDBC H2 (incluso per comodità, non serve Maven)
└── .gitignore
```

## Come compilare ed eseguire (da linea di comando)

```bash
# Dalla cartella JavaProject/
javac -cp lib/h2.jar -d out src/entity/*.java src/database/*.java src/dto/*.java src/controller/*.java

# Esempio: inizializzare lo schema e lanciare una classe di test/main
java -cp lib/h2.jar:out NomeDellaTuaClasseMain
```

Su Windows sostituire `:` con `;` nel classpath.

## Come importare in Eclipse

1. File → New → Java Project → nome `JavaProject`, usa la cartella `src/` come source folder.
2. Tasto destro sul progetto → Build Path → Add External Archives → seleziona `lib/h2.jar`.
3. Nel tuo main, richiama `DBManager.getInstance().inizializzaSchema("database/schema.sql")`
   per creare le tabelle e popolarle con i dati di esempio (va rifatto solo una volta,
   o ogni volta che si vuole ripartire da un DB pulito: cancellare la cartella `data/`
   generata a runtime).

## Credenziali di esempio (dati precaricati da schema.sql)

| Ruolo    | Username   | Email                          | Password |
|----------|------------|---------------------------------|----------|
| Docente  | afasolino  | a.fasolino@unina.it            | pwd123   |
| Docente  | mbianchi   | m.bianchi@unina.it              | pwd123   |
| Studente | lverdi     | l.verdi@studenti.unina.it       | pwd123   |
| Studente | grossi     | g.rossi@studenti.unina.it       | pwd123   |

Corsi di esempio: `ISW2526` (Ingegneria del Software, docente Fasolino),
`BDD2526` (Basi di Dati, docente Bianchi).

## Cosa manca (lavoro individuale per UC)

- **Boundary**: interfacce grafiche (es. Swing) per i 4 casi d'uso assegnati.
- Il caso d'uso **UC6** non è ancora stato definito dal team (nessun sequence
  diagram condiviso finora) — codice non ancora presente.
- Eventuali raffinamenti ai Controller in base al diagramma di sequenza di
  progetto specifico del proprio caso d'uso.
- Test JUnit e Control Flow Graph / complessità ciclomatica per i metodi
  scelti (vedi template, sezione 6).

## Stato / cosa è stato testato

Compilazione ed esecuzione a runtime verificate (contro un DB H2 reale, non
solo verifica di sintassi) per:
- creazione schema + popolamento dati d'esempio;
- autenticazione tramite username, registrazione (con controllo duplicati
  su email e username);
- iscrizione diretta e autonoma (incluso rifiuto di doppia iscrizione);
- UC7 PubblicaContenuto: caso dati non validi (erroreDatiObbligatori), caso
  dati validi con notifica automatica, caso "non pubblicato subito" (nessuna
  notifica);
- UC13 VisualizzaContenutiCorso: elenco non filtrato, filtro per categoria,
  filtro per sezione, dettaglio singolo contenuto;
- UC15 MonitoraAndamentoCorso: conteggi e distribuzione per categoria;
- generazione automatica di notifiche agli iscritti, struttura senza corsoId.

Durante lo sviluppo è stato individuato e corretto un problema di integrità
referenziale (rimozione di un contenuto con notifiche collegate): la foreign
key `notifiche.contenuto_id` usa `ON DELETE SET NULL`.
