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
- [`HomeScreen.kt`](../app/src/main/java/com/example/taskflow2/feature_home/ui/HomeScreen.kt)
- [`ExampleScreen.kt`](../app/src/main/java/com/example/taskflow2/feature_example/ui/ExampleScreen.kt)

## Come Aggiungere una Schermata

Con la struttura attuale il flusso consigliato e' questo:

1. crea la schermata Compose nella feature, per esempio `feature_profilo/ui/ProfiloScreen.kt`
2. aggiungi una nuova route nello stesso file di [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
3. registra la route nel `NavHost`
4. aggiungi una callback semplice alla schermata che deve aprirla, per esempio `onOpenProfile`
5. in `MainActivity` collega quella callback a `navController.navigate(...)`

L'idea chiave e' questa:

- la schermata nuova vive nella sua feature
- la home espone solo callback semplici come `onOpenProfile`
- `MainActivity` decide quale route aprire
- il back di sistema torna indietro da solo grazie a Navigation Compose

## Dove Mettere Cosa

Per non confondersi, usa questa regola pratica:

- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt): contiene `NavHost`, route e collegamenti di navigazione
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

### 2. Aggiungi la route in `MainActivity`

Esempio di nuova route:

```kotlin
private object TaskFlowRoute {
    const val Profile = "profile"
}
```

Regola pratica:

- usa un nome corto e stabile
- evita spazi o testi UI come route
- tieni tutte le route nello stesso punto del file

Esempio buono:

```kotlin
const val Profile = "profile"
```

Esempio da evitare:

```kotlin
const val Profile = "Apri Profilo"
```

Perche' la route e' un identificatore tecnico, non una stringa mostrata all'utente.

### 3. Registra la schermata dentro `NavHost`

Esempio di registrazione nel `NavHost`:

```kotlin
composable(route = TaskFlowRoute.Profile) {
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
        navController.navigate(TaskFlowRoute.Profile) {
            launchSingleTop = true
        }
    },
    onOpenExample = {
        navController.navigate(TaskFlowRoute.Example) {
            launchSingleTop = true
        }
    }
)
```

Questo passaggio e' importante per tenere pulita la UI:

- `HomeScreen` non conosce `NavController`
- `HomeScreen` non conosce la route `"profile"`
- la decisione di navigazione resta tutta in `MainActivity`

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
- per esempi piccoli preferisci tenere route e `NavHost` nello stesso file, qui `MainActivity`
- per esempi piccoli preferisci callback dirette e leggibili invece di helper non necessari
- crea i ViewModel nella destinazione che li usa, non troppo in alto senza motivo
- usa `collectAsStateWithLifecycle()` per osservare stato UI in modo sicuro rispetto al lifecycle
- usa `launchSingleTop` per evitare duplicati della stessa pagina
- usa `navigateUp()` o il back di sistema per tornare alla schermata precedente

## Quando Basta una Pagina UI

Se ti serve solo una pagina statica o un menu di navigazione:

1. crea una cartella feature, per esempio `feature_profilo/ui/`
2. aggiungi un file `ProfiloScreen.kt`
3. definisci una `@Composable` che riceve callback semplici come `onBack`
4. registra la nuova schermata nel `NavHost` in `MainActivity`
5. aggiungi un pulsante nella schermata che deve aprirla

Esempio minimale:

```kotlin
@Composable
fun ProfiloScreen(onBack: () -> Unit) {
    Column {
        Button(onClick = onBack) {
            Text("Indietro")
        }
        Text("Profilo custom")
    }
}
```

Questo approccio va benissimo per:

- schermate placeholder
- menu custom
- pagine informative
- prototipi iniziali

Non serve introdurre subito ViewModel, repository o use case se la pagina non ne ha bisogno.

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
2. Collega la navigazione direttamente in `MainActivity`
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
