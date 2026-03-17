package com.example.taskflow2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskflow2.feature_example.ui.ExampleScreen
import com.example.taskflow2.feature_home.presentation.HomeViewModel
import com.example.taskflow2.feature_home.ui.HomeScreen
import com.example.taskflow2.feature_todo.di.TodoFeatureModule
import com.example.taskflow2.feature_todo.presentation.TodoViewModel
import com.example.taskflow2.feature_todo.ui.TodoScreen
import com.example.taskflow2.samples.hilt.ActivitySessionTracker
import com.example.taskflow2.samples.hilt.AppIdentityProvider
import com.example.taskflow2.ui.theme.TaskFlow2Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Per questo progetto teniamo le route vicino al `NavHost`.
// Best practice per un esempio didattico piccolo:
// - niente stringhe duplicate sparse nel file
// - niente file extra da aprire per capire il flusso
// - nomi di route semplici e stabili, separati dal testo mostrato in UI
//
// Come aggiungere una nuova schermata:
// 1. aggiungi qui una nuova costante, per esempio `const val Profile = "profile"`
// 2. aggiungi una nuova `composable(route = TaskFlowRoute.Profile) { ... }`
// 3. collega una callback UI a `navController.navigate(TaskFlowRoute.Profile)`
private object TaskFlowRoute {
    const val Home = "home"
    const val Example = "example"
}

/**
 * CRC Card - MainActivity
 *
 * Responsabilita':
 * - Fare da entry point Android dell'app.
 * - Chiedere al modulo DI della feature TODO la factory del ViewModel.
 * - Collegare ViewModel, schermate e navigazione Compose.
 *
 * Serve a:
 * - Mostrare una composition root Android leggibile e raccolta in un solo file,
 *   utile soprattutto quando si sta studiando il flusso generale dell'app.
 *
 * Collabora con:
 * - `TodoFeatureModule` che costruisce il wiring della feature.
 * - `TodoViewModel`, `HomeViewModel`, `TodoScreen` e `HomeScreen`.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appIdentityProvider: AppIdentityProvider

    @Inject
    lateinit var activitySessionTracker: ActivitySessionTracker

    private val todoViewModelFactory by lazy {
        TodoFeatureModule.provideTodoViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Esempio pratico:
        // `ActivitySessionTracker` e' `@ActivityScoped` perche' vive quanto
        // questa Activity e usa `@ActivityContext`.
        // Renderlo `@Singleton` sarebbe un errore: rischieremmo di trattenere
        // un riferimento a una Activity distrutta.
        activitySessionTracker.currentSessionTag()

        // `AppIdentityProvider` invece usa `@ApplicationContext`, quindi puo'
        // vivere come `@Singleton` senza trattenere riferimenti UI.
        // Qui mostriamo anche un qualifier custom (`@AppPackageName`) per
        // distinguere quale `String` vogliamo ricevere dal grafo Hilt.
        appIdentityProvider.identityLabel()

        setContent {
            TaskFlow2Theme {
                // Per tenere l'esempio facile da studiare, la navigazione vive
                // direttamente qui: aprendo `MainActivity` si vede in un solo
                // punto come vengono creati i ViewModel e come si passa da
                // `Home` a `Example`.
                //
                // Best practice:
                // - un solo `NavController` creato nel punto piu' alto della graph
                // - callback semplici passate alle schermate
                // - nessun `NavController` passato dentro i composable di feature
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = TaskFlowRoute.Home
                ) {
                    // Per aggiungere una nuova schermata:
                    // - definisci una nuova route sopra
                    // - copia uno dei blocchi `composable(...)`
                    // - dentro il blocco mostra la nuova `Screen(...)`
                    composable(route = TaskFlowRoute.Home) {
                        // Home usa Hilt per mostrare una DI moderna e automatica.
                        // Todo resta manuale per mantenere nel progetto anche il confronto didattico.
                        //
                        // Best practice:
                        // creiamo i ViewModel dentro la destinazione che li usa,
                        // cosi' il loro scope segue la voce del back stack e non
                        // rischiamo di allargarlo inutilmente.
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        val todoViewModel: TodoViewModel = viewModel(factory = todoViewModelFactory)
                        val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                        val todoUiState by todoViewModel.uiState.collectAsStateWithLifecycle()

                        TodoScreen(
                            uiState = todoUiState,
                            onEvent = todoViewModel::onEvent,
                            headerContent = {
                                // Questo e' il punto in cui colleghiamo la UI
                                // della home alla navigazione reale.
                                //
                                // Se aggiungi un'altra schermata:
                                // 1. esponi una nuova callback in `HomeScreen`,
                                //    per esempio `onOpenProfile`
                                // 2. passala qui
                                // 3. dentro la lambda chiama
                                //    `navController.navigate(TaskFlowRoute.Profile)`
                                HomeScreen(
                                    uiState = homeUiState,
                                    onEvent = homeViewModel::onEvent,
                                    onOpenExample = {
                                        // La UI chiede solo "apri esempio".
                                        // La decisione concreta di navigazione resta qui.
                                        //
                                        // `launchSingleTop = true` e' una best practice
                                        // utile quando la stessa azione puo' essere
                                        // premuta piu' volte: evita copie duplicate
                                        // della stessa schermata in cima allo stack.
                                        navController.navigate(TaskFlowRoute.Example) {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        )
                    }

                    composable(route = TaskFlowRoute.Example) {
                        // Questa schermata e' minimale: il back di sistema
                        // torna automaticamente alla home grazie allo stack
                        // gestito da Navigation Compose.
                        //
                        // Best practice:
                        // se una schermata non ha bisogno di azioni di
                        // navigazione esplicite nella UI, non forziamo callback
                        // inutili. Meno parametri significa esempio piu' chiaro.
                        //
                        // Se aggiungi una nuova schermata con un bottone "Indietro",
                        // passa `onBack = navController::navigateUp` alla sua UI.
                        ExampleScreen()
                    }
                }
            }
        }
    }
}
