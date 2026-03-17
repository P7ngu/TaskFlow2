package com.example.taskflow2.feature_todo.domain.usecase

import com.example.taskflow2.feature_todo.domain.repository.TodoRepository

/**
 * CRC Card - AddTodoUseCase
 *
 * Responsabilita':
 * - Validare il titolo e avviare l'aggiunta di un nuovo TODO.
 *
 * Serve a:
 * - Tenere la business logic fuori dal ViewModel.
 * - Centralizzare la regola di dominio "non aggiungere titoli vuoti".
 *
 * Collabora con:
 * - `TodoRepository` che esegue la persistenza.
 * - `TodoViewModel` che gli delega l'evento di aggiunta.
 */
class AddTodoUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(title: String): Boolean {
        val sanitizedTitle = title.trim()

        // ❌ NON mettiamo questa logica nel ViewModel perché il ViewModel deve
        // orchestrare la UI, non decidere le regole del dominio.
        // ✅ Questa è business logic -> quindi va nel UseCase.
        if (sanitizedTitle.isBlank()) {
            return false
        }

        repository.addTodo(sanitizedTitle)
        return true
    }
}
