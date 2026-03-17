package com.example.taskflow2.feature_todo.domain.usecase

import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTodoUseCaseTest {

    @Test
    fun `blank title is rejected without touching repository`() = runTest {
        val repository = FakeTodoRepository()
        val useCase = AddTodoUseCase(repository)

        val added = useCase("   ")

        assertFalse(added)
        assertTrue(repository.addedTitles.isEmpty())
    }

    @Test
    fun `valid title is trimmed before reaching repository`() = runTest {
        val repository = FakeTodoRepository()
        val useCase = AddTodoUseCase(repository)

        val added = useCase("  Studiare MVVM  ")

        assertTrue(added)
        assertEquals(listOf("Studiare MVVM"), repository.addedTitles)
    }
}

private class FakeTodoRepository : TodoRepository {
    private val todosState = MutableStateFlow(emptyList<Todo>())
    val addedTitles = mutableListOf<String>()

    override fun getTodos(): Flow<List<Todo>> = todosState

    override suspend fun fetchTodoLists(): List<Todo> = todosState.value

    override suspend fun addTodo(title: String) {
        addedTitles += title
    }

    override suspend fun updateTodo(todo: Todo) = Unit
}
