# Estendere l'App

Questa guida spiega come aggiungere una nuova pagina custom all'app usando l'impostazione attuale con `Navigation Compose`.

## Esempio Gia' Presente

Nel progetto trovi gia' un flusso minimale:

1. Home
2. lista di pulsanti o destinazioni custom
3. click su una voce come `Matteo` o `Esempio`
4. apertura della schermata `Esempio` con solo `helloworld`

File di riferimento:

- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
- [`AppNavigation.kt`](../app/src/main/java/com/example/taskflow2/navigation/AppNavigation.kt)
- [`HomeScreen.kt`](../app/src/main/java/com/example/taskflow2/feature_home/ui/HomeScreen.kt)
- [`ExampleScreen.kt`](../app/src/main/java/com/example/taskflow2/feature_example/ui/ExampleScreen.kt)

## Come Aggiungere una Schermata

Con la struttura attuale il flusso consigliato e' questo:

1. crea la schermata Compose nella feature, per esempio `feature_profilo/ui/ProfiloScreen.kt`
2. aggiungi una nuova entry nella sealed class `Screen` in [`AppNavigation.kt`](../app/src/main/java/com/example/taskflow2/navigation/AppNavigation.kt)
3. registra la route nel `NavHost`
4. aggiungi una callback semplice alla schermata che deve aprirla, per esempio `onOpenProfile`
5. in `AppNavigation` collega quella callback a `navController.navigate(...)`

L'idea chiave e' questa:

- la schermata nuova vive nella sua feature
- la home espone solo callback semplici come `onOpenProfile`
- `AppNavigation` decide quale route aprire
- il back di sistema torna indietro da solo grazie a Navigation Compose

## Dove Mettere Cosa

Per non confondersi, usa questa regola pratica:

- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt): avvia tema e graph
- [`AppNavigation.kt`](../app/src/main/java/com/example/taskflow2/navigation/AppNavigation.kt): contiene `NavHost`, sealed class `Screen` e collegamenti di navigazione
- `feature_x/ui/Screen.kt`: contiene solo UI Compose e callback semplici
- `feature_x/presentation/`: aggiungilo solo quando la schermata ha davvero stato o logica

In altre parole:

- la navigazione sta nel punto alto dell'app
- la schermata non deve conoscere `NavController`
- la schermata riceve azioni come `onOpenProfile` o `onBack`

Questa separazione e' una best practice utile perche':

- riduce l'accoppiamento tra UI e navigation
- rende la schermata piu' semplice da leggere
- rende piu' facile spostare o riusare la UI

## Esempio Completo: Aggiungere `ProfileScreen`

Qui sotto trovi un esempio pratico completo.

### 1. Crea il file della schermata

Percorso consigliato:

```text
app/src/main/java/com/example/taskflow2/feature_profile/ui/ProfileScreen.kt
```

Esempio minimale:

```kotlin
package com.example.taskflow2.feature_profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Indietro")
        }

        Text("Profilo custom")
    }
}
```

Se la schermata e' solo dimostrativa, puoi fermarti qui.

Importante:

- se la schermata e' semplice, evita di creare subito `ViewModel`, `Contract`, `UseCase` o `Repository`
- inizia dalla UI minima e fai crescere la feature solo quando serve

### 2. Aggiungi la schermata nella sealed class `Screen`

Esempio di nuova entry:

```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Example : Screen("example")
    data object Profile : Screen("profile")
}
```

Regola pratica:

- usa un nome corto e stabile
- evita spazi o testi UI come route
- tieni tutte le route nello stesso punto del file

La route resta un identificatore tecnico, non una stringa mostrata all'utente.

### 3. Registra la schermata dentro `NavHost`

Esempio di registrazione nel `NavHost`:

```kotlin
composable(route = Screen.Profile.route) {
    ProfileScreen(
        onBack = navController::navigateUp
    )
}
```

Qui succedono due cose importanti:

- `composable(...)` dice a Navigation Compose quale schermata mostrare per quella route
- `navController::navigateUp` usa il back stack reale e torna alla schermata precedente

### 4. Espandi la schermata che deve aprirla

Se vuoi aprire la nuova schermata dalla home, di solito aggiungi una callback a [`HomeScreen.kt`](../app/src/main/java/com/example/taskflow2/feature_home/ui/HomeScreen.kt).

Esempio:

```kotlin
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onOpenExample: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
)
```

Poi dentro la UI aggiungi il bottone:

```kotlin
Button(
    onClick = onOpenProfile,
    modifier = Modifier.fillMaxWidth()
) {
    Text("Profilo")
}
```

Best practice:

- la schermata espone callback con nomi chiari come `onOpenProfile`
- evita callback generiche come `onClickOne`, `onNext`, `onAction`
- il nome della callback dovrebbe spiegare l'intento, non il dettaglio tecnico

### 5. Collega la callback alla navigazione reale

Esempio di collegamento UI -> navigazione:

```kotlin
HomeScreen(
    uiState = homeUiState,
    onEvent = homeViewModel::onEvent,
    onOpenProfile = {
        navController.navigate(Screen.Profile.route) {
            launchSingleTop = true
        }
    },
    onOpenExample = {
        navController.navigate(Screen.Example.route) {
            launchSingleTop = true
        }
    }
)
```

Questo passaggio e' importante per tenere pulita la UI:

- `HomeScreen` non conosce `NavController`
- `HomeScreen` non conosce la route `"profile"`
- la decisione di navigazione resta tutta in `AppNavigation`

Questa e' una delle best practice piu' utili da ricordare:

- UI: emette eventi
- composition root: decide come rispondere agli eventi

Se metti `navController.navigate(...)` direttamente dentro tante schermate,
tra qualche file diventa piu' difficile capire e cambiare il flusso.

### 6. Verifica il flusso

Quando hai finito, controlla questo:

1. dalla home vedi il nuovo pulsante
2. al click si apre la nuova schermata
3. il tasto indietro della UI funziona, se lo hai aggiunto
4. il back di sistema funziona
5. se clicchi piu' volte velocemente non si accumulano duplicati inutili grazie a `launchSingleTop`

## Best Practice di Navigazione

- tieni le route centralizzate in un solo punto
- evita stringhe hardcoded sparse nelle UI
- passa callback di navigazione alle schermate invece del `NavController`
- se usi una sealed class `Screen`, tieni sealed class e `NavHost` vicini nello stesso file di navigation
- per esempi piccoli preferisci callback dirette e leggibili invece di helper non necessari
- crea i ViewModel nella destinazione che li usa, non troppo in alto senza motivo
- usa `collectAsStateWithLifecycle()` per osservare stato UI in modo sicuro rispetto al lifecycle
- usa `launchSingleTop` per evitare duplicati della stessa pagina
- usa `navigateUp()` o il back di sistema per tornare alla schermata precedente

## ViewModel: Best Practice

Nel progetto attuale i ViewModel vengono creati dentro la destinazione che li usa, per esempio nel blocco:

```kotlin
composable(route = Screen.Home.route) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val todoViewModel: TodoViewModel = viewModel(factory = todoViewModelFactory)
}
```

Questa e' una best practice perche':

- il ViewModel resta vicino alla schermata che lo usa
- il suo scope segue la navigation entry corretta
- eviti di creare ViewModel non necessari quando una schermata non e' aperta
- il flusso resta piu' leggibile per chi studia

### Perche' non metterli tutti direttamente in `AppNavigation`

Anche se la navigation vive in `AppNavigation`, in generale non conviene creare li' tutti i ViewModel dell'app in anticipo.

Rischi principali:

- allarghi inutilmente lo scope di ViewModel che servono a una sola schermata
- rendi meno chiaro quale ViewModel appartiene a quale destinazione
- il file di navigation rischia di diventare un contenitore troppo pesante
- chi legge deve capire insieme routing, stato UI e creazione di tutte le feature

Regola pratica:

- crea il ViewModel dentro la `composable(...)` che lo usa
- passa alla UI solo `uiState` e callback/eventi
- evita di passare il ViewModel in giro piu' del necessario

### `uiState` e `collectAsStateWithLifecycle()`

Nel progetto vedi questo pattern:

```kotlin
val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
```

Significa:

- `homeViewModel.uiState` e' uno `StateFlow`
- `collectAsStateWithLifecycle()` lo converte in uno `State<T>` leggibile da Compose
- Compose si ricompone quando cambia il valore
- la collection rispetta il lifecycle della schermata

Questa e' la forma consigliata per leggere stato UI in Compose quando il ViewModel espone `StateFlow`.

### Cosa significa `by`

La parola chiave `by` qui usa la delegation di Kotlin.

Senza delegation:

```kotlin
val homeUiStateState = homeViewModel.uiState.collectAsStateWithLifecycle()
val homeUiState = homeUiStateState.value
```

Con delegation:

```kotlin
val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
```

Il secondo modo e' piu' pulito da leggere perche':

- evita il `.value` sparso
- rende il codice UI piu' lineare
- aiuta a distinguere meglio tra `State<T>` e valore UI corrente

### Pattern Consigliato

Per una schermata con ViewModel, il flusso consigliato e' questo:

1. crea il ViewModel nella `composable(...)`
2. raccogli `uiState` con `collectAsStateWithLifecycle()`
3. passa `uiState` e callback alla schermata Compose
4. lascia la UI focalizzata sul rendering

Esempio:

```kotlin
val profileViewModel: ProfileViewModel = hiltViewModel()
val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

ProfileScreen(
    uiState = profileUiState,
    onBack = navController::navigateUp,
    onEvent = profileViewModel::onEvent
)
```

### Errori Comuni con i ViewModel

- creare ViewModel troppo in alto solo "per comodita'"
- condividere lo stesso ViewModel tra schermate senza un motivo reale
- passare direttamente il ViewModel a molti composable profondi
- leggere `StateFlow` senza `collectAsStateWithLifecycle()` nella UI Compose
- mischiare responsabilita' di navigation e stato nello stesso punto senza bisogno

### Quando usare `backStackEntry`

Nel caso semplice del progetto attuale non serve.

Di default puoi scrivere:

```kotlin
composable(route = Screen.Profile.route) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = profileUiState,
        onBack = navController::navigateUp,
        onEvent = profileViewModel::onEvent
    )
}
```

`backStackEntry` diventa utile soprattutto in questi casi:

- devi leggere argomenti della route
- vuoi ottenere un ViewModel legato a una entry specifica
- vuoi condividere un ViewModel tra piu' schermate dello stesso graph

Forma tipica:

```kotlin
composable(route = "profile/{id}") { backStackEntry ->
    val id = backStackEntry.arguments?.getString("id")
}
```

Oppure, in casi piu' avanzati, per uno scope condiviso:

```kotlin
val parentEntry = remember(backStackEntry) {
    navController.getBackStackEntry("profile")
}
val profileViewModel: ProfileViewModel = hiltViewModel(parentEntry)
```

Regola pratica:

- se non ti servono argomenti o scope condivisi, evita `backStackEntry`
- usalo solo quando aggiunge valore reale
- per chi studia, e' meglio partire senza e introdurlo dopo

### Perche' non mettere la logica nel `NavHost`

Il `NavHost` dovrebbe restare soprattutto un punto di wiring:

- decide quale schermata mostrare
- collega callback di navigazione
- crea il ViewModel giusto per la destinazione

Non dovrebbe diventare il posto in cui:

- fai business logic
- trasformi dati complessi
- mantieni stato UI condiviso in modo improvvisato
- coordini manualmente troppe feature tra loro

Se il `NavHost` inizia a fare troppo, spesso il segnale e' che una parte della
logica dovrebbe stare altrove, di solito nel ViewModel o nel repository.

### Repository + `StateFlow` per stato condiviso

Se hai bisogno di stato condiviso tra piu' schermate, spesso non serve
spostare tutto nel `NavHost`.

Una soluzione piu' pulita puo' essere:

1. mettere lo stato condiviso in un repository
2. esporlo come `Flow` o `StateFlow`
3. farlo osservare dai ViewModel delle schermate che ne hanno bisogno

Esempio concettuale:

```kotlin
class SessionRepository {
    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session

    fun updateSession(newSession: Session?) {
        _session.value = newSession
    }
}
```

Poi nel ViewModel:

```kotlin
class ProfileViewModel(
    sessionRepository: SessionRepository
) : ViewModel() {
    val uiState = sessionRepository.session
}
```

Vantaggi:

- la navigation non diventa un contenitore di stato
- piu' schermate possono osservare la stessa fonte dati
- lo stato vive in un punto piu' adatto alla logica applicativa
- il flusso resta coerente con l'architettura a feature del progetto

Regola pratica:

- usa `backStackEntry` quando ti serve scope o argomenti di navigation
- usa repository + `StateFlow` quando il problema e' stato condiviso o dati condivisi
- non usare il `NavHost` come sostituto di repository o ViewModel

## `Screen` come Sealed Class: Pro e Contro

Potresti voler modellare le schermate con una sealed class o sealed interface, come fa ora il progetto in `AppNavigation.kt`.

### Pro

- centralizzi route e informazioni della schermata in un solo posto
- riduci il rischio di typo sulle stringhe
- l'elenco delle schermate disponibili e' piu' esplicito
- puo' scalare meglio se aggiungi argomenti, label o metadati

### Contro

- per un esempio piccolo aggiunge piu' astrazione del necessario
- chi studia deve seguire un livello in piu' rispetto a due costanti locali
- se il progetto ha poche schermate il vantaggio pratico e' limitato
- introdurla troppo presto puo' complicare una demo che vuole restare lineare

Regola pratica per questo progetto:

- la sealed class `Screen` va bene perche' le schermate iniziano a essere piu' di una e vogliamo centralizzarle
- se il numero di schermate cresce molto, puo' scalare meglio delle stringhe sparse

## Quando Basta una Pagina UI

Se ti serve solo una pagina statica o un menu di navigazione:

1. crea una cartella feature, per esempio `feature_profilo/ui/`
2. aggiungi un file `ProfiloScreen.kt`
3. definisci una `@Composable` che riceve callback semplici come `onBack`
4. registra la nuova schermata nel `NavHost` in `AppNavigation`
5. aggiungi un pulsante nella schermata che deve aprirla

Esempio minimale:

```kotlin
@Composable
fun ProfiloScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilo") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("Profilo custom")
        }
    }
}
```

Questo approccio va benissimo per:

- schermate placeholder
- menu custom
- pagine informative
- prototipi iniziali

Non serve introdurre subito ViewModel, repository o use case se la pagina non ne ha bisogno.

Per una schermata di dettaglio, una best practice semplice e' aggiungere un
back button visibile in alto a sinistra, oltre al back di sistema.

Perche' e' utile:

- rende il flusso piu' chiaro a colpo d'occhio
- aiuta chi studia a distinguere tra UI di dettaglio e schermata principale
- ti permette di collegare in modo esplicito `onBack` a `navController.navigateUp()`

## Quando Ti Serve una Feature Completa

Se la nuova pagina deve avere stato, logica o dati, segui la struttura standard del progetto:

```text
feature_nuova/
  data/
  domain/
  presentation/
  ui/
  di/
```

In pratica:

1. `ui/` contiene la schermata Compose
2. `presentation/` contiene `Contract` e `ViewModel`
3. `domain/` contiene model, repository astratti e use case
4. `data/` contiene implementazioni concrete, local/remote e mapper
5. `di/` collega le dipendenze della feature

Segnale pratico che e' il momento di fare il salto:

- la schermata ha piu' di un paio di callback locali
- devi caricare dati
- devi gestire loading/error/success
- hai logica che non vuoi lasciare direttamente nel composable

## Procedura Consigliata

1. Parti da una schermata semplice in `ui/`
2. Collega la navigazione direttamente in `AppNavigation`
3. Solo se serve, aggiungi `presentation/` con `UiState` e `UiEvent`
4. Poi estrai `domain/` e `data/` quando la feature smette di essere statica
5. Aggiungi almeno una preview o un test per la nuova logica

Ordine consigliato per non complicarti la vita:

1. fai comparire la schermata vuota nel `NavHost`
2. verifica che si apra davvero
3. aggiungi il contenuto UI
4. solo dopo introduci stato e logica

Questo ordine ti aiuta a capire subito se un problema e' di navigazione o di UI.

## Errori Comuni

- mettere `navController.navigate(...)` direttamente dentro tante schermate diverse
- copiare stringhe di route in piu' file
- creare subito una feature completa anche quando serve solo una pagina statica
- aggiungere troppa astrazione per una demo piccola
- dimenticare `launchSingleTop` quando una schermata puo' essere aperta piu' volte
- creare ViewModel in alto e poi passarli ovunque anche quando una sola destinazione li usa
- mischiare testo UI e identificatori tecnici delle route

## Checklist Finale

Prima di considerare finita una nuova schermata, controlla:

- esiste un file `Screen.kt` chiaro e ben nominato
- la route e' stata aggiunta nel `NavHost`
- il punto di ingresso UI ha una callback semplice
- il back funziona
- c'e' almeno una preview oppure un test
- il codice resta leggibile anche per chi apre il progetto per la prima volta

## Regola Pratica

Non tutte le pagine devono nascere gia' con repository, use case e DI.

Per una pagina custom nuova conviene iniziare dal minimo:

- schermata Compose
- callback di navigazione
- contenuto statico o locale

Quando la pagina cresce, la puoi promuovere a feature completa seguendo l'architettura gia' usata in `feature_home` e `feature_todo`.
