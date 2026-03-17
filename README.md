# TaskFlow2

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

- usa un `InMemoryTodoDataSource` invece di Room per mantenere chiaro il focus architetturale
- usa una feature `todo` con dependency injection manuale per far vedere chiaramente il wiring "a mano"
- usa una feature `home` con Hilt per mostrare la stessa architettura con una DI moderna
- usa Retrofit davvero, ma con un `Interceptor` mockato per mostrare il flusso `remote -> local -> domain -> UI` senza richiedere un backend reale
- usa commenti in italiano e CRC card per spiegare responsabilita' e collaborazioni

## Cosa NON fa il progetto

Per scelta, questo esempio non include:

- Room
- navigazione multi-screen
- persistenza reale su disco
- una vera API remota pubblica o un backend dedicato
- gestione errori di rete avanzata
- test unitari dedicati ai use case

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

- sostituire `InMemoryTodoDataSource` con Room
- sostituire il mock HTTP di `TeachingMockInterceptor` con una API reale
- aggiungere test unitari per `AddTodoUseCase` e `ToggleTodoUseCase`
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
