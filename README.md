# TaskFlow2

## Indice

- [Quick Start](#quick-start)
- [Obiettivo Del Progetto](#obiettivo-del-progetto)
- [Stack Tecnologico](#stack-tecnologico)
- [Requisiti Tecnici](#requisiti-tecnici)
- [Dipendenze Principali](#dipendenze-principali)
- [DAO Pattern e Room Example](#dao-pattern-e-room-example)
- [Coroutine Guide](#coroutine-guide)
- [Hilt Scope Guide](#hilt-scope-guide)
- [Hilt Qualifier Guide](#hilt-qualifier-guide)
- [MVVM + DI Perche Funzionano Bene Insieme](#mvvm--di-perche-funzionano-bene-insieme)
- [Stato Attuale Del Progetto](#stato-attuale-del-progetto)
- [Licenza](#licenza)
- [Architettura](#architettura)
- [Principi Architetturali Applicati](#principi-architetturali-applicati)
- [Struttura Del Progetto](#struttura-del-progetto)
- [Entry Point Dellapp](#entry-point-dellapp)
- [Feature 1 TODO](#feature-1-todo)
- [Feature 2 Home](#feature-2-home)
- [Descrizione Dei Layer](#descrizione-dei-layer)
- [Flusso Dei Dati](#flusso-dei-dati)
- [Scelte Didattiche Intenzionali](#scelte-didattiche-intenzionali)
- [Cosa Non Fa Il Progetto](#cosa-non-fa-il-progetto)
- [Come Eseguire Il Progetto](#come-eseguire-il-progetto)
- [Come Estenderlo](#come-estenderlo)
- [Guida Rapida Ai File Piu Importanti](#guida-rapida-ai-file-piu-importanti)
- [Messaggio Finale](#messaggio-finale)

## Quick Start

Se hai appena scaricato o clonato il progetto:

### Android Studio

1. Apri la cartella `/TaskFlow2` in Android Studio
2. Attendi il sync Gradle iniziale
3. Se richiesto, accetta installazione o aggiornamento di SDK/Build Tools
4. Avvia un emulatore oppure collega un device Android
5. Premi `Run` sul modulo `app`

### Da terminale

Assicurati di avere Android Studio installato, cosi' Gradle puo' usare il JBR e l'SDK locale.

Build debug:

```bash
./gradlew assembleDebug
```

Test unitari:

```bash
./gradlew test
```

APK debug generato:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Note utili:

- `local.properties` non va condiviso: Android Studio lo rigenera in base al tuo SDK locale
- al primo avvio Gradle puo' scaricare dipendenze e impiegare un po' di tempo

Applicazione Android di esempio scritta in Kotlin con Jetpack Compose, pensata come progetto didattico per mostrare una struttura moderna, pulita e scalabile basata su:

- MVVM
- Clean Architecture
- Unidirectional Data Flow (UDF)
- organizzazione per feature

L'app contiene due feature:

- `feature_todo`: la feature principale, con lista TODO, aggiunta di elementi e toggle completato
- `feature_home`: una feature dimostrativa che mostra come integrare `local + remote + repository + use case + ViewModel` in una vertical slice completa, usando Hilt e Retrofit in modo guidato

In piu' c'e' un esempio separato di `DAO + Room` in `feature_todo_room_example`, tenuto fuori dalla schermata principale per non mischiare troppi concetti nella stessa feature.

## Obiettivo del progetto

Questo repository non nasce come semplice demo UI, ma come esempio architetturale. Lo scopo e' mostrare chiaramente:

- come separare business logic, UI e accesso ai dati
- perche' il `ViewModel` non deve contenere business logic
- perche' il `domain` deve dipendere da interfacce e non da classi concrete
- come applicare davvero l'inversion of dependency
- come modellare il flusso dati con `StateFlow`
- come organizzare il codice per feature, evitando package globali poco scalabili

## Stack tecnologico

- Kotlin `2.0.21`
- Android Gradle Plugin `8.13.2`
- Jetpack Compose
- Material 3
- AndroidX Lifecycle
- Hilt
- Retrofit + OkHttp
- Room
- StateFlow / Coroutines Flow
- MVVM + Clean Architecture

## Requisiti tecnici

- `minSdk = 24`
- `targetSdk = 36`
- `compileSdk = 36`
- Java/Kotlin target `11`

## Dipendenze principali

Il modulo `app` usa queste librerie principali:

- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.lifecycle:lifecycle-runtime-compose`
- `androidx.lifecycle:lifecycle-viewmodel-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.activity:activity-compose`
- Compose BOM
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`
- `com.google.dagger:hilt-android`
- `com.squareup.retrofit2:retrofit`
- `com.squareup.okhttp3:okhttp`
- `androidx.room:room-runtime`
- `androidx.room:room-ktx`

## DAO Pattern e Room Example

Nel progetto la feature TODO principale resta volutamente in-memory, ma ora c'e' anche un esempio separato di persistenza reale con Room in:

- `app/src/main/java/com/example/taskflow2/feature_todo_room_example/`

Questo esempio serve a mostrare il pattern DAO senza mischiarlo con la feature TODO gia' esistente.

### Cos'e' il pattern DAO

DAO significa `Data Access Object`.

L'idea e' semplice:

- il DAO contiene le operazioni di accesso ai dati
- le query SQL restano concentrate in un punto dedicato
- il resto dell'app non parla direttamente con il database

Nel nostro esempio:

- `RoomTodoDatabase` espone il database Room
- `RoomTodoDao` contiene le query sulla tabella `room_todo_items`
- `RoomTodoExampleRepositoryImpl` usa il DAO e traduce `Entity` <-> modello di dominio

### Perche' serve

Il DAO pattern migliora:

- separazione delle responsabilita'
- leggibilita' delle query
- testabilita' del data layer
- riduzione dell'accoppiamento con il database concreto

Regola pratica:

- il `Database` sa quali tabelle e DAO esistono
- il `Dao` sa come leggere e scrivere una tabella
- il `Repository` sa cosa esporre al domain

### `observeAll()` con Room

In [RoomTodoDao.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDao.kt) c'e' questo metodo:

```kotlin
@Query("SELECT * FROM room_todo_items ORDER BY id ASC")
fun observeAll(): Flow<List<RoomTodoEntity>>
```

Perche' e' interessante:

- ritorna un `Flow`
- non richiede polling manuale
- Room osserva la tabella e riemette i dati quando cambia

Quindi il comportamento mentale e':

- inserisci o aggiorni un record
- Room invalida la query
- il `Flow` del DAO emette il nuovo risultato
- repository, use case e UI possono reagire in cascata

### Come e' organizzato l'esempio

- [RoomTodoEntity.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoEntity.kt): formato di persistenza Room
- [RoomTodoDao.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDao.kt): DAO con `observeAll()`
- [RoomTodoDatabase.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDatabase.kt): database Room
- [RoomTodoExampleRepository.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/domain/repository/RoomTodoExampleRepository.kt): contratto del domain
- [RoomTodoExampleRepositoryImpl.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/repository/RoomTodoExampleRepositoryImpl.kt): adapter tra DAO e domain
- [ObserveAllRoomTodosUseCase.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/domain/usecase/ObserveAllRoomTodosUseCase.kt): use case minimale che espone il `Flow`
- [RoomTodoExampleModule.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo_room_example/di/RoomTodoExampleModule.kt): wiring Hilt del database, DAO e repository

### Nota didattica

Le [Android Developers docs su Room](https://developer.android.com/training/data-storage/room) oggi raccomandano KSP nei progetti Kotlin.

In questa repo l'esempio Room usa il setup `kapt` gia' presente per Hilt, cosi' resta piu' compatto e leggibile per chi studia. Concettualmente il pattern DAO non cambia: cambierebbe solo il tool di code generation.

## Coroutine Guide

Le coroutine sono il modo principale con cui Kotlin gestisce lavoro asincrono in modo leggibile.

### Cosa sono

Una coroutine e' un'unita' di lavoro leggera che puo' essere sospesa e ripresa senza bloccare il thread.

In pratica servono per:

- fare operazioni asincrone senza callback annidate
- evitare di bloccare il thread UI
- esprimere meglio flussi di dati e operazioni che richiedono tempo

### Perche' servono in Android

In Android il thread principale deve restare libero per:

- disegnare la UI
- ricevere input utente
- reagire velocemente a tocchi, scroll e navigazione

Se blocchiamo il main thread con lavoro lungo, l'app diventa scattosa o puo' andare in ANR.

Le coroutine aiutano a:

- spostare il lavoro asincrono fuori dal flusso bloccante
- aggiornare poi lo stato UI in modo ordinato
- mantenere il codice piu' semplice di callback, listener e thread manuali

### Differenza tra coroutine e thread

Coroutine e thread non sono la stessa cosa.

#### Thread

Un thread e' una risorsa del sistema operativo.

Caratteristiche:

- e' piu' pesante da creare e gestire
- esegue lavoro realmente in parallelo o concorrente a livello di sistema
- se ne crei troppi, consumi piu' memoria e coordinazione

Idea mentale:

- il thread e' il "binario fisico" su cui gira il lavoro

#### Coroutine

Una coroutine e' unita' di lavoro gestita a livello di linguaggio/libreria Kotlin.

Caratteristiche:

- e' molto piu' leggera di un thread
- puo' sospendersi e riprendere senza bloccare il thread
- molte coroutine possono condividere pochi thread sottostanti

Idea mentale:

- la coroutine e' il "task logico" che viene eseguito sopra uno o piu' thread

#### Differenza pratica

- thread = "dove gira fisicamente il lavoro"
- coroutine = "come modello e organizzo il lavoro asincrono"

Quindi:

- non creiamo una coroutine per avere automaticamente un nuovo thread
- una coroutine puo' girare sul thread principale o su thread di background, a seconda del dispatcher e del contesto
- tante coroutine possono essere multiplexate sugli stessi thread

#### Perche' in Kotlin preferiamo spesso coroutine ai thread manuali

Perche' con le coroutine otteniamo:

- codice piu' leggibile
- meno gestione manuale di thread, callback e sincronizzazione
- sospensione non bloccante con `delay`
- integrazione naturale con `Flow`, `StateFlow`, `viewModelScope` e test come `runTest`

#### Esempio mentale veloce

- creare un thread e' come assumere un nuovo lavoratore fisso
- creare una coroutine e' come aggiungere un nuovo compito a un sistema di lavoro che riusa i lavoratori gia' disponibili

#### Attenzione importante

Le coroutine non eliminano i problemi di concorrenza per magia.

Se piu' coroutine modificano lo stesso stato condiviso, restano possibili:

- race condition
- stato incoerente
- bug difficili da riprodurre

Quello che cambia e' che Kotlin offre strumenti molto piu' comodi per gestire questi casi rispetto a thread manuali e callback annidate.

### Come interagiscono con dispatcher e thread pool

Per capire davvero le coroutine, bisogna distinguere tre livelli:

- coroutine = unita' logica di lavoro
- dispatcher = regola che decide su quali thread eseguire quella coroutine
- thread pool = insieme reale di thread che il dispatcher puo' usare sotto al cofano

#### Dispatcher

Un dispatcher dice a Kotlin coroutines dove far partire o riprendere una coroutine.

In pratica decide:

- su quale contesto di esecuzione lavorare
- se restare sul main thread o andare su thread di background
- come distribuire il lavoro sui thread disponibili

Idea mentale:

- la coroutine e' il compito
- il dispatcher e' il coordinatore che decide chi lo esegue

#### Thread pool

Un thread pool e' un gruppo di thread riutilizzati per eseguire lavoro senza creare ogni volta thread nuovi.

Perche' e' utile:

- riusa thread gia' esistenti
- riduce overhead di creazione/distruzione
- permette di gestire meglio tanti task concorrenti

Molti dispatcher lavorano proprio sopra thread pool interni o condivisi.

#### Relazione pratica

Quando lanci una coroutine:

- non stai creando automaticamente un nuovo thread
- stai creando un task logico
- il dispatcher decide su quale thread o thread pool quel task verra' eseguito

Quindi due coroutine diverse possono:

- girare sullo stesso thread in momenti diversi
- girare su thread diversi dello stesso pool
- partire su un thread e riprendere su un altro, se il dispatcher lo consente

### Dispatcher piu' comuni

#### `Dispatchers.Main`

Serve per lavoro legato alla UI.

In Android:

- viene usato per aggiornare stato osservato dalla UI
- e' il dispatcher naturale del `viewModelScope`

Da usare per:

- aggiornamenti UI
- orchestrazione vicina alla schermata
- raccolta di eventi che devono poi riflettersi sulla UI

Da non usare per:

- lavoro pesante CPU
- operazioni lunghe di I/O

#### `Dispatchers.IO`

Serve per operazioni bloccanti o di input/output.

Esempi:

- file
- database
- rete

Idea pratica:

- se il lavoro potrebbe bloccare un thread per attesa I/O, spesso `IO` e' il dispatcher giusto

#### Differenza con Java per I/O

Se arrivi da Java, il parallelo mentale e' questo:

- in Java spesso gestisci I/O con `Thread`, `ExecutorService`, `Future`, callback o `CompletableFuture`
- in Kotlin con coroutine normalmente scrivi codice piu' lineare e sposti il lavoro con `withContext(Dispatchers.IO)`

Idea pratica:

- Java classico: "creo o uso un thread / executor per non bloccare il chiamante"
- Kotlin: "resto nella coroutine e cambio contesto verso `Dispatchers.IO`"

Quindi `Dispatchers.IO` non e' "un nuovo linguaggio per fare rete", ma il modo idiomatico di dire:

- questo pezzo puo' bloccare per file, database o rete
- non deve girare sul thread UI

Esempio mentale:

- Java: spesso ragioni in termini di thread da gestire
- Kotlin: ragioni piu' spesso in termini di coroutine + dispatcher

#### `Dispatchers.Default`

Serve per lavoro CPU-bound, cioe' calcolo.

Esempi:

- trasformazioni pesanti
- parsing importante
- algoritmi o elaborazioni che consumano CPU

Idea pratica:

- se il lavoro "calcola tanto" piu' che "attendere", spesso `Default` e' piu' adatto di `IO`

#### Dispatcher di test

Nei test usiamo dispatcher speciali di `kotlinx-coroutines-test`.

Nel progetto:

- [MainDispatcherRule.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/testutil/MainDispatcherRule.kt) sostituisce `Dispatchers.Main`
- [CoroutineTimeControlTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt) mostra tempo virtuale e controllo deterministico

Questo ci permette di:

- evitare attese reali
- controllare quando una coroutine avanza
- rendere i test stabili e ripetibili

### Esempio pratico nel codice

Nel progetto c'e' anche un esempio reale di `withContext(Dispatchers.IO)` in:

- [HomeRefreshAuditLogger.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_home/data/local/HomeRefreshAuditLogger.kt)

Li' facciamo una vera scrittura su file interno dell'app dopo il refresh Home.

Perche' e' corretto:

- scrivere su file e' I/O bloccante
- non vogliamo farlo sul main thread
- `Dispatchers.IO` e' pensato proprio per questo tipo di lavoro

Nota importante:

- non abbiamo aggiunto `withContext(IO)` attorno a Retrofit solo "perche' e' rete"
- come esempio didattico sarebbe stato meno pulito
- qui invece mostriamo un caso in cui il cambio di dispatcher e' chiaramente giustificato

### Perche' e' importante per evitare bug

Se scegli male il dispatcher:

- puoi bloccare il main thread
- puoi fare lavoro pesante nel posto sbagliato
- puoi ottenere prestazioni peggiori o UI scattosa

Se capisci il rapporto tra coroutine, dispatcher e thread pool:

- eviti di confondere "coroutine" con "thread"
- sai dove vive davvero il lavoro
- sai quando cambiare contesto e quando no

### Regola pratica veloce

- `Main` per lavoro vicino alla UI
- `IO` per operazioni che attendono risorse esterne
- `Default` per lavoro di calcolo
- dispatcher di test per controllare coroutine nei test

### Parole chiave essenziali

#### `suspend`

Una funzione `suspend` e' una funzione che puo' sospendersi senza bloccare il thread.

Nel progetto:

- i use case che fanno lavoro asincrono, come refresh o scrittura dati, usano `suspend`

Idea mentale:

- "questa operazione puo' richiedere tempo, ma non voglio bloccare il thread"

#### `launch`

`launch` avvia una coroutine che esegue lavoro in background logico e non restituisce un valore diretto.

In altre parole:

- serve quando vuoi "far partire un lavoro"
- non ti aspetti un risultato immediato come valore di ritorno
- ottieni un `Job`, utile per lifecycle, cancellazione o sincronizzazione, ma non un valore come con `async`

Nel progetto:

- i `ViewModel` usano `viewModelScope.launch { ... }` per reagire agli eventi UI

Perche' ha senso:

- il `ViewModel` puo' avviare lavoro asincrono e poi aggiornare `StateFlow`
- la coroutine viene legata al lifecycle del `ViewModel`

Esempio:

```kotlin
viewModelScope.launch {
    _uiState.update { it.copy(isRefreshing = true) }

    try {
        refreshHomeInfoUseCase()
    } finally {
        _uiState.update { it.copy(isRefreshing = false) }
    }
}
```

Spiegazione:

- `launch` fa partire il lavoro asincrono
- il `ViewModel` non blocca il thread UI
- non stiamo restituendo un valore diretto al chiamante
- aggiorniamo lo stato prima e dopo il lavoro

### `launch` e "fire and forget"

Molto spesso `launch` viene descritto come pattern "fire and forget".

Questo significa:

- fai partire un lavoro
- non aspetti un valore di ritorno diretto
- lasci che la coroutine completi il suo compito nel contesto in cui e' stata lanciata

Esempi tipici:

- salvare dati
- reagire a un click
- fare refresh di una schermata
- aggiornare stato UI dopo un'operazione asincrona

Ma "fire and forget" non significa "senza controllo".

Nel progetto, per esempio, non usiamo `GlobalScope.launch { ... }`:

- usiamo `viewModelScope.launch { ... }`
- quindi il lavoro parte, ma resta legato al lifecycle del `ViewModel`
- se il `ViewModel` viene distrutto, la coroutine viene cancellata

Questa e' la versione sana del "fire and forget" in Android:

- il caller non aspetta un valore
- ma la coroutine vive dentro una scope controllata

Esempio di fire and forget sano:

```kotlin
fun onRefreshClicked() {
    viewModelScope.launch {
        refreshHomeInfoUseCase()
    }
}
```

Spiegazione:

- il click avvia il lavoro
- non facciamo `await` di nessun risultato diretto
- la coroutine e' comunque controllata dal lifecycle del `ViewModel`

### Quando usare `launch`

Usa `launch` quando:

- devi avviare un effetto collaterale asincrono
- vuoi aggiornare stato osservabile, non restituire un valore
- stai reagendo a un evento UI
- hai gia' una scope chiara, come `viewModelScope`

### Quando NON e' la scelta migliore

`launch` non e' ideale quando:

- ti serve un valore di ritorno diretto
- vuoi comporre un risultato con altre coroutine
- vuoi esprimere chiaramente "questa operazione produce un output"

In quei casi spesso ha piu' senso:

- una funzione `suspend`
- oppure `async` / `await`, se ti serve davvero un risultato asincrono

### Regola pratica veloce

- `launch` = "avvia un lavoro"
- `suspend` = "definisci un'operazione sospendibile"
- `async` = "avvia un lavoro che produce un risultato"

#### `async` e `await`

`async` avvia una coroutine che produce un risultato.

A differenza di `launch`:

- `launch` restituisce un `Job`
- `async` restituisce un `Deferred<T>`
- quel `Deferred<T>` puo' essere letto con `await()`

Usalo quando:

- vuoi un valore asincrono
- vuoi comporre piu' operazioni concorrenti
- il risultato finale conta davvero per il chiamante

Esempio:

```kotlin
val profileDeferred = scope.async { fetchProfile() }
val todosDeferred = scope.async { fetchTodos() }

val profile = profileDeferred.await()
val todos = todosDeferred.await()
```

Spiegazione:

- facciamo partire due lavori che producono un risultato
- con `await()` aspettiamo il valore finale
- questo e' diverso da `launch`, che non nasce per restituire un output

Quando non serve:

- se vuoi solo avviare un effetto collaterale
- se non ti interessa un valore finale

In quei casi `launch` e' spesso piu' onesto e piu' semplice.

#### `withContext`

`withContext(...)` non avvia "un nuovo lavoro indipendente" come `launch`.

Serve invece a:

- cambiare contesto o dispatcher
- eseguire un blocco
- restituire il risultato di quel blocco
- poi tornare al contesto precedente

Esempio:

```kotlin
suspend fun loadAuditEntry(): String {
    return withContext(Dispatchers.IO) {
        file.readText()
    }
}
```

Spiegazione:

- entriamo temporaneamente in `Dispatchers.IO`
- facciamo lavoro bloccante di I/O
- restituiamo il risultato
- poi torniamo al contesto del chiamante

Differenza pratica rispetto a `launch`:

- `launch` = avvia una coroutine separata, senza valore diretto
- `withContext` = resta dentro il flusso della funzione corrente e restituisce un valore

Nel progetto:

- [HomeRefreshAuditLogger.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_home/data/local/HomeRefreshAuditLogger.kt) usa `withContext(Dispatchers.IO)` per scrivere su file in modo corretto

Regola pratica:

- `launch` se vuoi far partire un lavoro
- `async` se vuoi far partire un lavoro che produce un risultato
- `withContext` se vuoi cambiare dispatcher dentro una funzione e continuare il flusso normale

#### `try/catch`

`try/catch` e' la scelta giusta quando vuoi gestire localmente un errore dentro
una coroutine o dentro una funzione `suspend`.

Esempio:

```kotlin
viewModelScope.launch {
    try {
        refreshHomeInfoUseCase()
    } catch (e: Exception) {
        _uiState.update { it.copy(errorMessage = e.message) }
    }
}
```

Spiegazione:

- qui vogliamo trasformare l'errore in stato UI
- quindi `try/catch` e' piu' adatto di un handler globale

Nota importante:

- non bisogna "mangiare" `CancellationException`
- se la intercetti, in genere va rilanciata

Nel progetto:

- [HomeViewModel.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_home/presentation/HomeViewModel.kt) usa `try/catch` per convertire errori di refresh in `errorMessage`

#### `CoroutineExceptionHandler`

`CoroutineExceptionHandler` serve per gestire eccezioni non catturate in
coroutine avviate tipicamente con `launch`.

Ha senso soprattutto per:

- logging
- telemetry
- fallback globale
- debug

Esempio:

```kotlin
val handler = CoroutineExceptionHandler { _, throwable ->
    log(throwable)
}

scope.launch(handler) {
    error("boom")
}
```

Spiegazione:

- qui non stiamo recuperando localmente dall'errore
- stiamo dicendo: "se nessuno lo gestisce, passa da questo handler"

Nota importante:

- non sostituisce `try/catch` quando vuoi recuperare davvero
- e non e' il modo giusto per leggere errori di `async` tramite `await`

Nel progetto:

- [CoroutineErrorHandlingExamples.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/coroutines/CoroutineErrorHandlingExamples.kt) contiene un esempio dedicato
- [CoroutineErrorHandlingExamplesTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineErrorHandlingExamplesTest.kt) lo verifica

#### `supervisorScope`

`supervisorScope` serve quando vuoi che piu' coroutine figlie siano indipendenti:

- se una fallisce, le altre non vengono cancellate automaticamente

Questo e' utile quando vuoi risultati parziali.

Esempio:

```kotlin
supervisorScope {
    val first = async { runCatching { loadFirst() } }
    val second = async { runCatching { loadSecond() } }

    first.await() to second.await()
}
```

Spiegazione:

- senza `supervisorScope`, un fallimento di un figlio spesso cancella i fratelli
- con `supervisorScope`, puoi continuare a raccogliere i risultati indipendenti

Quando usarlo:

- dashboard con widget indipendenti
- schermate che possono mostrare dati parziali
- caricamenti paralleli dove un fallimento non deve buttare giu' tutto

Nel progetto:

- [CoroutineErrorHandlingExamples.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/coroutines/CoroutineErrorHandlingExamples.kt) contiene un esempio di `supervisorScope`
- [CoroutineErrorHandlingExamplesTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineErrorHandlingExamplesTest.kt) mostra che un risultato resta disponibile anche se l'altro fallisce

#### `coroutineScope` vs `supervisorScope`

Questi due costrutti sembrano simili, ma hanno una differenza architetturale importante.

##### `coroutineScope`

Con `coroutineScope`:

- i figli appartengono tutti alla stessa struttura
- se un figlio fallisce, il fallimento si propaga
- gli altri figli vengono cancellati

Esempio:

```kotlin
coroutineScope {
    val first = async { loadFirst() }
    val second = async { error("boom") }

    first.await()
    second.await()
}
```

Spiegazione:

- se `second` fallisce
- il fallimento si propaga all'intera scope
- `first` non continua indisturbato: viene cancellato

Quando ha senso:

- quando tutti i figli fanno parte della stessa operazione logica
- quando un fallimento deve far fallire tutto
- quando non avrebbe senso mostrare risultati parziali

Idea mentale:

- "o va bene tutto, o fallisce tutto"

##### `supervisorScope`

Con `supervisorScope`:

- i figli restano piu' indipendenti
- se un figlio fallisce, gli altri non vengono cancellati automaticamente
- puoi ancora raccogliere risultati parziali

Esempio:

```kotlin
supervisorScope {
    val first = async { runCatching { loadFirst() } }
    val second = async { runCatching { loadSecond() } }

    first.await() to second.await()
}
```

Spiegazione:

- se `second` fallisce
- `first` puo' comunque completare
- il chiamante puo' decidere come usare il risultato disponibile

Quando ha senso:

- dashboard con sezioni indipendenti
- schermate con caricamenti paralleli separati
- casi in cui un errore parziale non deve distruggere tutta l'esperienza

Idea mentale:

- "ogni figlio prova a fare il suo lavoro; poi valuto i risultati"

##### Regola pratica

- usa `coroutineScope` quando i figli fanno parte di un'unica operazione indivisibile
- usa `supervisorScope` quando vuoi isolare i fallimenti e salvare risultati parziali

### Cos'e' un `Job`

Quando usi `launch`, ricevi un `Job`.

Il `Job` rappresenta:

- la coroutine in esecuzione
- il suo stato
- la possibilita' di cancellarla o aspettarne la fine

Con un `Job` puoi, per esempio:

- controllare se e' ancora attivo
- cancellarlo
- fare `join()` per aspettarne il completamento
- usare `cancelAndJoin()` per cancellarlo e aspettarne subito la fine

Idea mentale:

- la coroutine e' il lavoro
- il `Job` e' il "manico" con cui puoi controllare quel lavoro

Esempio:

```kotlin
val job = scope.launch {
    delay(1_000)
}

if (job.isActive) {
    // il lavoro e' ancora in corso
}
```

Spiegazione:

- `launch` restituisce un `Job`
- il `Job` permette di osservare e controllare la coroutine
- puoi conservarlo se ti serve cancellare o aspettare il completamento

### Cancellazione delle coroutine

Le coroutine in Kotlin usano cancellazione cooperativa.

Questo significa:

- una coroutine non viene "uccisa brutalmente" in ogni punto possibile
- il codice deve trovarsi in punti sospendibili o controllare lo stato di cancellazione

La cancellazione funziona bene soprattutto quando la coroutine:

- usa funzioni sospendibili come `delay`
- raccoglie `Flow`
- controlla periodicamente il proprio stato

### `cancel()`, `join()` e `cancelAndJoin()`

#### `cancel()`

`cancel()` chiede a un `Job` di fermarsi.

Importante:

- non significa "interrompi tutto all'istante in modo brutale"
- significa "questa coroutine deve terminare appena collabora con la cancellazione"

Da solo, `cancel()` non aspetta la fine completa della coroutine.

Esempio:

```kotlin
val job = scope.launch {
    while (isActive) {
        delay(100)
    }
}

job.cancel()
```

Spiegazione:

- chiediamo alla coroutine di fermarsi
- la coroutine uscira' quando collaborera' con la cancellazione
- qui collabora grazie a `isActive` e `delay`

#### `join()`

`join()` aspetta che un `Job` finisca.

Serve quando vuoi dire:

- "non proseguire finche' questo lavoro non e' terminato"

`join()` non cancella il job: aspetta soltanto il completamento.

Esempio:

```kotlin
val job = scope.launch {
    delay(1_000)
}

job.join()
```

Spiegazione:

- qui aspettiamo soltanto la fine del lavoro
- se il job termina normalmente, `join()` riprende
- se il job era gia' terminato, `join()` rientra subito

#### `cancelAndJoin()`

`cancelAndJoin()` unisce i due passaggi:

- chiede la cancellazione
- aspetta che il job termini davvero

Nella pratica e' molto utile per test e shutdown ordinato, perche' evita di
scrivere separatamente:

```kotlin
job.cancel()
job.join()
```

Esempio diretto:

```kotlin
val job = scope.launch {
    while (isActive) {
        delay(100)
    }
}

job.cancelAndJoin()
```

Spiegazione:

- cancelliamo il job
- aspettiamo che la coroutine abbia davvero finito
- e' molto comodo in test, cleanup o shutdown ordinato

### `isActive`

`isActive` serve a sapere se la coroutine corrente e' ancora attiva.

Si usa spesso in loop o lavori lunghi, per esempio:

```kotlin
while (isActive) {
    // lavoro cooperativo
}
```

Esempio piu' completo:

```kotlin
val job = scope.launch {
    while (isActive) {
        doSmallWorkChunk()
        delay(50)
    }
}
```

Spiegazione:

- il loop continua solo finche' la coroutine e' attiva
- se qualcuno chiama `cancel()`, `isActive` diventera' `false`
- il lavoro si ferma senza continuare inutilmente

Perche' e' utile:

- permette di interrompere un lavoro lungo quando la scope viene cancellata
- evita che la coroutine continui a fare lavoro inutile
- aiuta a rispettare il lifecycle di `ViewModel`, screen o scope di test

### Quando controllare `isActive`

Controlla `isActive` soprattutto quando:

- hai loop lunghi
- fai lavoro CPU-bound a pezzi
- hai una coroutine lunga che non passa spesso da funzioni sospendibili

Se invece il codice usa gia' spesso funzioni sospendibili, molte volte la cooperazione alla cancellazione arriva gia' in modo naturale.

### `isCompleted` e `isCancelled`

Queste proprieta' vivono sul `Job` e aiutano a capire in che stato si trova.

#### `isActive`

Significa:

- il job e' partito e non e' ancora terminato

#### `isCompleted`

Significa:

- il job ha finito il suo ciclo di vita
- puo' essere finito normalmente oppure terminato dopo cancellazione

Quindi:

- un job cancellato alla fine diventa comunque anche `isCompleted == true`

#### `isCancelled`

Significa:

- il job e' terminato a seguito di cancellazione

Quindi:

- `isCompleted` dice "ha finito?"
- `isCancelled` dice "ha finito per cancellazione?"

### Lettura pratica degli stati

Caso 1: job ancora in corso

- `isActive = true`
- `isCompleted = false`
- `isCancelled = false`

Caso 2: job finito normalmente

- `isActive = false`
- `isCompleted = true`
- `isCancelled = false`

Caso 3: job cancellato

- `isActive = false`
- `isCompleted = true`
- `isCancelled = true`

Esempio pratico:

```kotlin
val job = scope.launch {
    delay(1_000)
}

job.cancelAndJoin()

println(job.isActive)     // false
println(job.isCompleted)  // true
println(job.isCancelled)  // true
```

Spiegazione:

- il job non sta piu' lavorando
- il suo ciclo di vita e' concluso
- la conclusione e' avvenuta per cancellazione, non per completamento normale

### Esempio mentale pratico

- `launch` fa partire il lavoro
- il `Job` ti permette di controllarlo
- `cancel()` chiede alla coroutine di fermarsi
- `join()` aspetta che finisca
- `cancelAndJoin()` la ferma e aspetta la fine
- `isActive` permette al codice della coroutine di capire se deve continuare o uscire
- `isCompleted` e `isCancelled` ti aiutano a leggere lo stato finale del job

### Perche' conta in Android

In Android questo e' fondamentale per evitare:

- lavoro inutile dopo che una schermata e' stata chiusa
- aggiornamenti di stato fuori lifecycle
- spreco di CPU e batteria

Per questo scope come `viewModelScope` sono cosi' utili:

- quando il `ViewModel` finisce, i `Job` figli vengono cancellati
- le coroutine che collaborano correttamente si fermano da sole

### Nota di sicurezza

Usare `launch` nel posto sbagliato puo' creare problemi:

- coroutine scollegate dal lifecycle
- errori piu' difficili da tracciare
- lavoro che continua anche quando la schermata non esiste piu'

Per questo in Android e' meglio evitare `GlobalScope.launch` nei casi normali e preferire scope legate al lifecycle, come `viewModelScope`.

#### `delay`

`delay(...)` sospende una coroutine per un certo tempo senza bloccare il thread.

Non e' come `Thread.sleep(...)`:

- `delay` sospende in modo cooperativo
- `Thread.sleep` blocca davvero il thread

Nel progetto:

- c'e' un test dedicato in [CoroutineTimeControlTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt) che mostra come testare `delay` senza aspettare tempo reale

### `Flow` e `StateFlow`

Le coroutine in questo progetto non servono solo per "fare cose in background", ma anche per modellare dati che cambiano nel tempo.

#### `Flow`

`Flow` rappresenta un flusso di valori emessi nel tempo.

Nel progetto:

- repository e use case espongono `Flow`
- i `ViewModel` osservano quel flusso e lo trasformano in stato UI

Idea mentale:

- "non ti do un solo valore, ti do una sequenza di aggiornamenti"

#### Differenza con Java per `Flow`

`Flow` non coincide con i `Stream` di Java.

Differenza chiave:

- `Stream` Java e' pensato soprattutto per trasformare collezioni gia' disponibili
- `Flow` Kotlin rappresenta valori che possono arrivare nel tempo, anche in modo asincrono

Quindi:

- `Stream` = elaborazione di una collection
- `Flow` = stream asincrono di emissioni nel tempo

Se arrivi da Java, `Flow` somiglia di piu' al mondo:

- callback/listener
- `Publisher` reattivi
- RxJava `Observable` o `Flowable`

ma con sintassi integrata nelle coroutine Kotlin.

Idea pratica:

- Java `Stream` e `List.stream()` sono utili per `map/filter/reduce` su dati gia' presenti
- Kotlin `Flow` serve quando i dati arrivano nel tempo, per esempio database osservabile, eventi, rete o stato UI

Per questo nel progetto usiamo `Flow` per:

- osservare dati che cambiano
- aggiornare la UI in modo reattivo
- evitare polling manuale

#### Cold Flow vs Hot Flow

Una distinzione molto importante e' questa:

- `Flow` normale e' spesso cold
- `SharedFlow` e `StateFlow` sono hot

Analogia semplice:

- cold flow = un barattolo chiuso che apri solo quando ti serve
- hot flow = un barattolo gia' aperto sul tavolo, che puo' cambiare anche mentre non lo stai guardando

#### Cold Flow

Un cold flow:

- non parte da solo
- in genere esegue il suo lavoro solo quando qualcuno fa `collect`
- puo' rieseguire l'upstream da capo per ogni nuovo collector

Idea mentale:

- il cold flow e' come una ricetta
- ogni volta che qualcuno la esegue, si ricomincia il lavoro
- oppure come un barattolo chiuso: finche' non lo apri, dentro non succede nulla per te

Esempio:

```kotlin
val coldFlow = flow {
    println("parto")
    emit(1)
}

coldFlow.collect { println(it) } // stampa "parto"
coldFlow.collect { println(it) } // stampa di nuovo "parto"
```

Questo e' utile quando:

- vuoi descrivere lavoro che parte on-demand
- vuoi che ogni collector abbia il proprio ciclo di esecuzione

#### Hot Flow

Un hot flow:

- esiste indipendentemente dal singolo collector
- puo' emettere anche se in quel momento nessuno sta ascoltando
- non riparte necessariamente da zero per ogni collector

Idea mentale:

- l'hot flow e' come una radio accesa
- tu puoi sintonizzarti ora, ma la trasmissione non nasce perche' tu ascolti
- oppure come un barattolo gia' aperto: quando arrivi, trovi gia' uno stato attuale

#### `SharedFlow`

`SharedFlow` e' un hot flow pensato per condividere emissioni con piu' collector.

Di solito si crea tramite `MutableSharedFlow`:

```kotlin
val events = MutableSharedFlow<String>()
val publicEvents: SharedFlow<String> = events
```

Quando usarlo:

- eventi one-shot
- messaggi broadcast
- stream condivisi tra piu' observer

Nota importante:

- se `replay = 0`, un collector che arriva tardi non riceve i valori gia' emessi
- quindi e' ottimo per eventi, meno per "stato corrente"

#### `StateFlow`

`StateFlow` e' un hot flow specializzato per rappresentare stato corrente.

Caratteristiche:

- ha sempre un valore attuale
- quando un collector si iscrive, riceve subito l'ultimo stato
- e' perfetto per UI state e osservazione continua

Nel progetto:

- i `ViewModel` espongono `StateFlow<UiState>`
- Compose osserva quello stato e ridisegna la UI quando cambia

#### `MutableStateFlow`

`MutableStateFlow` e' la versione mutabile usata di solito all'interno del producer.

Pattern tipico:

```kotlin
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState
```

Significato:

- dentro il `ViewModel` aggiorni `_uiState`
- fuori esponi solo `StateFlow`, cioe' la vista read-only

Questo e' importante per:

- incapsulamento
- evitare modifiche arbitrarie dall'esterno
- mantenere prevedibile il flusso dei dati

#### Regola pratica veloce

- `Flow` cold: lavoro on-demand, ogni collector puo' far ripartire l'upstream
- `SharedFlow`: hot flow per eventi condivisi
- `StateFlow`: hot flow per stato corrente
- `MutableStateFlow`: lato mutabile interno, spesso dentro `ViewModel` o data source

#### Esempio didattico nel progetto

- [FlowTemperatureTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/flow/FlowTemperatureTest.kt) mostra:
  - un cold flow che riparte per ogni collector
  - un `MutableSharedFlow` che perde i valori precedenti se non usa replay
  - un `MutableStateFlow` che espone subito l'ultimo stato disponibile

#### `collect`

`collect { ... }` serve a consumare i valori emessi da un `Flow`.

Esempio:

```kotlin
flowOf(1, 2, 3).collect { value ->
    println(value)
}
```

Spiegazione:

- il `Flow` emette valori nel tempo
- `collect` e' il punto in cui li ricevi davvero
- finche' non raccogli il `Flow`, spesso il lavoro non parte nemmeno

Nel progetto:

- i `ViewModel` usano `collect` per osservare i dati di repository e use case

#### `flowOf`

`flowOf(...)` crea un `Flow` a partire da valori gia' noti.

Esempio:

```kotlin
val numbers = flowOf(1, 2, 3)
```

Quando e' utile:

- esempi didattici
- test
- piccoli flussi statici
- casi in cui vuoi trasformare pochi valori gia' disponibili in un `Flow`

#### `asFlow`

`asFlow()` converte una collection o una sequenza in `Flow`.

Esempio:

```kotlin
val flow = listOf("a", "b", "c").asFlow()
```

Quando e' utile:

- quando hai gia' una lista o una sequenza
- quando vuoi trattarla con operatori e raccolta tipici dei `Flow`

#### Operatori `Flow` e pigrizia della pipeline

Una delle idee piu' importanti di `Flow` e' che molti operatori sono lazy.

Questo significa:

- puoi costruire una pipeline di trasformazioni
- ma il lavoro reale spesso non parte finche' non usi un terminal operator come `collect`

Idea mentale:

- `map`, `filter`, `transform`, `take`, `onEach`, `catch`, `onCompletion` descrivono il flusso
- `collect` lo avvia davvero

#### `map`

`map` trasforma ogni elemento in un altro elemento, uno a uno.

Esempio:

```kotlin
flowOf(1, 2, 3)
    .map { value -> value * 10 }
```

Utile quando:

- vuoi cambiare il formato di ogni valore
- vuoi convertire entity -> domain model
- vuoi fare trasformazioni semplici 1 -> 1

#### `filter`

`filter` lascia passare solo i valori che rispettano una condizione.

Esempio:

```kotlin
flowOf(1, 2, 3, 4)
    .filter { value -> value % 2 == 0 }
```

Utile quando:

- vuoi scartare elementi non validi
- vuoi far proseguire solo certi stati o eventi

#### `transform`

`transform` e' piu' flessibile di `map`.

Con `map` emetti normalmente un solo valore per ogni input. Con `transform` puoi:

- emettere zero valori
- emettere un valore
- emettere piu' valori

Esempio:

```kotlin
flowOf(2, 4).transform { value ->
    emit("item=$value")
    emit("double=${value * 2}")
}
```

Quando usarlo:

- quando `map` e `filter` separati diventano scomodi
- quando un input deve produrre piu' output

#### `take`

`take(n)` prende solo i primi `n` valori e poi interrompe la raccolta.

Esempio:

```kotlin
flowOf(10, 20, 30, 40).take(2)
```

Perche' e' utile:

- limita il numero di emissioni
- ferma prima la pipeline
- nei `Flow` freddi puo' anche interrompere il lavoro upstream prima del previsto

#### `onEach`

`onEach` esegue un effetto collaterale per ogni elemento, senza cambiare il valore.

Esempio:

```kotlin
flowOf(1, 2, 3)
    .onEach { value -> println("arrivato: $value") }
```

Utile per:

- logging
- debug
- telemetria
- side effect leggeri prima del `collect`

#### `catch`

`catch` intercetta eccezioni che arrivano dall'upstream del `Flow`.

Esempio:

```kotlin
flow {
    emit(1)
    throw IllegalStateException("boom")
}.catch { throwable ->
    emit(-1)
}
```

Nota importante:

- `catch` gestisce errori upstream
- non e' pensato per "inghiottire tutto" indiscriminatamente
- nelle coroutine bisogna continuare a trattare `CancellationException` con attenzione

#### `onCompletion`

`onCompletion` viene chiamato quando il `Flow` termina, sia normalmente sia per errore o cancellazione.

Esempio:

```kotlin
flowOf(1, 2, 3)
    .onCompletion { cause ->
        println(cause ?: "completed")
    }
```

Utile quando:

- vuoi sapere se il flusso e' finito
- vuoi loggare il motivo della chiusura
- vuoi fare cleanup leggero alla fine

#### `flowOn`

`flowOn(...)` serve a cambiare il dispatcher dell'upstream del `Flow`.

Questo e' il punto chiave:

- `flowOn` non sposta tutto il `Flow` in blocco
- sposta gli operatori che stanno prima di lui
- il collector e gli operatori downstream restano nel contesto del collector

Esempio:

```kotlin
flow {
    emit(loadFromDisk())
}
    .map { data -> parse(data) }
    .flowOn(Dispatchers.IO)
    .onEach { value ->
        println("collector side: $value")
    }
    .collect { value ->
        render(value)
    }
```

Come leggerlo:

- `flow { ... }` e `map { ... }` stanno upstream
- `flowOn(Dispatchers.IO)` li sposta su `IO`
- `onEach` e `collect` stanno downstream
- quindi continuano nel contesto del collector, per esempio `Main`

#### Perche' conta con i dispatcher

Nel README abbiamo gia' visto:

- `Dispatchers.Main` per lavoro vicino alla UI
- `Dispatchers.IO` per I/O bloccante
- `Dispatchers.Default` per lavoro CPU-bound

`flowOn` e' il modo idiomatico per dire:

- "questa parte upstream del flow non deve girare sul thread del collector"

Quindi:

- se l'upstream fa I/O, spesso userai `flowOn(Dispatchers.IO)`
- se l'upstream fa calcolo pesante, spesso userai `flowOn(Dispatchers.Default)`

#### Regola pratica importante

Dentro un `flow { ... }` non e' una buona idea cambiare dispatcher a caso con `withContext(...)` attorno a `emit(...)`.

Le docs ufficiali di Kotlin Flow spiegano che, se vuoi cambiare il contesto del flow, la strada corretta e' `flowOn(...)`, non emettere da un dispatcher diverso dentro il builder.

#### Cosa NON fa `flowOn`

- non cambia automaticamente il dispatcher del `collect`
- non rende `SharedFlow` "piu' background"
- non sostituisce la scelta architetturale del dispatcher giusto

Nota utile:

- la documentazione ufficiale specifica anche che applicare `flowOn` a uno `SharedFlow` non ha effetto, perche' `SharedFlow` non ha un execution context proprio

#### Esempio didattico nel progetto

- [FlowDispatcherTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/flow/FlowDispatcherTest.kt) mostra che `flowOn` sposta l'upstream su un dispatcher dedicato, mentre il collector resta nel proprio contesto

#### `collect`

`collect` e' il terminal operator piu' comune.

E' il punto in cui:

- il `Flow` parte davvero
- i valori vengono consumati
- la pipeline lazy smette di essere solo descrittiva

Senza `collect`:

- molti `Flow` non eseguono nulla
- `map`, `filter` e gli altri operatori restano solo una definizione del lavoro

#### Esempio didattico completo

```kotlin
val result = mutableListOf<String>()

flowOf(1, 2, 3, 4)
    .filter { value -> value % 2 == 0 }
    .map { value -> value * 10 }
    .transform { value ->
        emit("item=$value")
        emit("double=${value * 2}")
    }
    .take(3)
    .onEach { value ->
        println("onEach: $value")
    }
    .onCompletion { cause ->
        println(cause ?: "completed")
    }
    .collect { value ->
        result += value
    }
```

Cosa succede:

- `filter` lascia passare solo `2` e `4`
- `map` li trasforma in `20` e `40`
- `transform` espande ogni valore in piu' emissioni
- `take(3)` si ferma ai primi tre output
- `onEach` osserva ogni elemento che passa
- `onCompletion` segnala la fine
- `collect` avvia davvero tutto

Nel progetto:

- [FlowOperatorsTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt) mostra pipeline lazy, `transform`, `take`, `map`, `filter`, `collect`, `catch`, `onEach` e `onCompletion`

#### Esempi pratici nel progetto

- [FlowBuildersTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt) mostra `flowOf`, `asFlow` e `collect` in modo minimale e leggibile

#### `StateFlow`

`StateFlow` e' un tipo di `Flow` che rappresenta uno stato corrente sempre disponibile.

Nel progetto:

- i `ViewModel` espongono `StateFlow<UiState>`
- Compose osserva quello stato e ridisegna la UI quando cambia

Perche' e' utile:

- la UI legge sempre l'ultimo stato valido
- il flusso dati resta prevedibile e unidirezionale

### Come si collegano a MVVM qui

Nel progetto il flusso tipico e':

- la UI invia un evento
- il `ViewModel` lancia una coroutine con `viewModelScope.launch`
- il `ViewModel` chiama un use case `suspend` o osserva un `Flow`
- il repository restituisce dati o aggiorna una sorgente dati
- il `ViewModel` aggiorna `StateFlow`
- la UI osserva lo stato e si aggiorna

Questa combinazione rende il codice:

- piu' leggibile
- meno accoppiato
- piu' facile da testare

### Perche' `runTest` e' importante nei test

Nei test usiamo `runTest` per eseguire coroutine in un ambiente controllato.

Questo permette di:

- testare funzioni `suspend`
- controllare coroutine del `ViewModel`
- saltare `delay` senza aspettare tempo reale
- usare `advanceTimeBy(...)` e `advanceUntilIdle()`

Nel progetto:

- [CoroutineTimeControlTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt) mostra il controllo del tempo virtuale
- [CoroutineJobControlTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineJobControlTest.kt) mostra `Job`, `cancel()`, `join()`, `cancelAndJoin()`, `isActive`, `isCompleted` e `isCancelled`
- [MainDispatcherRule.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/testutil/MainDispatcherRule.kt) sostituisce `Dispatchers.Main` nei test dei `ViewModel`
- [InMemoryTodoDataSource.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSource.kt) contiene anche un esempio reale di `suspend fun fetchTodoLists()` con `delay(...)`
- [InMemoryTodoDataSourceTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSourceTest.kt) mostra come testarlo senza aspettare un secondo reale

### Regola pratica veloce

- usa coroutine per lavoro asincrono leggibile e non bloccante
- usa `suspend` per operazioni che possono richiedere tempo
- usa `Flow` per dati che cambiano nel tempo
- usa `StateFlow` per lo stato corrente della UI
- usa `runTest` nei test per controllare coroutine e tempo virtuale

## Hilt Scope Guide

Nel progetto al momento usiamo soprattutto `@Singleton`, ma e' utile capire bene la differenza con `@ActivityScoped` e `@ViewModelScoped`.

### `@Singleton`

Significato:

- esiste una sola istanza per tutto il ciclo di vita dell'app process
- vive nel `SingletonComponent`
- tutti i consumer che chiedono quella dipendenza ricevono la stessa istanza finche' il processo resta vivo

Quando usarlo:

- client condivisi come `Retrofit`, `OkHttpClient`, interceptor, repository condivisi
- cache applicative o oggetti stateless che non devono essere ricreati spesso
- dipendenze che servono in piu' schermate o in piu' feature

Quando NON usarlo:

- per oggetti che dipendono strettamente da una singola schermata o da un singolo `ViewModel`
- per stato UI temporaneo
- per oggetti che vuoi ricreare a ogni `Activity` o a ogni `ViewModel`

Nel progetto:

- `OkHttpClient`, `Retrofit`, `HomeApiService`, `HomeRepositoryImpl`, `HomeLocalDataSource`, `HomeRemoteDataSource` e `TeachingMockInterceptor` sono pensati come dipendenze condivise, quindi `@Singleton` ha senso

### `@ActivityScoped`

Significato:

- esiste una sola istanza per una specifica `Activity`
- vive nel `ActivityComponent`
- se l'`Activity` viene distrutta e ricreata, anche la dipendenza viene distrutta e ricreata

Quando usarlo:

- oggetti legati a una singola `Activity`
- coordinatori UI, navigator, controller o helper che hanno senso solo dentro quella schermata Android
- dipendenze che devono essere condivise tra fragment o composable ospitati dalla stessa `Activity`, ma non dal resto dell'app

Quando NON usarlo:

- per oggetti che vuoi riusare in tutta l'app: in quel caso meglio `@Singleton`
- per oggetti che devono vivere quanto il `ViewModel`
- se vuoi sopravvivere alle configuration change: `@ActivityScoped` non e' la scope giusta, per quel caso in Hilt di solito si valuta `@ActivityRetainedScoped`

Esempio mentale:

- "serve solo finche' questa Activity esiste" -> `@ActivityScoped`

### `@ViewModelScoped`

Significato:

- esiste una sola istanza per uno specifico `ViewModel`
- vive nel `ViewModelComponent`
- tutte le dipendenze iniettate dentro quel `ViewModel` condividono la stessa istanza scoped, ma un altro `ViewModel` ricevera' una istanza diversa

Quando usarlo:

- oggetti di supporto al `ViewModel`
- mapper stateful, use case stateful, sessioni temporanee o orchestratori che devono vivere esattamente quanto il `ViewModel`
- dipendenze che devono essere condivise tra piu' use case o helper dello stesso `ViewModel`, ma non fuori da li'

Quando NON usarlo:

- per client globali di rete o repository condivisi
- per oggetti che devono essere condivisi tra piu' `ViewModel`
- per oggetti che devono vivere solo dentro una singola `Activity` ma non dentro il `ViewModel`

Esempio mentale:

- "serve solo a questo ViewModel e deve morire con lui" -> `@ViewModelScoped`

### Regola pratica veloce

- usa `@Singleton` per infrastruttura condivisa e dipendenze app-wide
- usa `@ActivityScoped` per oggetti legati alla vita di una specifica `Activity`
- usa `@ViewModelScoped` per oggetti che appartengono a un singolo `ViewModel`

### Regola ancora piu' importante

La scope deve sempre riflettere il ciclo di vita reale dell'oggetto, non solo il punto in cui viene iniettato.

Se scegli una scope troppo larga:

- l'oggetto vive piu' del necessario
- rischi stato condiviso indesiderato
- aumenti accoppiamento e bug difficili da capire

Se scegli una scope troppo stretta:

- l'oggetto viene ricreato troppo spesso
- perdi cache o stato utile
- il comportamento puo' diventare incoerente tra schermate e rotazioni

## Hilt Qualifier Guide

I qualifier servono a dire a Hilt quale dipendenza vogliamo quando il solo tipo
non basta piu' a distinguerla.

### Built-in qualifiers usati qui

#### `@ActivityContext`

Significato:

- chiede a Hilt il `Context` della Activity corrente
- e' utile per dipendenze che devono vivere quanto la Activity

Quando usarlo:

- helper, coordinator o oggetti `@ActivityScoped`
- casi in cui serve davvero il contesto della schermata corrente

Quando NON usarlo:

- in un `@Singleton`
- in oggetti che devono sopravvivere alla Activity

Nel progetto:

- [ActivitySessionTracker.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/activity/ActivitySessionTracker.kt) usa `@ActivityContext` e per questo non deve diventare `@Singleton`

#### `@ApplicationContext`

Significato:

- chiede a Hilt il `Context` dell'applicazione
- e' il context sicuro da usare in componenti app-wide

Quando usarlo:

- servizi applicativi, provider, helper globali, risorse condivise
- oggetti `@Singleton` che hanno bisogno di un `Context`

Quando NON usarlo:

- se hai davvero bisogno del lifecycle o del comportamento di una singola Activity

Nel progetto:

- [AppIdentityProvider.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/app/AppIdentityProvider.kt) usa `@ApplicationContext`, quindi puo' stare tranquillamente in `@Singleton` senza leak di UI

### Custom qualifiers usati qui

#### Perche' esistono

Se nel grafo hai piu' dipendenze dello stesso tipo, per esempio:

- piu' `String`
- piu' `OkHttpClient`
- piu' `Retrofit`

Hilt non puo' indovinare quale vuoi. Un qualifier custom elimina l'ambiguita'.

#### `@AppPackageName`

Serve a distinguere una `String` che rappresenta il package name dell'app da altre `String` possibili.

Nel progetto:

- e' dichiarato in [AppQualifiers.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/di/AppQualifiers.kt)
- viene fornito da [AppModule.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/di/AppModule.kt)
- viene consumato da [AppIdentityProvider.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/app/AppIdentityProvider.kt)

#### `@TeachingBaseUrl` e `@TeachingHttpClient`

Servono a distinguere:

- la base URL didattica dell'esempio
- l'`OkHttpClient` usato per il flusso di rete della feature Home

Nel progetto:

- sono dichiarati in [NetworkQualifiers.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/di/NetworkQualifiers.kt)
- vengono usati in [NetworkModule.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt)

### Regola pratica veloce

- usa i built-in qualifier di Hilt quando ti serve distinguere il tipo di `Context`
- usa qualifier custom quando hai piu' dipendenze dello stesso tipo ma con significati diversi
- se inizi ad avere piu' `String`, `Retrofit` o `OkHttpClient`, aggiungi qualifier prima che il grafo diventi ambiguo

### Nota anti-leak

Un qualifier non gestisce il lifecycle: quello e' compito della scope.

Quindi:

- qualifier = "quale dipendenza voglio?"
- scope = "per quanto tempo deve vivere?"

Per evitare leak, servono entrambe le scelte giuste:

- usa `@ActivityContext` solo con dipendenze legate alla Activity
- usa `@ApplicationContext` per singleton che richiedono un context
- non mettere in `@Singleton` oggetti che trattengono Activity, View o `ActivityContext`

## MVVM + DI Perche Funzionano Bene Insieme

MVVM e dependency injection si rafforzano a vicenda.

### Vantaggi principali

- il `ViewModel` resta focalizzato su stato UI ed eventi, invece di creare da solo repository, data source o client di rete
- la business logic puo' vivere in use case e repository, quindi si testa senza passare dalla UI
- le dipendenze concrete si possono sostituire con fake o stub nei test
- il wiring resta fuori dalla schermata e fuori dal `ViewModel`, quindi il codice e' piu' leggibile e meno accoppiato
- cambiare implementazione tecnica diventa piu' semplice: il `ViewModel` continua a parlare con astrazioni o collaboratori gia' pronti

### Cosa dimostra questo progetto

Nel progetto i test mostrano proprio questi vantaggi:

- [AddTodoUseCaseTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/AddTodoUseCaseTest.kt)
  dimostra che la regola "non aggiungere titoli vuoti" vive nel use case e si testa con un repository fake
- [TodoViewModelTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/feature_todo/presentation/TodoViewModelTest.kt)
  dimostra che il `ViewModel` aggiorna `UiState` e reagisce agli eventi senza UI reale, grazie a use case e repository iniettati
- [HomeViewModelTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/feature_home/presentation/HomeViewModelTest.kt)
  dimostra che anche un `@HiltViewModel` resta facile da testare per semplice constructor injection, usando collaboratori fake al posto del wiring reale
- [CoroutineTimeControlTest.kt](/Users/matteoperotta/AndroidStudioProjects/TaskFlow2/app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
  dimostra come `runTest`, `advanceTimeBy` e `advanceUntilIdle` permettano di testare coroutine e `delay` senza aspettare tempo reale

### Regola pratica

Se un `ViewModel` e' difficile da testare, spesso significa una di queste cose:

- sta creando da solo le dipendenze
- contiene troppa business logic
- dipende da dettagli Android o da oggetti concreti invece che da collaboratori sostituibili

In quel caso MVVM + DI non sono ancora applicati fino in fondo.

## Stato attuale del progetto

Il progetto e' compilabile e verificato con:

- `./gradlew assembleDebug`
- `./gradlew test`

## Licenza

Questo repository usa una licenza personalizzata di tipo "learning-only".

In breve:

- puoi usarlo, studiarlo e modificarlo per imparare
- non puoi usarlo per insegnare, fare corsi, workshop, tutoring o contenuti formativi derivati da questo progetto

Il testo completo e' disponibile nel file `LICENSE`.

## Architettura

L'app segue una variante pratica di Clean Architecture organizzata per feature.

Ogni feature contiene i propri layer interni:

```text
feature_x/
  data/
    local/
    remote/      (solo se serve)
    repository/
  domain/
    model/
    repository/
    usecase/
  presentation/
    Contract.kt
    ViewModel.kt
  ui/
    Screen.kt
  di/
    FeatureModule.kt
```

## Principi architetturali applicati

### 1. Dependency Rule

Le dipendenze puntano verso l'interno:

- `ui` dipende da `presentation`
- `presentation` dipende da `domain`
- `data` dipende da `domain`
- `domain` non dipende da Android, Compose o dettagli tecnici

Il `domain` e' il centro delle regole dell'app.

### 2. Inversion of Dependency

I casi d'uso non conoscono implementazioni concrete. Dipendono da interfacce definite nel `domain`.

Esempio:

- `TodoRepository` e' dichiarato nel `domain`
- `TodoRepositoryImpl` vive nel `data`
- i use case dipendono da `TodoRepository`, non da `TodoRepositoryImpl`

Questo permette di:

- cambiare sorgenti dati senza toccare il `domain`
- testare piu' facilmente
- mantenere il codice disaccoppiato

### 3. Business logic fuori dal ViewModel

Il `ViewModel`:

- riceve eventi utente
- aggiorna lo stato UI
- orchestra i use case

Il `ViewModel` non deve:

- validare regole di dominio
- decidere come trasformare il business
- conoscere dettagli del repository concreto

La business logic vive nei use case, per esempio:

- `AddTodoUseCase` valida il titolo
- `ToggleTodoUseCase` decide come invertire `completed`

### 4. Repository != Data Source

Nel progetto i ruoli sono distinti:

- `DataSource`: componente tecnico che legge/scrive su una fonte dati concreta
- `Repository`: adapter che coordina le sorgenti dati e offre un contratto al dominio
- `UseCase`: rappresenta un'azione del dominio applicativo

### 5. UDF - Unidirectional Data Flow

Il flusso della feature TODO e' questo:

```text
UI -> TodoUiEvent -> TodoViewModel -> UseCase -> Repository -> DataSource
                                                   |
                                                   v
                                              risultato
                                                   |
                                                   v
StateFlow <- TodoUiState <- TodoViewModel <- Flow dal repository
```

La UI non modifica direttamente lo stato. Invia eventi. Il `ViewModel` aggiorna `StateFlow`. Compose osserva lo stato e ridisegna.

## Struttura del progetto

```text
app/src/main/java/com/example/taskflow2/
  TaskFlowApplication.kt
  MainActivity.kt
  core/
    di/
    network/
  feature_todo/
    data/
      local/
      repository/
    di/
    domain/
      model/
      repository/
      usecase/
    presentation/
    ui/
  feature_home/
    data/
      local/
      remote/
      repository/
    di/
    domain/
      model/
      repository/
      usecase/
    presentation/
    ui/
  feature_todo_room_example/
    data/
      local/
      repository/
    di/
    domain/
      model/
      repository/
      usecase/
  ui/theme/
```

## Entry point dell'app

File principale:

- `app/src/main/java/com/example/taskflow2/MainActivity.kt`
- `app/src/main/java/com/example/taskflow2/TaskFlowApplication.kt`

Responsabilita':

- inizializzare l'app Android
- inizializzare Hilt a livello application
- ottenere il `HomeViewModel` tramite Hilt e il `TodoViewModel` tramite factory manuale
- collegare `ViewModel` e schermate Compose

La `MainActivity` non contiene business logic. Fa da composition root Android. `TaskFlowApplication` prepara invece il grafo Hilt dell'app.

## Feature 1: TODO

La feature principale mostra:

- una sezione header opzionale in cui attualmente viene renderizzata la Home demo
- titolo della schermata
- campo di input per una nuova attivita'
- bottone `Aggiungi`
- lista TODO
- checkbox per marcare completato/non completato

### Comportamento

All'avvio:

- il `TodoViewModel` osserva il `Flow` dei TODO
- lo stato iniziale parte con `isLoading = true`
- appena arrivano i dati, la UI viene aggiornata

Quando l'utente digita:

- la UI invia `TodoUiEvent.InputChanged`
- il `ViewModel` aggiorna `TodoUiState.inputTitle`

Quando l'utente preme `Aggiungi`:

- la UI invia `TodoUiEvent.AddClicked`
- il `ViewModel` invoca `AddTodoUseCase`
- il use case fa `trim()` e blocca i titoli vuoti
- se il TODO viene aggiunto, il campo input viene svuotato

Quando l'utente fa toggle su un item:

- la UI invia `TodoUiEvent.ToggleClicked(todo)`
- il `ViewModel` invoca `ToggleTodoUseCase`
- il use case crea una nuova copia del modello con `completed = !completed`
- il repository salva il nuovo stato

### Struttura interna della feature TODO

#### Domain

- `Todo`: entita' del dominio
- `TodoRepository`: contratto astratto del dominio
- `GetTodosUseCase`: espone il flusso della lista TODO
- `FetchTodoListsUseCase`: espone uno snapshot one-shot della lista TODO
- `AddTodoUseCase`: contiene la regola "non aggiungere titoli vuoti"
- `ToggleTodoUseCase`: contiene la logica di inversione del completamento

#### Data

- `InMemoryTodoDataSource`: fake DB in memoria
- `TodoRepositoryImpl`: implementa il repository del dominio

#### Presentation

- `TodoContract.kt`
  - `TodoUiState`
  - `TodoUiEvent`
- `TodoViewModel`

#### UI

- `TodoScreen`
- `TodoItem`

#### DI

- `TodoFeatureModule`

### File principali della feature TODO

- `app/src/main/java/com/example/taskflow2/feature_todo/domain/model/Todo.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/domain/repository/TodoRepository.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/domain/usecase/GetTodosUseCase.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/domain/usecase/FetchTodoListsUseCase.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/domain/usecase/AddTodoUseCase.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/domain/usecase/ToggleTodoUseCase.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSource.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/data/repository/TodoRepositoryImpl.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/presentation/TodoContract.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/presentation/TodoViewModel.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/ui/TodoScreen.kt`
- `app/src/main/java/com/example/taskflow2/feature_todo/di/TodoFeatureModule.kt`

## Feature 2: Home

`feature_home` e' una feature dimostrativa, ma e' integrata realmente nell'app. Serve a mostrare un caso in cui il repository coordina:

- `local`
- `remote`
- mapping verso il `domain`

In questa feature il progetto mostra anche:

- Hilt per la dependency injection
- Retrofit per il contratto HTTP
- OkHttp `Interceptor` per mockare la risposta e mantenere l'esempio eseguibile senza backend reale

### Cosa mostra

La Home renderizza:

- messaggio di benvenuto
- stato del server
- etichetta ultimo aggiornamento
- bottone `Refresh remoto`

### Comportamento

All'avvio:

- `HomeViewModel` osserva i dati della Home
- parte un refresh remoto automatico
- l'utente vede prima la cache locale iniziale e poi i dati remoti aggiornati

Quando l'utente preme `Refresh remoto`:

- la UI invia `HomeUiEvent.RefreshClicked`
- il `ViewModel` invoca `RefreshHomeInfoUseCase`
- il repository chiama il `remote data source`
- il `remote data source` usa `HomeApiService` creato da Retrofit
- il risultato remoto viene salvato nel `local data source`
- la UI si aggiorna osservando il `Flow` locale

### Perche' questa feature e' utile didatticamente

Mostra bene che:

- il repository coordina fonti diverse
- il domain continua a vedere solo `HomeRepository`
- il `ViewModel` non chiama mai direttamente un data source
- local e remote non devono essere conosciuti dalla UI
- Hilt puo' vivere nello strato esterno senza contaminare il domain
- Retrofit puo' restare confinato nel data layer

### Struttura interna della feature Home

#### Domain

- `HomeInfo`
- `HomeRepository`
- `ObserveHomeInfoUseCase`
- `RefreshHomeInfoUseCase`

#### Data

- `HomeLocalDataSource`
- `HomeApiService`
- `HomeRemoteDataSource`
- `HomeRepositoryImpl`

#### Presentation

- `HomeContract.kt`
  - `HomeUiState`
  - `HomeUiEvent`
- `HomeViewModel`

#### UI

- `HomeScreen`

#### DI

- `NetworkModule`
- `HomeFeatureModule`

### File principali della feature Home

- `app/src/main/java/com/example/taskflow2/feature_home/domain/model/HomeInfo.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/domain/repository/HomeRepository.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/domain/usecase/ObserveHomeInfoUseCase.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/domain/usecase/RefreshHomeInfoUseCase.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/data/local/HomeLocalDataSource.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/data/remote/HomeApiService.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/data/remote/HomeRemoteDataSource.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/data/repository/HomeRepositoryImpl.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/presentation/HomeContract.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/presentation/HomeViewModel.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/ui/HomeScreen.kt`
- `app/src/main/java/com/example/taskflow2/feature_home/di/HomeFeatureModule.kt`
- `app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt`
- `app/src/main/java/com/example/taskflow2/core/network/TeachingMockInterceptor.kt`

## Descrizione dei layer

### `domain`

Contiene il cuore dell'app:

- modelli di dominio
- interfacce dei repository
- casi d'uso

Regole:

- non dipende da Android
- non dipende da Compose
- non dipende da implementazioni concrete

### `data`

Contiene i dettagli tecnici:

- accesso ai dati locali
- accesso ai dati remoti
- implementazioni concrete dei repository
- mapping tra modelli tecnici e di dominio

Regole:

- puo' dipendere dal `domain`
- non deve contenere business logic di dominio

### `presentation`

Contiene:

- `UiState`
- `UiEvent`
- `ViewModel`

Regole:

- orchestra i use case
- non contiene regole di business
- non conosce dettagli di persistenza

### `ui`

Contiene solo Compose:

- schermate
- componenti visuali

Regole:

- legge stato
- invia eventi
- non chiama repository o data source

### `di`

Contiene il wiring concreto:

- moduli Hilt o factory manuali
- binding tra interfacce e implementazioni
- creazione dello stack di rete
- costruzione del repository e delle dipendenze esterne

Regole:

- e' uno strato esterno
- puo' conoscere dettagli concreti e framework-specifici

## Flusso dei dati

### Flusso TODO

1. `TodoScreen` invia un `TodoUiEvent`
2. `TodoViewModel` riceve l'evento
3. il `ViewModel` chiama un use case
4. il use case usa `TodoRepository`
5. `TodoRepositoryImpl` usa `InMemoryTodoDataSource`
6. i dati aggiornati risalgono come `Flow`
7. il `ViewModel` aggiorna `TodoUiState`
8. Compose ridisegna la schermata

### Flusso Home

1. `HomeScreen` invia `HomeUiEvent.RefreshClicked`
2. `HomeViewModel` chiama `RefreshHomeInfoUseCase`
3. il use case usa `HomeRepository`
4. `HomeRepositoryImpl` legge dal remoto e salva nel locale
5. il `local data source` emette il nuovo stato
6. `ObserveHomeInfoUseCase` espone il dato al `ViewModel`
7. `HomeUiState` viene aggiornato
8. Compose ridisegna l'header Home

## Perche' questa struttura e' piu' pulita di package globali tipo `presentation.viewmodel`

Invece di avere package globali basati sul tipo:

```text
presentation/viewmodel/
data/repository/
domain/usecase/
```

il progetto usa package per feature:

```text
feature_todo/...
feature_home/...
```

Vantaggi:

- ogni feature e' una vertical slice autonoma
- la navigazione del codice e' piu' semplice
- e' piu' facile aggiungere nuove feature
- si riduce il rumore di package globali troppo generici
- `UiState`, `UiEvent` e `ViewModel` restano vicini

## Scelte didattiche intenzionali

Questo progetto fa alcune scelte esplicitamente pedagogiche:

- usa un `InMemoryTodoDataSource` nella feature TODO principale per mantenere chiaro il focus architetturale
- affianca un esempio separato `feature_todo_room_example` per mostrare DAO + Room senza appesantire la schermata TODO
- usa una feature `todo` con dependency injection manuale per far vedere chiaramente il wiring "a mano"
- usa una feature `home` con Hilt per mostrare la stessa architettura con una DI moderna
- usa Retrofit davvero, ma con un `Interceptor` mockato per mostrare il flusso `remote -> local -> domain -> UI` senza richiedere un backend reale
- usa commenti in italiano e CRC card per spiegare responsabilita' e collaborazioni

## Cosa NON fa il progetto

Per scelta, questo esempio non include:

- navigazione multi-screen
- l'uso di Room dentro la feature TODO principale
- una vera API remota pubblica o un backend dedicato
- gestione errori di rete avanzata
- test di integrazione dedicati a Room o migration test

Questo rende il progetto piu' piccolo e leggibile per studio, ma la struttura e' pronta per essere estesa.

## Come eseguire il progetto

### Da Android Studio

1. Aprire la cartella del progetto
2. Attendere il sync Gradle
3. Avviare un emulatore o collegare un device
4. Eseguire il modulo `app`

### Da terminale

Build debug:

```bash
./gradlew assembleDebug
```

Eseguire i test:

```bash
./gradlew test
```

## Come estenderlo

Possibili evoluzioni consigliate:

- collegare la schermata TODO principale all'esempio Room gia' presente
- sostituire il mock HTTP di `TeachingMockInterceptor` con una API reale
- aggiungere test di integrazione per `RoomTodoDao` con database in-memory
- aggiungere error state nel contratto UI della Home
- introdurre navigazione Compose tra piu' schermate
- estrarre mapper dedicati se i modelli tecnici crescono

## Guida rapida ai file piu' importanti

### Core app

- `MainActivity.kt`: entry point Android e wiring delle feature nella UI
- `TaskFlowApplication.kt`: bootstrap di Hilt
- `NetworkModule.kt`: costruzione di OkHttp, Retrofit e `HomeApiService`

### Feature TODO

- `TodoContract.kt`: contratto UI della feature TODO
- `TodoViewModel.kt`: stato e orchestrazione della schermata TODO
- `TodoScreen.kt`: UI Compose della feature TODO
- `FetchTodoListsUseCase.kt`: esempio di fetch `suspend` one-shot alternativo al `Flow`
- `AddTodoUseCase.kt`: regola di validazione del titolo
- `ToggleTodoUseCase.kt`: logica di toggle
- `TodoRepository.kt`: contratto del dominio
- `TodoRepositoryImpl.kt`: implementazione concreta del repository
- `InMemoryTodoDataSource.kt`: fake DB locale
- `TodoFeatureModule.kt`: dependency injection manuale della feature

### Feature Home

- `HomeContract.kt`: contratto UI della feature Home
- `HomeViewModel.kt`: stato e orchestrazione della Home
- `HomeScreen.kt`: UI Compose della Home
- `ObserveHomeInfoUseCase.kt`: osservazione dei dati Home
- `RefreshHomeInfoUseCase.kt`: refresh dei dati Home
- `HomeRepository.kt`: contratto del dominio
- `HomeRepositoryImpl.kt`: coordinamento local + remote
- `HomeLocalDataSource.kt`: cache locale
- `HomeApiService.kt`: contratto Retrofit della feature
- `HomeRemoteDataSource.kt`: adapter remoto basato su Retrofit
- `TeachingMockInterceptor.kt`: risposta HTTP mockata per l'esempio
- `HomeFeatureModule.kt`: binding Hilt del repository

### Room Example

- `RoomTodoDao.kt`: esempio di DAO con `observeAll()`
- `RoomTodoDatabase.kt`: database Room che espone il DAO
- `RoomTodoEntity.kt`: entity di persistenza
- `RoomTodoExampleRepositoryImpl.kt`: adapter tra DAO e domain
- `ObserveAllRoomTodosUseCase.kt`: use case minimale che espone il `Flow`
- `RoomTodoExampleModule.kt`: wiring Hilt del database e del repository

## Messaggio finale

Questo progetto vuole essere un esempio semplice ma architetturalmente corretto.

Non cerca di mostrare "tutte" le librerie possibili, ma di far vedere bene le responsabilita' corrette:

- la UI mostra stato e invia eventi
- il ViewModel orchestra
- i use case contengono le regole del dominio
- il repository adatta e coordina le sorgenti dati
- il data layer gestisce i dettagli tecnici
- il domain resta pulito e indipendente

Se stai studiando Clean Architecture in Android, questo repository puo' essere usato come base per esercizi, refactor guidati, aggiunta di test o sostituzione graduale dei componenti fake con infrastrutture reali.
