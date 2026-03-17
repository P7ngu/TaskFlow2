package com.example.taskflow2.feature_todo.domain.usecase

import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository

/**
 * CRC Card - ToggleTodoUseCase
 *
 * Responsabilita':
 * - Applicare la regola di dominio che inverte lo stato `completed` di un TODO.
 *
 * Serve a:
 * - Evitare che il ViewModel manipoli direttamente regole di business.
 *
 * Collabora con:
 * - `TodoRepository` che salva il TODO aggiornato.
 * - `TodoViewModel` che delega il toggle a questo caso d'uso.
 */
class ToggleTodoUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(todo: Todo) {
        // ❌ NON mettiamo questa logica nel repository perché "invertire completed"
        // non e' persistenza: e' una decisione di business.
        // ✅ Questa e' business logic -> quindi va nel UseCase.
        val updatedTodo = todo.copy(completed = !todo.completed)
        repository.updateTodo(updatedTodo)
    }
}
