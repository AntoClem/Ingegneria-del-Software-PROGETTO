# Piattaforma per la gestione e la condivisione di materiale didattico

Progetto per il corso di **Ingegneria del Software** — Prof.ssa A. R. Fasolino
Corso di Laurea in Ingegneria Informatica, Università degli Studi di Napoli Federico II — A.A. 2025/26

**Gruppo 18** — Traccia n. 17

| Membro | Casi d'uso curati |
|---|---|
| Giovanni Aliperta | UC6 IscrivitiCorso |
| Attilio Loris Maria Bontempi | UC13 VisualizzaContenutiCorso |
| Carlo Cotino | UC7 PubblicaContenuto |
| Clemente Antonio  | UC15 MonitoraAndamentoCorso |

I casi d'uso UC1 (Registrazione), UC2 (Accesso), UC9 (AttivaContenuto), UC11 (RimuoviContenuto)
e UC22 (VisualizzaNotifiche) sono stati progettati e realizzati collegialmente dal gruppo.

---

## Che cos'è

Applicazione desktop Java per la pubblicazione e la consultazione di materiale didattico
universitario. I Docenti creano Corsi, vi pubblicano Contenuti organizzati in Sezioni e ne
monitorano l'andamento; gli Studenti si iscrivono ai Corsi tramite codice, consultano il
materiale pubblicato e ricevono una notifica a ogni nuova pubblicazione.

## Struttura del repository

```
├── Documentation/
│   ├── Documentazione_IS_Gruppo18.docx    ← documento di progetto (115 pagine)
│   ├── Documentazione_IS_Gruppo18.pdf
│   ├── Diagrammi/                          ← grafo di flusso per il test strutturale
│   └── Schermate/                          ← 11 schermate dell'applicazione in esecuzione
├── JavaProject/
│   ├── pom.xml
│   ├── sql/                                ← script di preparazione del database
│   └── src/
│       ├── main/java/it/unina/materialedidattico/
│       │   ├── boundary/                   ← interfaccia utente (Swing) e adattatori
│       │   ├── control/                    ← coordinamento dei casi d'uso
│       │   ├── entity/                     ← dominio, pattern State e Registri
│       │   ├── database/                   ← Façade di persistenza (Hibernate)
│       │   └── dto/                        ← oggetti di scambio fra i livelli
│       └── test/java/                      ← 37 test JUnit 5
└── VisualParadigm/
    ├── Progetto_IS_Gruppo18.vpp            ← modello UML completo
    └── Diagrammi/                          ← 25 diagrammi esportati a 300 dpi
```

## Architettura

Pattern architetturale **BCED** (Boundary — Control — Entity — Database) in forma stretta,
con dipendenze orientate esclusivamente dall'alto verso il basso.

| Livello | Contenuto |
|---|---|
| Boundary | 13 classi: schermate Swing, stile di presentazione, adattatore verso il servizio di notifiche |
| Control | 10 classi: Façade, quattro Gestori, sessione, factory |
| Entity | 19 classi: dominio, pattern State, tre Registri |
| Database | 2 classi: Façade di persistenza e factory JPA |

### Design pattern applicati

| Pattern | Dove | Requisito che lo motiva |
|---|---|---|
| Singleton | `JpaUtil`, `PiattaformaFacade`, Gestori, Registri, stati, `SessioneUtente` | costo di costruzione, unicità del punto di accesso |
| Façade | `PiattaformaFacade`, `GestorePersistenza`, i tre Registri | riduzione dell'accoppiamento |
| Factory Method | `UtenteFactory` → `StudenteFactory`, `DocenteFactory` | RF01 |
| State | `StatoContenuto` → `StatoBozza`, `StatoPubblicato`, `StatoRimosso` | RF10, RF12 |
| Observer | `Subject`/`Observer` → `GestoreNotifiche` | RF22, RF23 |
| Adapter | `ServizioNotifiche` ← `AdapterServizioNotifiche` → `GatewayNotificheEsterno` | V04 |

## Tecnologie

Java 17 · Apache Maven · Hibernate 6.4 (JPA 3.0) · MySQL 8 · Java Swing con FlatLaf · JUnit 5

## Come eseguirlo

**Prerequisiti:** JDK 17+, Maven 3.8+, MySQL Server 8 in ascolto sulla porta 3306.

```bash
# 1. Preparare lo schema (solo alla prima esecuzione)
mysql -u root -p < JavaProject/sql/reset_schema.sql

# 2. Compilare e avviare: Hibernate crea le tabelle dalle annotazioni
cd JavaProject
mvn clean compile
mvn exec:java -Dexec.mainClass="it.unina.materialedidattico.Main"

# 3. Chiudere l'applicazione e caricare i dati di dimostrazione
mysql -u root -p < sql/seed_data.sql
```

Le credenziali di connessione al database sono in
`src/main/resources/META-INF/persistence.xml` e vanno allineate alle proprie.

**Utenti di dimostrazione**

| Ruolo | Email | Password |
|---|---|---|
| Docente | `a.fasolino@unina.it` | `Password01` |
| Studente | `g.aliperta@studenti.unina.it` | `Password01` |

## Test

```bash
cd JavaProject && mvn test
```

37 test su 5 suite, che coprono i 9 casi d'uso realizzati. I test operano su uno schema
separato (`materialedidatticodb_test`) e non alterano i dati di dimostrazione.

| Suite | Casi d'uso | Test |
|---|---|---|
| `RegistrazioneTest` | UC1 | 9 |
| `AccessoTest` | UC2 | 8 |
| `IscrivitiCorsoTest` | UC6 | 5 |
| `PubblicaContenutoTest` | UC7, UC9, UC11 | 9 |
| `MonitoraAndamentoCorsoTest` | UC15, UC13, UC18, UC22 | 6 |

## Documentazione

Il documento di progetto e il modello Visual Paradigm costituiscono un unico elaborato:
ogni figura del documento è collegata al diagramma corrispondente in questo repository, e
ogni diagramma del modello riporta il riferimento al paragrafo che lo descrive.
L'indice completo della corrispondenza è nell'appendice A del documento.
