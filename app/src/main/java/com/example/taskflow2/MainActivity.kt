package com.example.taskflow2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taskflow2.feature_todo.di.TodoFeatureModule
import com.example.taskflow2.navigation.AppNavigation
import com.example.taskflow2.samples.hilt.ActivitySessionTracker
import com.example.taskflow2.samples.hilt.AppIdentityProvider
import com.example.taskflow2.ui.theme.TaskFlow2Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * CRC Card - MainActivity
 *
 * Responsabilita':
 * - Fare da entry point Android dell'app.
 * - Chiedere al modulo DI della feature TODO la factory del ViewModel.
 * - Avviare tema e navigation graph dell'app.
 *
 * Serve a:
 * - Lasciare la composition root Android leggera, delegando il wiring UI
 *   della navigazione a `AppNavigation`.
 *
 * Collabora con:
 * - `TodoFeatureModule` che costruisce il wiring della feature.
 * - `AppNavigation` che ospita `NavHost`, schermate e ViewModel di destinazione.
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
                AppNavigation(
                    todoViewModelFactory = todoViewModelFactory
                )
            }
        }
    }
}
