package com.example.taskflow2.feature_todo.domain.usecase

import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

/**
 * CRC Card - GetTodosUseCase
 *
 * Responsabilita':
 * - Esporre il flusso dei TODO al layer presentation.
 *
 * Serve a:
 * - Incapsulare l'azione di dominio "recupera la lista TODO".
 * - Mantenere il ViewModel indipendente dal repository concreto.
 *
 * Collabora con:
 * - `TodoRepository` da cui ottiene il `Flow<List<Todo>>`.
 * - `TodoViewModel` che osserva il risultato per aggiornare lo stato UI.
 */
class GetTodosUseCase(
    private val repository: TodoRepository
) {
    // Il flusso dei dati segue questa direzione:
    // UI -> ViewModel -> UseCase -> Repository -> Data
    // e poi il risultato risale verso la UI come nuovo stato osservabile.
    operator fun invoke(): Flow<List<Todo>> = repository.getTodos()
}
