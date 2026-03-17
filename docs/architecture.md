# Architettura

## Obiettivo del Progetto

Questa repo non vuole essere solo una demo UI. Vuole mostrare, in modo leggibile:

- separazione tra UI, domain e data
- inversion of dependency
- use case con responsabilita' chiare
- repository come adapter, non come contenitore generico di logica
- flusso dati reattivo con `StateFlow`
- organizzazione per feature invece che per package globali

## Struttura ad Alto Livello

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

## Layer Per Feature

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

## Principi Applicati

### Dependency Rule

Le dipendenze puntano verso l'interno:

- `ui` dipende da `presentation`
- `presentation` dipende da `domain`
- `data` dipende da `domain`
- `domain` non dipende da Android, Compose o dettagli tecnici

### Inversion of Dependency

Il domain dipende da contratti astratti, non da implementazioni concrete.

Esempio:

- [`TodoRepository`](../app/src/main/java/com/example/taskflow2/feature_todo/domain/repository/TodoRepository.kt)
- [`TodoRepositoryImpl`](../app/src/main/java/com/example/taskflow2/feature_todo/data/repository/TodoRepositoryImpl.kt)

### Repository != Data Source

- `DataSource`: legge/scrive da una fonte concreta
- `Repository`: coordina le fonti e offre un contratto al domain
- `UseCase`: esprime un'azione del dominio

### UDF

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

## Feature Reali

### Feature TODO

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

### Feature Home

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

## Samples e Codice App

Per evitare che il codice principale diventi troppo “da manuale”, la repo separa:

### Codice app

- `feature_todo/`
- `feature_home/`
- `core/`
- `ui/theme/`

### Codice dimostrativo

- [`samples/hilt/`](../app/src/main/java/com/example/taskflow2/samples/hilt/)
- [`samples/coroutines/`](../app/src/main/java/com/example/taskflow2/samples/coroutines/)
- [`feature_todo_room_example/`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/)

Questa scelta aiuta molto chi studia:

- prima capisce il flusso reale dell'app
- poi isola i concetti didattici senza mischiarli troppo

## Perche' Feature Packages e non Package Globali

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

## Entry Point Android

- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
- [`TaskFlowApplication.kt`](../app/src/main/java/com/example/taskflow2/TaskFlowApplication.kt)

`MainActivity` fa da composition root Android. Non contiene business logic: collega ViewModel e schermate.  
`TaskFlowApplication` inizializza Hilt a livello application.

## Scelte Didattiche Intenzionali

- TODO usa DI manuale
- Home usa Hilt
- Room vive in un esempio separato
- alcuni helper puramente esplicativi stanno in `samples/`
- i test sono usati come parte della spiegazione architetturale
