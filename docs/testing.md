# Testing Guide

## Obiettivo

In questa repo i test non servono solo a verificare il codice: servono anche a spiegare i concetti.

Per questo conviene leggerli come una seconda documentazione.

## Come Eseguirli

```bash
./gradlew test
```

## Mappa dei Test

### MVVM + DI

- [`AddTodoUseCaseTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/AddTodoUseCaseTest.kt)
  Regola di dominio testata con fake repository
- [`TodoViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/presentation/TodoViewModelTest.kt)
  Eventi e stato UI senza schermata reale
- [`HomeViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_home/presentation/HomeViewModelTest.kt)
  Test di un `HiltViewModel` tramite constructor injection

### Coroutine

- [`CoroutineTimeControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
  `runTest`, tempo virtuale e `delay`
- [`CoroutineJobControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineJobControlTest.kt)
  `Job`, `cancel`, `join`, stati finali
- [`CoroutineErrorHandlingExamplesTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineErrorHandlingExamplesTest.kt)
  `try/catch`, `CoroutineExceptionHandler`, `supervisorScope`

### Flow

- [`FlowBuildersTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt)
  `flowOf`, `asFlow`, `collect`
- [`FlowOperatorsTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt)
  operatori lazy, `map`, `filter`, `transform`, `take`, `catch`, `onEach`, `onCompletion`
- [`FlowTemperatureTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowTemperatureTest.kt)
  `cold Flow`, `SharedFlow`, `MutableStateFlow`
- [`FlowDispatcherTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowDispatcherTest.kt)
  `flowOn`, upstream e collector

### Data Source e Fetch One-Shot

- [`InMemoryTodoDataSourceTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/data/local/InMemoryTodoDataSourceTest.kt)
  Fetch `suspend` con `delay`
- [`FetchTodoListsUseCaseTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/FetchTodoListsUseCaseTest.kt)
  Percorso `repository -> use case`

### Utility di Test

- [`MainDispatcherRule.kt`](../app/src/test/java/com/example/taskflow2/testutil/MainDispatcherRule.kt)
  Sostituisce `Dispatchers.Main` nei test dei ViewModel

## Ordine Consigliato di Studio

Se parti da zero:

1. [`TodoViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/presentation/TodoViewModelTest.kt)
2. [`AddTodoUseCaseTest.kt`](../app/src/test/java/com/example/taskflow2/feature_todo/domain/usecase/AddTodoUseCaseTest.kt)
3. [`FlowBuildersTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowBuildersTest.kt)
4. [`FlowOperatorsTest.kt`](../app/src/test/java/com/example/taskflow2/flow/FlowOperatorsTest.kt)
5. [`CoroutineTimeControlTest.kt`](../app/src/test/java/com/example/taskflow2/coroutines/CoroutineTimeControlTest.kt)
6. [`HomeViewModelTest.kt`](../app/src/test/java/com/example/taskflow2/feature_home/presentation/HomeViewModelTest.kt)

## Cosa Ancora Manca

Se vuoi estendere la repo, i prossimi test naturali sono:

- test Room con database in-memory
- eventuali test di integrazione piu' espliciti per DAO
