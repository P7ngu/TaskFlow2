package com.example.taskflow2.feature_todo.di

import androidx.lifecycle.ViewModelProvider
import com.example.taskflow2.feature_todo.data.local.InMemoryTodoDataSource
import com.example.taskflow2.feature_todo.data.repository.TodoRepositoryImpl
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository
import com.example.taskflow2.feature_todo.domain.usecase.AddTodoUseCase
import com.example.taskflow2.feature_todo.domain.usecase.GetTodosUseCase
import com.example.taskflow2.feature_todo.domain.usecase.ToggleTodoUseCase
import com.example.taskflow2.feature_todo.presentation.TodoViewModel

/**
 * CRC Card - TodoFeatureModule
 *
 * Responsabilita':
 * - Costruire le dipendenze concrete della feature TODO.
 * - Esporre la factory Android usata per creare `TodoViewModel`.
 *
 * Serve a:
 * - Tenere il wiring della feature in un punto dedicato invece che sparso nella Activity.
 *
 * Collabora con:
 * - `InMemoryTodoDataSource`, `TodoRepositoryImpl`, `TodoRepository`.
 * - `GetTodosUseCase`, `AddTodoUseCase`, `ToggleTodoUseCase`.
 * - `TodoViewModel`.
 */
object TodoFeatureModule {

    fun provideTodoViewModelFactory(): ViewModelProvider.Factory {
        val localDataSource = InMemoryTodoDataSource()
        val repository: TodoRepository = TodoRepositoryImpl(localDataSource)
        val getTodosUseCase = GetTodosUseCase(repository)
        val addTodoUseCase = AddTodoUseCase(repository)
        val toggleTodoUseCase = ToggleTodoUseCase(repository)

        // La factory Android e' wiring di framework:
        // per Clean Architecture e' piu' pulito tenerla nel layer `di`,
        // non nel `presentation`, che deve limitarsi a stato ed orchestrazione.
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                    return TodoViewModel(
                        getTodosUseCase = getTodosUseCase,
                        addTodoUseCase = addTodoUseCase,
                        toggleTodoUseCase = toggleTodoUseCase
                    ) as T
                }

                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
