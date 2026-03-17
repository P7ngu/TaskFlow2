package com.example.taskflow2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow2.feature_home.presentation.HomeViewModel
import com.example.taskflow2.feature_home.ui.HomeScreen
import com.example.taskflow2.feature_todo.di.TodoFeatureModule
import com.example.taskflow2.feature_todo.presentation.TodoViewModel
import com.example.taskflow2.feature_todo.ui.TodoScreen
import com.example.taskflow2.ui.theme.TaskFlow2Theme
import dagger.hilt.android.AndroidEntryPoint

/**
 * CRC Card - MainActivity
 *
 * Responsabilita':
 * - Fare da entry point Android dell'app.
 * - Chiedere al modulo DI della feature TODO la factory del ViewModel.
 * - Collegare il ViewModel alla UI Compose.
 *
 * Serve a:
 * - Mostrare come la composition root possa vivere nel layer Android,
 *   oppure essere delegata a moduli di feature, lasciando il domain indipendente
 *   dai dettagli di framework.
 *
 * Collabora con:
 * - `TodoFeatureModule` che costruisce il wiring della feature.
 * - `TodoViewModel` e `TodoScreen` per trasformare dati in UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val todoViewModelFactory by lazy {
        TodoFeatureModule.provideTodoViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TaskFlow2Theme {
                // Home usa Hilt per mostrare una DI moderna e automatica.
                // Todo resta manuale per mantenere nel progetto anche il confronto didattico.
                val homeViewModel: HomeViewModel = hiltViewModel()
                val viewModel: TodoViewModel = viewModel(factory = todoViewModelFactory)
                val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                TodoScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    headerContent = {
                        HomeScreen(
                            uiState = homeUiState,
                            onEvent = homeViewModel::onEvent
                        )
                    }
                )
            }
        }
    }
}
