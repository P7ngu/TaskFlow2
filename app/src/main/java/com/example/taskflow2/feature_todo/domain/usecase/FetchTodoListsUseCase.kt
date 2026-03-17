package com.example.taskflow2.feature_todo.domain.usecase

import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository

/**
 * CRC Card - FetchTodoListsUseCase
 *
 * Responsabilita':
 * - Esporre una lettura one-shot della lista TODO.
 *
 * Serve a:
 * - Mostrare la differenza tra una fetch `suspend` che restituisce uno snapshot
 *   e un `Flow` che continua a emettere aggiornamenti nel tempo.
 * - Tenere il caller indipendente dal repository concreto.
 *
 * Collabora con:
 * - `TodoRepository` da cui ottiene la lista corrente.
 */
class FetchTodoListsUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(): List<Todo> = repository.fetchTodoLists()
}
