package com.example.taskflow2.feature_home.presentation

/**
 * CRC Card - HomeUiState
 *
 * Responsabilita':
 * - Contenere tutto lo stato necessario a renderizzare la schermata Home.
 *
 * Serve a:
 * - Supportare una UI dichiarativa governata da un singolo stato immutabile.
 *
 * Collabora con:
 * - `HomeViewModel` che lo aggiorna.
 * - `HomeScreen` che lo legge.
 */
data class HomeUiState(
    val welcomeMessage: String = "",
    val serverStatus: String = "",
    val lastSyncLabel: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)

/**
 * CRC Card - HomeUiEvent
 *
 * Responsabilita':
 * - Rappresentare gli eventi utente della feature Home.
 *
 * Serve a:
 * - Mantenere esplicito il canale di input verso il ViewModel.
 *
 * Collabora con:
 * - `HomeScreen` che genera gli eventi.
 * - `HomeViewModel` che li interpreta.
 */
sealed interface HomeUiEvent {
    data object RefreshClicked : HomeUiEvent
}
