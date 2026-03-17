package com.example.taskflow2.feature_todo.presentation

import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository
import com.example.taskflow2.feature_todo.domain.usecase.AddTodoUseCase
import com.example.taskflow2.feature_todo.domain.usecase.GetTodosUseCase
import com.example.taskflow2.feature_todo.domain.usecase.ToggleTodoUseCase
import com.example.taskflow2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `viewmodel exposes repository data as UI state without a real UI`() = runTest {
        val repository = FakeTodoRepository(
            initialTodos = listOf(
                Todo(id = 1L, title = "Capire la DI", completed = false)
            )
        )

        val viewModel = TodoViewModel(
            getTodosUseCase = GetTodosUseCase(repository),
            addTodoUseCase = AddTodoUseCase(repository),
            toggleTodoUseCase = ToggleTodoUseCase(repository)
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals("Capire la DI", uiState.todos.single().title)
    }

    @Test
    fun `add click clears input after use case succeeds`() = runTest {
        val repository = FakeTodoRepository()
        val viewModel = TodoViewModel(
            getTodosUseCase = GetTodosUseCase(repository),
            addTodoUseCase = AddTodoUseCase(repository),
            toggleTodoUseCase = ToggleTodoUseCase(repository)
        )

        advanceUntilIdle()
        viewModel.onEvent(TodoUiEvent.InputChanged("  Scrivere test  "))
        viewModel.onEvent(TodoUiEvent.AddClicked)
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.inputTitle)
        assertEquals(listOf("Scrivere test"), repository.addedTitles)
    }
}

private class FakeTodoRepository(
    initialTodos: List<Todo> = emptyList()
) : TodoRepository {
    private val todosState = MutableStateFlow(initialTodos)
    val addedTitles = mutableListOf<String>()

    override fun getTodos(): Flow<List<Todo>> = todosState

    override suspend fun fetchTodoLists(): List<Todo> = todosState.value

    override suspend fun addTodo(title: String) {
        addedTitles += title
        val nextTodo = Todo(
            id = todosState.value.size.toLong() + 1L,
            title = title,
            completed = false
        )
        todosState.value = todosState.value + nextTodo
    }

    override suspend fun updateTodo(todo: Todo) {
        todosState.value = todosState.value.map { current ->
            if (current.id == todo.id) todo else current
        }
    }
}
