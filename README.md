# FARO: UserService

## Descrizione del progetto
Il controllo della sicurezza negli ambienti industriali in cui vengono stoccate e movimentate sostanze pericolose rappresenta una delle sfide più delicate nella gestione degli impianti. Con l'aumento della complessità delle operazioni quotidiane, diventa fondamentale disporre di un supporto oggettivo che permetta di valutare in tempo reale se una determinata combinazione di attività concomitanti generi una condizione di rischio.

Per rispondere a questa esigenza è stato sviluppato **FARO** (Framework di Allerta e Rilevamento Operativo), un sistema che realizza un **Digital Twin** dell'area di stoccaggio: una rappresentazione virtuale costantemente sincronizzata con lo stato fisico dell'impianto. FARO integra tre componenti complementari: il monitoraggio ambientale tramite sensori collegati a un Raspberry Pi in ciascuna zona, il tracciamento della posizione e delle autorizzazioni del personale tramite beacon BLE rilevati dall'app mobile, e un modulo di pianificazione delle operazioni che, prima di autorizzare una nuova attività, ne valuta il rischio combinando una formula quantitativa consolidata in letteratura con un modello di Machine Learning. L'obiettivo comune di queste componenti è rispondere, in ogni istante, alla domanda: **è sicuro autorizzare questa operazione, in questa zona, adesso?**

---

## Architettura del sistema
FARO è organizzato secondo un'architettura a microservizi, in cui ciascun componente comunica con gli altri tramite API REST per le richieste sincrone e tramite RabbitMQ per gli eventi asincroni (AMQP tra microservizi, STOMP verso l'app mobile, MQTT per la diffusione degli allarmi d'area). Le principali componenti in cui si articola il sistema sono:

#### UserService *(repository corrente)*
Microservizio Quarkus responsabile della gestione degli utenti (lavoratori e amministratori), dell'autenticazione JWT e della gestione delle code di messaggistica personale di ciascun utente.

#### OperationalService
Microservizio Quarkus responsabile della gestione delle aree, degli item, delle sostanze pericolose e della pianificazione delle task, oltre all'orchestrazione della doppia valutazione del rischio (formula + Machine Learning).

#### MLService
Servizio FastAPI che espone il modello di Machine Learning per la classificazione del rischio delle task e genera, tramite un LLM locale (Ollama), una spiegazione testuale del verdetto.

#### EdgeService
Servizio FastAPI deployato sul Raspberry Pi presente in ogni area, responsabile dell'acquisizione delle misurazioni ambientali dal sensore DHT11 e della diffusione degli allarmi.

#### App mobile React Native
Applicazione sviluppata con React Native ed Expo che consente a lavoratori e amministratori di autenticarsi, tracciare automaticamente la propria posizione tramite beacon BLE, pianificare/evadere le task e ricevere notifiche in tempo reale.

#### RabbitMQ
Message broker che gestisce sia la messaggistica AMQP interna tra microservizi sia i protocolli STOMP e MQTT (tramite i relativi plugin) usati rispettivamente dall'app mobile per la coda personale e per la diffusione degli allarmi d'area con meccanismo di *retain*.

---

Di seguito viene fornita una descrizione dettagliata della componente implementata nella repository corrente.

## UserService

### Panoramica
*UserService* realizza tutti i requisiti relativi alla gestione degli utenti e dei ruoli del sistema. Si occupa di:
- registrazione di nuovi utenti (riservata agli amministratori);
- autenticazione e autorizzazione tramite token JWT;
- gestione del profilo e dell'anagrafica di utenti, lavoratori e amministratori;
- gestione delle code di messaggistica personale di ciascun utente;
- inoltro degli aggiornamenti di posizione ricevuti dall'app mobile verso *OperationalService*.

### Modello dati
Il modello dati è organizzato attorno a un'entità base `User`, di cui `Worker` e `Admin` sono sottoclassi:
- **User**: credenziali (email, password con hashing BCrypt), nome, cognome, ruolo, area corrente;
- **Worker**: aggiunge l'elenco delle aree per cui è autorizzato ad operare (`authorizedAreaIds`);
- **Admin**: aggiunge, se non è un amministratore globale, l'identificativo dell'unica area di propria competenza (`managedAreaId`).

### Autenticazione e sicurezza
L'autenticazione e l'autorizzazione delle richieste HTTP sono gestite tramite **JSON Web Token**, firmati con algoritmo HMAC-SHA256. Al login, il servizio genera un token contenente il soggetto (email), il ruolo (`ADMIN`/`WORKER`), l'identificativo dell'utente e, per gli amministratori con ambito ristretto, l'area di competenza gestita.

Poiché Quarkus non offre nativamente un meccanismo di autenticazione Bearer-JWT personalizzabile in questo modo, il progetto implementa:
- `BearerTokenAuthMechanism`: estrae il token dall'header `Authorization`;
- `JwtIdentityProvider`: valida firma e scadenza del token e costruisce l'identità di sicurezza applicativa;
- `JwtUtilities` / `SecurityConstants`: generazione e costanti di configurazione del token.

Le password non vengono mai memorizzate in chiaro: al momento della registrazione sono sottoposte ad hashing tramite **BCrypt** (`spring-security-crypto`), utilizzato anche in fase di verifica delle credenziali.

### Messaggistica: code personali
All'atto della creazione dell'account, `QueueSeeder` dichiara su RabbitMQ una coppia di code dedicate per ciascun utente:
- `faro.inbox.{userId}`: utilizzata per recapitare notifiche all'utente (assegnazione task, allarmi diretti);
- `faro.outbox.{userId}`: utilizzata per ricevere dall'app mobile gli aggiornamenti di posizione (`POSITION_UPDATE`).

Alla ricezione di un `POSITION_UPDATE`, *UserService* aggiorna l'area corrente dell'utente e inoltra l'informazione a *OperationalService* tramite l'exchange `faro.area.updates`, affinché quest'ultimo possa mantenere aggiornato il conteggio dei lavoratori presenti in ciascuna area. Il formato comune dei messaggi scambiati (`FaroMessage`) funge da wrapper con campo `type`, timestamp e payload specifico, per disaccoppiare i componenti del sistema.

### API REST
| Metodo | Path | Descrizione | Ruoli |
|--------|------|-------------|-------|
| POST | `/api/authenticate` | Login, restituisce token JWT | pubblico |
| POST | `/api/registration` | Registrazione worker/admin | ADMIN |
| GET | `/api/users/{id}` | Dettaglio utente | ADMIN, WORKER |
| PUT | `/api/users/{id}` | Modifica utente | ADMIN, WORKER |
| DELETE | `/api/users/{id}` | Eliminazione utente | ADMIN |
| GET/PUT/DELETE | `/api/workers/{id}` | Gestione anagrafica lavoratore | ADMIN, WORKER |
| PUT | `/api/workers/{id}/areas` | Assegnazione aree autorizzate | ADMIN |
| GET/PUT/DELETE | `/api/admins/{id}` | Gestione anagrafica amministratore | ADMIN |
| PUT | `/api/admins/{id}/area` | Assegnazione area di competenza | ADMIN |

### Struttura del progetto
```
src/main/java/it/unisalento/faro/
├── configuration/       # AdminSeeder, SecurityBeansProducer, configurazione RabbitMQ
├── domain/              # User, Worker, Admin, Role
├── dto/                 # DTO di login/registrazione, DTO principali, DTO dei messaggi, response DTO
├── exceptions/          # Eccezioni applicative (utente non trovato, email duplicata, ecc.)
├── repositories/        # UserRepository (Panache/MongoDB)
├── restcontrollers/     # AdminRestController, UserRestController, WorkerRestController, ...
├── security/            # BearerTokenAuthMechanism, JwtIdentityProvider, JwtUtilities
└── service/             # AdminService, UserService, WorkerService
```

### Tecnologie
- **Quarkus** (Java 21) con i moduli `quarkus-mongodb-panache`, `quarkus-security`, `quarkus-spring-web`/`quarkus-spring-di`, `quarkus-rabbitmq-client`, `rest-client`;
- **MongoDB** dedicato (principio *database-per-service*);
- **RabbitMQ** per la messaggistica asincrona (AMQP);
- **JWT** (HMAC-SHA256) + **BCrypt** per sicurezza e autenticazione;
- **Docker**, con immagini `linux/arm64` per il deployment su Raspberry Pi.
