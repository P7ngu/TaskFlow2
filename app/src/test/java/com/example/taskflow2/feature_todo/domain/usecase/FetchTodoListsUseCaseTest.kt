package com.example.taskflow2.feature_todo.domain.usecase

import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchTodoListsUseCaseTest {

    @Test
    fun `returns one shot snapshot from repository`() = runTest {
        val repository = SnapshotTodoRepositoryFake(
            snapshot = listOf(
                Todo(id = 1L, title = "Lista snapshot", completed = false),
                Todo(id = 2L, title = "Secondo elemento", completed = true)
            )
        )
        val useCase = FetchTodoListsUseCase(repository)

        val result = useCase()

        assertEquals(2, result.size)
        assertEquals("Lista snapshot", result.first().title)
    }
}

private class SnapshotTodoRepositoryFake(
    private val snapshot: List<Todo>
) : TodoRepository {
    private val todosState = MutableStateFlow(snapshot)

    override fun getTodos(): Flow<List<Todo>> = todosState

    override suspend fun fetchTodoLists(): List<Todo> = snapshot

    override suspend fun addTodo(title: String) = Unit

    override suspend fun updateTodo(todo: Todo) = Unit
}
