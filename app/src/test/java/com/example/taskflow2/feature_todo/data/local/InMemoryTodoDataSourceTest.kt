package com.example.taskflow2.feature_todo.data.local

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryTodoDataSourceTest {

    @Test
    fun `fetchTodoLists simulates async delay but stays testable with virtual time`() = runTest {
        val dataSource = InMemoryTodoDataSource()

        val deferred = async { dataSource.fetchTodoLists() }

        runCurrent()
        advanceTimeBy(999)
        assertFalse(deferred.isCompleted)

        advanceTimeBy(1)
        runCurrent()
        assertTrue(deferred.isCompleted)
        assertEquals(2, deferred.await().size)
        assertEquals("Studiare Clean Architecture", deferred.await().first().title)
    }
}
