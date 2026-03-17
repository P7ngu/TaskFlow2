package com.example.taskflow2.feature_todo.domain.repository

import com.example.taskflow2.feature_todo.domain.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * CRC Card - TodoRepository
 *
 * Responsabilita':
 * - Definire il contratto del dominio per lavorare con i TODO.
 * - Esporre operazioni astratte senza rivelare dettagli di persistenza.
 *
 * Serve a:
 * - Applicare inversion of dependency: il domain dipende da un'astrazione.
 *
 * Collabora con:
 * - `GetTodosUseCase`, `AddTodoUseCase`, `ToggleTodoUseCase` che usano questo contratto.
 * - `TodoRepositoryImpl` che fornisce l'implementazione concreta nel layer data.
 */
// Il repository e il data source NON sono la stessa cosa.
// - Data source: sa leggere/scrivere in una fonte concreta (memory, DB, rete...)
// - Repository: offre al domain un contratto stabile e coordina le sorgenti dati
//
// Il repository è un'interfaccia per rispettare inversion of dependency:
// i casi d'uso dipendono da un'astrazione, non da una classe concreta.
// In questo modo il domain resta indipendente da Android, Compose e dettagli tecnici.
interface TodoRepository {
    fun getTodos(): Flow<List<Todo>>

    suspend fun addTodo(title: String)

    suspend fun updateTodo(todo: Todo)
}
