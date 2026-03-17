# Guida Completa

Questa pagina raccoglie in un solo file tutta la documentazione principale della repo.

## Indice

- [Getting Started](#getting-started)
- [Architettura](#architettura)
- [Coroutine e Flow](#coroutine-e-flow)
- [Hilt e Dependency Injection](#hilt-e-dependency-injection)
- [Room e DAO Pattern](#room-e-dao-pattern)
- [Testing Guide](#testing-guide)

## Getting Started

### Requisiti

- Android Studio installato
- JDK/JBR fornito da Android Studio
- SDK Android coerente con il progetto

Valori tecnici correnti:

- `minSdk = 24`
- `targetSdk = 36`
- `compileSdk = 36`
- Java/Kotlin target `11`

### Aprire il progetto

#### Android Studio

1. Apri la cartella root `TaskFlow2`
2. Attendi il sync Gradle
3. Se richiesto, installa SDK o Build Tools mancanti
4. Avvia un emulatore o collega un device
5. Esegui il modulo `app`

#### Terminale

Build debug:

```bash
./gradlew assembleDebug
```

Test unitari:

```bash
./gradlew test
```

APK generato:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Cosa vedrai all'avvio

- schermata TODO principale
- header Home integrato nella parte alta
- feature TODO con lista e inserimento task
- feature Home con dati locali/remoti mockati in modo didattico

### Da Dove Iniziare a Leggere

Ordine consigliato:

1. [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
2. [`feature_todo/`](../app/src/main/java/com/example/taskflow2/feature_todo/)
3. [`feature_home/`](../app/src/main/java/com/example/taskflow2/feature_home/)
4. [`samples/`](../app/src/main/java/com/example/taskflow2/samples/)
5. [`app/src/test/java/com/example/taskflow2/`](../app/src/test/java/com/example/taskflow2/)

### Note Utili

- `local.properties` e' locale e non va condiviso
- `build/`, `.gradle/` e file generati non fanno parte del codice sorgente da studiare
- i test sono parte integrante della documentazione: spesso spiegano i concetti meglio di una sola pagina teorica

## Architettura

### Obiettivo del Progetto

Questa repo non vuole essere solo una demo UI. Vuole mostrare, in modo leggibile:

- separazione tra UI, domain e data
- inversion of dependency
- use case con responsabilita' chiare
- repository come adapter, non come contenitore generico di logica
- flusso dati reattivo con `StateFlow`
- organizzazione per feature invece che per package globali

### Struttura ad Alto Livello

```text
app/src/main/java/com/example/taskflow2/
  MainActivity.kt
  TaskFlowApplication.kt
  core/
    di/
    network/
  samples/
    coroutines/
    hilt/
  feature_todo/
  feature_home/
  feature_todo_room_example/
  ui/theme/
```

### Layer Per Feature

Ogni feature segue una struttura simile:

```text
feature_x/
  data/
    local/
    remote/      (se serve)
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
```

### Principi Applicati

#### Dependency Rule

Le dipendenze puntano verso l'interno:

- `ui` dipende da `presentation`
- `presentation` dipende da `domain`
- `data` dipende da `domain`
- `domain` non dipende da Android, Compose o dettagli tecnici

#### Inversion of Dependency

Il domain dipende da contratti astratti, non da implementazioni concrete.

Esempio:

- [`TodoRepository`](../app/src/main/java/com/example/taskflow2/feature_todo/domain/repository/TodoRepository.kt)
- [`TodoRepositoryImpl`](../app/src/main/java/com/example/taskflow2/feature_todo/data/repository/TodoRepositoryImpl.kt)

#### Repository != Data Source

- `DataSource`: legge/scrive da una fonte concreta
- `Repository`: coordina le fonti e offre un contratto al domain
- `UseCase`: esprime un'azione del dominio

#### UDF

Flusso tipico:

```text
UI -> UiEvent -> ViewModel -> UseCase -> Repository -> DataSource
                                          |
                                          v
                                     risultato
                                          |
                                          v
UI <- UiState <- StateFlow <- ViewModel <- Flow
```

### Feature Reali

#### Feature TODO

Cartella:

- [`feature_todo/`](../app/src/main/java/com/example/taskflow2/feature_todo/)

Mostra:

- TODO list
- input + add
- toggle completed
- DI manuale per confronto didattico

File chiave:

- [`TodoViewModel.kt`](../app/src/main/java/com/example/taskflow2/feature_todo/presentation/TodoViewModel.kt)
- [`TodoRepository.kt`](../app/src/main/java/com/example/taskflow2/feature_todo/domain/repository/TodoRepository.kt)
- [`InMemoryTodoDataSource.kt`](../app/src/main/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSource.kt)

#### Feature Home

Cartella:

- [`feature_home/`](../app/src/main/java/com/example/taskflow2/feature_home/)

Mostra:

- Hilt
- Retrofit
- local + remote + repository
- refresh e stato UI

File chiave:

- [`HomeViewModel.kt`](../app/src/main/java/com/example/taskflow2/feature_home/presentation/HomeViewModel.kt)
- [`HomeRepositoryImpl.kt`](../app/src/main/java/com/example/taskflow2/feature_home/data/repository/HomeRepositoryImpl.kt)
- [`NetworkModule.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt)

### Samples e Codice App

Per evitare che il codice principale diventi troppo “da manuale”, la repo separa:

#### Codice app

- `feature_todo/`
- `feature_home/`
- `core/`
- `ui/theme/`

#### Codice dimostrativo

- [`samples/hilt/`](../app/src/main/java/com/example/taskflow2/samples/hilt/)
- [`samples/coroutines/`](../app/src/main/java/com/example/taskflow2/samples/coroutines/)
- [`feature_todo_room_example/`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/)

Questa scelta aiuta molto chi studia:

- prima capisce il flusso reale dell'app
- poi isola i concetti didattici senza mischiarli troppo

### Perche' Feature Packages e non Package Globali

Invece di:

```text
presentation/viewmodel/
data/repository/
domain/usecase/
```

la repo usa:

```text
feature_todo/...
feature_home/...
```

Vantaggi:

- ogni feature e' una vertical slice
- il codice si naviga piu' facilmente
- i file collegati tra loro stanno vicini
- aggiungere nuove feature e' piu' semplice

### Entry Point Android

- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
- [`TaskFlowApplication.kt`](../app/src/main/java/com/example/taskflow2/TaskFlowApplication.kt)

`MainActivity` fa da composition root Android. Non contiene business logic: collega ViewModel e schermate.  
`TaskFlowApplication` inizializza Hilt a livello application.

### Scelte Didattiche Intenzionali

- TODO usa DI manuale
- Home usa Hilt
- Room vive in un esempio separato
- alcuni helper puramente esplicativi stanno in `samples/`
- i test sono usati come parte della spiegazione architetturale

## Coroutine e Flow

### Coroutine in una Frase

Una coroutine e' unita' di lavoro leggera che puo' sospendersi e riprendere senza bloccare il thread.

Serve per:

- lavoro asincrono leggibile
- evitare callback annidate
- tenere libero il main thread

### Coroutine vs Thread

- thread = risorsa del sistema operativo
- coroutine = task logico gestito da Kotlin

Una coroutine non crea automaticamente un nuovo thread.  
Il dispatcher decide dove farla girare.

Se arrivi da Java:

- Java classico ragiona spesso con `Thread`, `ExecutorService`, `Future`
- Kotlin ragiona piu' spesso con coroutine + dispatcher

### Dispatcher

#### `Dispatchers.Main`

Per lavoro vicino alla UI.

#### `Dispatchers.IO`

Per I/O bloccante:

- file
- database
- rete

Confronto mentale con Java:

- Java: sposto il lavoro su executor/thread dedicato
- Kotlin: resto nella coroutine e uso `withContext(Dispatchers.IO)` o `flowOn(Dispatchers.IO)`

#### `Dispatchers.Default`

Per lavoro CPU-bound:

- trasformazioni pesanti
- parsing
- calcoli

### `launch`, `async`, `withContext`

#### `launch`

Avvia lavoro asincrono senza restituire un valore diretto.  
Restituisce un `Job`.

Utile per:

- reagire a eventi UI
- fire-and-forget controllato dentro `viewModelScope`

#### `async`

Avvia lavoro asincrono che produce un risultato.  
Restituisce `Deferred<T>`.

#### `withContext`

Cambia contesto/dispatcher dentro la funzione corrente e restituisce un risultato.

Regola pratica:

- `launch` = avvia un lavoro
- `async` = avvia un lavoro che produce un valore
- `withContext` = cambio dispatcher dentro il flusso corrente

### Job e Cancellazione

Un `Job` ti permette di:

- osservare lo stato della coroutine
- cancellarla
- aspettarne la fine

Concetti chiave:

- `cancel()`
- `join()`
- `cancelAndJoin()`
- `isActive`
- `isCompleted`
- `isCancelled`

La cancellazione nelle coroutine e' cooperativa: il codice deve collaborare.

### Error Handling

#### `try/catch`

Da usare quando vuoi gestire localmente l'errore e magari trasformarlo in stato UI.

#### `CoroutineExceptionHandler`

Utile soprattutto per:

- logging
- telemetry
- fallback globale

#### `supervisorScope`

Serve quando figli diversi devono poter fallire in modo indipendente.

Esempio nel progetto:

- [`samples/coroutines/CoroutineErrorHandlingExamples.kt`](../app/src/main/java/com/example/taskflow2/samples/coroutines/CoroutineErrorHandlingExamples.kt)
- [`CoroutineErrorHandlingExamplesTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineErrorHandlingExamplesTest.kt)

### Flow

`Flow` rappresenta valori emessi nel tempo.

Confronto importante con Java:

- Java `Stream` lavora soprattutto su collection gia' disponibili
- Kotlin `Flow` modella valori che arrivano nel tempo, anche in modo asincrono

Quindi `Flow` assomiglia di piu' a:

- callback/listener
- publisher reattivi
- RxJava `Observable` o `Flowable`

### Cold Flow vs Hot Flow

#### Cold Flow

- parte quando fai `collect`
- puo' rieseguire l'upstream per ogni collector

Analogia:

- un barattolo chiuso che apri quando ti serve

#### Hot Flow

- esiste indipendentemente dal singolo collector
- puo' emettere anche se nessuno ascolta

Analogia:

- un barattolo gia' aperto sul tavolo

#### `SharedFlow`

Hot flow per eventi condivisi.

Buono per:

- eventi one-shot
- broadcast

#### `StateFlow`

Hot flow per stato corrente.

Caratteristiche:

- ha sempre un valore attuale
- i nuovi collector ricevono subito l'ultimo stato

#### `MutableStateFlow`

Versione mutabile usata di solito dentro producer e ViewModel.

Pattern tipico:

```kotlin
private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState
```

### Operator Più Importanti

#### Builder e terminal operator

- `flowOf`
- `asFlow`
- `collect`

#### Trasformazione e filtro

- `map`
- `filter`
- `transform`
- `take`

#### Side effect e gestione lifecycle

- `onEach`
- `catch`
- `onCompletion`

### `flowOn` e Dispatcher

`flowOn(...)` cambia il dispatcher dell'upstream del `Flow`.

Punto chiave:

- sposta cio' che sta prima
- non sposta automaticamente collector e downstream

Esempio mentale:

- repository costruisce `flow { ... }`
- upstream fa I/O
- il repository usa `flowOn(Dispatchers.IO)`

Regola pratica:

- `suspend + withContext(IO)` per operazione one-shot
- `Flow + flowOn(IO)` quando il repository costruisce una pipeline `Flow` e vuoi spostarne l'upstream

Nota su Room:

- se il `Flow` arriva gia' da Room, non aggiungere `flowOn(IO)` “per riflesso”

### Esempi nel Progetto

#### Codice app

- [`HomeRefreshAuditLogger.kt`](../app/src/main/java/com/example/taskflow2/feature_home/data/local/HomeRefreshAuditLogger.kt)
- [`HomeRepositoryImpl.kt`](../app/src/main/java/com/example/taskflow2/feature_home/data/repository/HomeRepositoryImpl.kt)
- [`InMemoryTodoDataSource.kt`](../app/src/main/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSource.kt)

#### Test didattici

- [`CoroutineTimeControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
- [`CoroutineJobControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineJobControlTest.kt)
- [`FlowBuildersTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt)
- [`FlowOperatorsTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt)
- [`FlowTemperatureTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowTemperatureTest.kt)
- [`FlowDispatcherTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowDispatcherTest.kt)

## Hilt e Dependency Injection

### Perche' MVVM + DI Funzionano Bene Insieme

MVVM e DI si rafforzano a vicenda:

- il ViewModel resta focalizzato su stato ed eventi
- la business logic vive in use case e repository
- le dipendenze concrete si sostituiscono facilmente nei test
- il wiring resta fuori dalla UI

Nel progetto questo si vede bene in:

- [`feature_todo/`](../app/src/main/java/com/example/taskflow2/feature_todo/)
- [`feature_home/`](../app/src/main/java/com/example/taskflow2/feature_home/)
- [`app/src/test/java/com/example/taskflow2/`](../app/src/test/java/com/example/taskflow2/)

### `@Inject`, `@Binds`, `@Provides`

#### `@Inject`

Serve a dire a Hilt come costruire una classe tramite costruttore.

#### `@Binds`

Usalo per collegare:

- interfaccia -> implementazione

quando l'implementazione ha gia' un costruttore `@Inject`.

Esempio:

- [`HomeFeatureModule.kt`](../app/src/main/java/com/example/taskflow2/feature_home/di/HomeFeatureModule.kt)

#### `@Provides`

Usalo quando un oggetto va costruito manualmente:

- Retrofit
- OkHttp
- builder/factory esterni
- Room database

Esempi:

- [`NetworkModule.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt)
- [`RoomTodoExampleModule.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/di/RoomTodoExampleModule.kt)

### Scope

#### `@Singleton`

Vive per il ciclo di vita del processo app.

Usalo per:

- Retrofit
- OkHttp
- repository condivisi
- helper application-wide

#### `@ActivityScoped`

Vive quanto una singola Activity.

Usalo quando una dipendenza usa `@ActivityContext` o appartiene davvero alla schermata.

Esempio:

- [`samples/hilt/ActivitySessionTracker.kt`](../app/src/main/java/com/example/taskflow2/samples/hilt/ActivitySessionTracker.kt)

#### `@ViewModelScoped`

Vive quanto un singolo ViewModel.

Utile per:

- stato tecnico temporaneo del ViewModel
- coordinatori che non devono diventare globali

Esempio:

- [`samples/hilt/HomeRefreshSession.kt`](../app/src/main/java/com/example/taskflow2/samples/hilt/HomeRefreshSession.kt)

### Qualifier

I qualifier servono quando hai piu' dipendenze dello stesso tipo.

#### Built-in

- `@ApplicationContext`
- `@ActivityContext`

#### Custom

- `@AppPackageName`
- `@TeachingBaseUrl`
- `@TeachingHttpClient`

File chiave:

- [`AppQualifiers.kt`](../app/src/main/java/com/example/taskflow2/core/di/AppQualifiers.kt)
- [`NetworkQualifiers.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkQualifiers.kt)
- [`samples/hilt/AppIdentityProvider.kt`](../app/src/main/java/com/example/taskflow2/samples/hilt/AppIdentityProvider.kt)

### Anti-Leak

Regola fondamentale:

- non mettere in `@Singleton` oggetti che trattengono `Activity`, `Fragment`, `View` o `@ActivityContext`

Questo evita:

- memory leak
- state leak tra schermate o ViewModel diversi

### Stato Attuale del Progetto

Il progetto usa due approcci di proposito:

- `feature_home` usa Hilt
- `feature_todo` usa DI manuale

Questa asimmetria e' intenzionale: serve a mostrare confronto tra wiring manuale e DI moderna.

### Dove Guardare nel Codice

- [`TaskFlowApplication.kt`](../app/src/main/java/com/example/taskflow2/TaskFlowApplication.kt)
- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
- [`NetworkModule.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt)
- [`HomeFeatureModule.kt`](../app/src/main/java/com/example/taskflow2/feature_home/di/HomeFeatureModule.kt)
- [`samples/hilt/`](../app/src/main/java/com/example/taskflow2/samples/hilt/)

## Room e DAO Pattern

### Perche' c'e' un Esempio Separato

La feature TODO principale resta volutamente in-memory per non caricare troppi concetti insieme.

Per mostrare Room in modo pulito, la repo include una vertical slice separata:

- [`feature_todo_room_example/`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/)

### DAO Pattern

DAO significa `Data Access Object`.

L'idea e':

- il DAO contiene l'accesso ai dati
- le query SQL stanno in un punto dedicato
- il resto dell'app non parla direttamente con il database

### Componenti dell'Esempio

- [`RoomTodoEntity.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoEntity.kt)
  Entity di persistenza
- [`RoomTodoDao.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDao.kt)
  DAO con query e `observeAll()`
- [`RoomTodoDatabase.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDatabase.kt)
  Database Room
- [`RoomTodoExampleRepository.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/domain/repository/RoomTodoExampleRepository.kt)
  Contratto del domain
- [`RoomTodoExampleRepositoryImpl.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/repository/RoomTodoExampleRepositoryImpl.kt)
  Adapter tra DAO ed entita' di dominio
- [`ObserveAllRoomTodosUseCase.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/domain/usecase/ObserveAllRoomTodosUseCase.kt)
  Use case minimale

### `observeAll()` e Flow

Nel DAO:

```kotlin
@Query("SELECT * FROM room_todo_items ORDER BY id ASC")
fun observeAll(): Flow<List<RoomTodoEntity>>
```

Perche' e' interessante:

- espone un `Flow`
- non richiede polling manuale
- quando la tabella cambia, Room riemette il risultato

Quindi il flusso mentale e':

- cambia il database
- Room invalida la query
- il `Flow` del DAO emette il nuovo valore
- repository/use case/UI possono reagire

### Repository e Room

Nel repository dell'esempio il ruolo e' chiaro:

- il DAO sa fare query SQL
- il repository mappa `Entity` -> modello di dominio
- il domain continua a dipendere da un contratto astratto

Nota didattica:

- se il repository restituisce un `Flow` gia' fornito da Room, non serve aggiungere `flowOn(IO)` “automaticamente”

### Hilt + Room

Il wiring Hilt dell'esempio e' qui:

- [`RoomTodoExampleModule.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/di/RoomTodoExampleModule.kt)

Si vede bene la distinzione:

- `@Provides` per database e DAO
- `@Binds` per repository interface -> implementation

### Nota su KSP e Kapt

Le docs Android oggi raccomandano spesso KSP per Room nei progetti Kotlin.

Questa repo usa `kapt` anche per Room per un motivo pratico:

- Hilt nel progetto usa gia' `kapt`
- per una repo didattica compatta era piu' semplice tenere un solo setup di code generation

Il pattern DAO non cambia.

## Testing Guide

### Obiettivo

In questa repo i test non servono solo a verificare il codice: servono anche a spiegare i concetti.

Per questo conviene leggerli come una seconda documentazione.

### Come Eseguirli

```bash
./gradlew test
```

### Mappa dei Test

#### MVVM + DI

- [`AddTodoUseCaseTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/AddTodoUseCaseTest.kt)
  Regola di dominio testata con fake repository
- [`TodoViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/presentation/TodoViewModelTest.kt)
  Eventi e stato UI senza schermata reale
- [`HomeViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_home/presentation/HomeViewModelTest.kt)
  Test di un `HiltViewModel` tramite constructor injection

#### Coroutine

- [`CoroutineTimeControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
  `runTest`, tempo virtuale e `delay`
- [`CoroutineJobControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineJobControlTest.kt)
  `Job`, `cancel`, `join`, stati finali
- [`CoroutineErrorHandlingExamplesTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineErrorHandlingExamplesTest.kt)
  `try/catch`, `CoroutineExceptionHandler`, `supervisorScope`

#### Flow

- [`FlowBuildersTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt)
  `flowOf`, `asFlow`, `collect`
- [`FlowOperatorsTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt)
  operatori lazy, `map`, `filter`, `transform`, `take`, `catch`, `onEach`, `onCompletion`
- [`FlowTemperatureTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowTemperatureTest.kt)
  `cold Flow`, `SharedFlow`, `MutableStateFlow`
- [`FlowDispatcherTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowDispatcherTest.kt)
  `flowOn`, upstream e collector

#### Data Source e Fetch One-Shot

- [`InMemoryTodoDataSourceTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSourceTest.kt)
  Fetch `suspend` con `delay`
- [`FetchTodoListsUseCaseTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/FetchTodoListsUseCaseTest.kt)
  Percorso `repository -> use case`

#### Utility di Test

- [`MainDispatcherRule.kt`](../app/src/test/java/com/example/taskflow2/testutil/MainDispatcherRule.kt)
  Sostituisce `Dispatchers.Main` nei test dei ViewModel

### Ordine Consigliato di Studio

Se parti da zero:

1. [`TodoViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/presentation/TodoViewModelTest.kt)
2. [`AddTodoUseCaseTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/AddTodoUseCaseTest.kt)
3. [`FlowBuildersTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt)
4. [`FlowOperatorsTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt)
5. [`CoroutineTimeControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
6. [`HomeViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_home/presentation/HomeViewModelTest.kt)

### Cosa Ancora Manca

Se vuoi estendere la repo, i prossimi test naturali sono:

- test Room con database in-memory
- eventuali test di integrazione piu' espliciti per DAO
