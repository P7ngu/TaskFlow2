package com.example.taskflow2.feature_todo.domain.model

/**
 * CRC Card - Todo
 *
 * Responsabilita':
 * - Rappresentare un'entita' del dominio TODO.
 * - Trasportare solo dati significativi per il business: id, titolo, completamento.
 *
 * Serve a:
 * - Essere il modello stabile condiviso tra use case, repository e UI state.
 *
 * Collabora con:
 * - `TodoRepository` che lo legge e lo salva.
 * - `ToggleTodoUseCase` che ne crea una nuova versione con `copy()`.
 * - `TodoUiState` e `TodoScreen` che lo mostrano all'utente.
 */
data class Todo(
    val id: Long,
    val title: String,
    val completed: Boolean
)
