package com.example.taskflow2.feature_todo.presentation

import com.example.taskflow2.feature_todo.domain.model.Todo

/**
 * CRC Card - TodoUiState
 *
 * Responsabilita':
 * - Descrivere in modo immutabile tutto cio' che la UI TODO deve renderizzare.
 *
 * Serve a:
 * - Rendere esplicito lo stato osservato via `StateFlow`.
 * - Supportare UDF: ogni render dipende solo da questo stato.
 *
 * Collabora con:
 * - `TodoViewModel` che lo aggiorna con `copy()`.
 * - `TodoScreen` che lo legge per disegnare l'interfaccia.
 */
data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val inputTitle: String = "",
    val isLoading: Boolean = true
)

/**
 * CRC Card - TodoUiEvent
 *
 * Responsabilita':
 * - Modellare gli eventi utente che partono dalla UI TODO.
 *
 * Serve a:
 * - Rendere esplicito il canale "UI -> ViewModel" dell'Unidirectional Data Flow.
 *
 * Collabora con:
 * - `TodoScreen` che emette questi eventi.
 * - `TodoViewModel` che li interpreta e orchestra i casi d'uso.
 */
sealed interface TodoUiEvent {
    data class InputChanged(val value: String) : TodoUiEvent

    data object AddClicked : TodoUiEvent

    data class ToggleClicked(val todo: Todo) : TodoUiEvent
}
