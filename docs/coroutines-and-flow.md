# Coroutine e Flow

## Coroutine in una Frase

Una coroutine e' unita' di lavoro leggera che puo' sospendersi e riprendere senza bloccare il thread.

Serve per:

- lavoro asincrono leggibile
- evitare callback annidate
- tenere libero il main thread

## Coroutine vs Thread

- thread = risorsa del sistema operativo
- coroutine = task logico gestito da Kotlin

Una coroutine non crea automaticamente un nuovo thread.  
Il dispatcher decide dove farla girare.

Se arrivi da Java:

- Java classico ragiona spesso con `Thread`, `ExecutorService`, `Future`
- Kotlin ragiona piu' spesso con coroutine + dispatcher

## Dispatcher

### `Dispatchers.Main`

Per lavoro vicino alla UI.

### `Dispatchers.IO`

Per I/O bloccante:

- file
- database
- rete

Confronto mentale con Java:

- Java: sposto il lavoro su executor/thread dedicato
- Kotlin: resto nella coroutine e uso `withContext(Dispatchers.IO)` o `flowOn(Dispatchers.IO)`

### `Dispatchers.Default`

Per lavoro CPU-bound:

- trasformazioni pesanti
- parsing
- calcoli

## `launch`, `async`, `withContext`

### `launch`

Avvia lavoro asincrono senza restituire un valore diretto.  
Restituisce un `Job`.

Utile per:

- reagire a eventi UI
- fire-and-forget controllato dentro `viewModelScope`

### `async`

Avvia lavoro asincrono che produce un risultato.  
Restituisce `Deferred<T>`.

### `withContext`

Cambia contesto/dispatcher dentro la funzione corrente e restituisce un risultato.

Regola pratica:

- `launch` = avvia un lavoro
- `async` = avvia un lavoro che produce un valore
- `withContext` = cambio dispatcher dentro il flusso corrente

## Job e Cancellazione

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

## Error Handling

### `try/catch`

Da usare quando vuoi gestire localmente l'errore e magari trasformarlo in stato UI.

### `CoroutineExceptionHandler`

Utile soprattutto per:

- logging
- telemetry
- fallback globale

### `supervisorScope`

Serve quando figli diversi devono poter fallire in modo indipendente.

Esempio nel progetto:

- [`samples/coroutines/CoroutineErrorHandlingExamples.kt`](../app/src/main/java/com/example/taskflow2/samples/coroutines/CoroutineErrorHandlingExamples.kt)
- [`CoroutineErrorHandlingExamplesTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineErrorHandlingExamplesTest.kt)

## Flow

`Flow` rappresenta valori emessi nel tempo.

Confronto importante con Java:

- Java `Stream` lavora soprattutto su collection gia' disponibili
- Kotlin `Flow` modella valori che arrivano nel tempo, anche in modo asincrono

Quindi `Flow` assomiglia di piu' a:

- callback/listener
- publisher reattivi
- RxJava `Observable` o `Flowable`

## Cold Flow vs Hot Flow

### Cold Flow

- parte quando fai `collect`
- puo' rieseguire l'upstream per ogni collector

Analogia:

- un barattolo chiuso che apri quando ti serve

### Hot Flow

- esiste indipendentemente dal singolo collector
- puo' emettere anche se nessuno ascolta

Analogia:

- un barattolo gia' aperto sul tavolo

### `SharedFlow`

Hot flow per eventi condivisi.

Buono per:

- eventi one-shot
- broadcast

### `StateFlow`

Hot flow per stato corrente.

Caratteristiche:

- ha sempre un valore attuale
- i nuovi collector ricevono subito l'ultimo stato

### `MutableStateFlow`

Versione mutabile usata di solito dentro producer e ViewModel.

Pattern tipico:

```kotlin
private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState
```

## Operator Più Importanti

### Builder e terminal operator

- `flowOf`
- `asFlow`
- `collect`

### Trasformazione e filtro

- `map`
- `filter`
- `transform`
- `take`

### Side effect e gestione lifecycle

- `onEach`
- `catch`
- `onCompletion`

## `flowOn` e Dispatcher

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

## Esempi nel Progetto

### Codice app

- [`HomeRefreshAuditLogger.kt`](../app/src/main/java/com/example/taskflow2/feature_home/data/local/HomeRefreshAuditLogger.kt)
- [`HomeRepositoryImpl.kt`](../app/src/main/java/com/example/taskflow2/feature_home/data/repository/HomeRepositoryImpl.kt)
- [`InMemoryTodoDataSource.kt`](../app/src/main/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSource.kt)

### Test didattici

- [`CoroutineTimeControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
- [`CoroutineJobControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineJobControlTest.kt)
- [`FlowBuildersTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt)
- [`FlowOperatorsTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt)
- [`FlowTemperatureTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowTemperatureTest.kt)
- [`FlowDispatcherTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowDispatcherTest.kt)
