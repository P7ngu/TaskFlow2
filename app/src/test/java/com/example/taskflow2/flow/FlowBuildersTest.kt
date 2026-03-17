package com.example.taskflow2.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowBuildersTest {

    @Test
    fun `flowOf emits declared values in order`() = runTest {
        val collected = mutableListOf<String>()

        flowOf("uno", "due", "tre").collect { value ->
            collected += value
        }

        assertEquals(listOf("uno", "due", "tre"), collected)
    }

    @Test
    fun `asFlow turns collection into flow`() = runTest {
        val collected = mutableListOf<Int>()

        listOf(1, 2, 3).asFlow().collect { value ->
            collected += value
        }

        assertEquals(listOf(1, 2, 3), collected)
    }
}
