package com.example.taskflow2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskflow2.feature_example.ui.ExampleScreen
import com.example.taskflow2.feature_home.presentation.HomeViewModel
import com.example.taskflow2.feature_home.ui.HomeScreen
import com.example.taskflow2.feature_todo.presentation.TodoViewModel
import com.example.taskflow2.feature_todo.ui.TodoScreen

/**
 * Elenco centralizzato delle schermate dell'app.
 *
 * Una sealed class e' utile quando vogliamo evitare stringhe duplicate e avere
 * in un solo posto l'elenco delle destinazioni disponibili.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Example : Screen("example")
}

/**
 * Navigation graph principale dell'app.
 *
 * Best practice mostrate qui:
 * - un solo `NavController` creato al punto piu' alto della graph
 * - schermate registrate tramite `Screen.route`
 * - ViewModel creati nella `composable(...)` che li usa davvero
 * - UI che riceve solo `uiState` e callback, non il `NavController`
 */
@Composable
fun AppNavigation(
    todoViewModelFactory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            // Anche se la navigation vive in `AppNavigation`, non conviene
            // creare qui in anticipo tutti i ViewModel dell'app. Li creiamo
            // solo dentro la schermata che li usa per mantenere scope e
            // responsabilita' piu' chiari.
            val homeViewModel: HomeViewModel = hiltViewModel()
            val todoViewModel: TodoViewModel = viewModel(factory = todoViewModelFactory)

            // `collectAsStateWithLifecycle()` converte lo `StateFlow` del
            // ViewModel in stato Compose rispettando il lifecycle.
            // La delegation `by` ci fa leggere direttamente il valore corrente.
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val todoUiState by todoViewModel.uiState.collectAsStateWithLifecycle()

            TodoScreen(
                uiState = todoUiState,
                onEvent = todoViewModel::onEvent,
                headerContent = {
                    HomeScreen(
                        uiState = homeUiState,
                        onEvent = homeViewModel::onEvent,
                        onOpenExample = {
                            navController.navigate(Screen.Example.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            )
        }

        composable(Screen.Example.route) {
            ExampleScreen(
                onBack = navController::navigateUp
            )
        }
    }
}
