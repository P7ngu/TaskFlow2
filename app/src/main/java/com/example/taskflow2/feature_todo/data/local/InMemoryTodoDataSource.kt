package com.example.taskflow2.feature_todo.data.local

import com.example.taskflow2.feature_todo.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * CRC Card - InMemoryTodoDataSource
 *
 * Responsabilita':
 * - Simulare una sorgente dati locale in memoria.
 * - Leggere e scrivere record TODO senza logica di business.
 *
 * Serve a:
 * - Mostrare il ruolo di un data source concreto, separato dal repository.
 *
 * Collabora con:
 * - `TodoRepositoryImpl` che lo usa come fonte dati locale.
 * - `Todo` come modello memorizzato.
 */
class InMemoryTodoDataSource {

    // Questo data source simula un database locale.
    // Sa solo fare operazioni tecniche di lettura/scrittura sui dati.
    // Non contiene regole di business.
    private val todos = MutableStateFlow(
        listOf(
            Todo(id = 1L, title = "Studiare Clean Architecture", completed = false),
            Todo(id = 2L, title = "Provare Jetpack Compose", completed = true)
        )
    )

    private var nextId = 3L

    fun observeTodos(): Flow<List<Todo>> = todos.asStateFlow()

    suspend fun insertTodo(title: String) {
        val newTodo = Todo(
            id = nextId++,
            title = title,
            completed = false
        )

        todos.update { currentTodos ->
            currentTodos + newTodo
        }
    }

    suspend fun updateTodo(updatedTodo: Todo) {
        todos.update { currentTodos ->
            currentTodos.map { currentTodo ->
                if (currentTodo.id == updatedTodo.id) {
                    updatedTodo
                } else {
                    currentTodo
                }
            }
        }
    }
}
