package com.example.taskflow2.feature_todo.data.repository

import com.example.taskflow2.feature_todo.data.local.InMemoryTodoDataSource
import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

/**
 * CRC Card - TodoRepositoryImpl
 *
 * Responsabilita':
 * - Implementare il contratto `TodoRepository`.
 * - Coordinare la sorgente dati concreta senza introdurre business logic.
 *
 * Serve a:
 * - Fare da ponte tra domain e data source locale.
 *
 * Collabora con:
 * - `InMemoryTodoDataSource` per operazioni tecniche di lettura/scrittura.
 * - `TodoRepository` come contratto esposto al domain.
 */
class TodoRepositoryImpl(
    private val localDataSource: InMemoryTodoDataSource
) : TodoRepository {

    // Il repository coordina la sorgente dati concreta.
    // In un'app reale qui potremmo unire cache locale + API remota.
    // Importante: non decide le regole del dominio.
    override fun getTodos(): Flow<List<Todo>> = localDataSource.observeTodos()

    override suspend fun addTodo(title: String) {
        localDataSource.insertTodo(title)
    }

    override suspend fun updateTodo(todo: Todo) {
        localDataSource.updateTodo(todo)
    }
}
