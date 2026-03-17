# TaskFlow2

## Indice

- [Quick Start](#quick-start)
- [Obiettivo Del Progetto](#obiettivo-del-progetto)
- [Stack Tecnologico](#stack-tecnologico)
- [Requisiti Tecnici](#requisiti-tecnici)
- [Dipendenze Principali](#dipendenze-principali)
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
