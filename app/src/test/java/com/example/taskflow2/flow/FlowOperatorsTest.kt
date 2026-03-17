package com.example.taskflow2.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowOperatorsTest {

    @Test
    fun `flow operators are lazy until collect starts the pipeline`() = runTest {
        val trace = mutableListOf<String>()

        val pipeline = flowOf(1, 2, 3, 4)
            .onEach { value ->
                trace += "onEach:$value"
            }
            .filter { value ->
                trace += "filter:$value"
                value % 2 == 0
            }
            .map { value ->
                trace += "map:$value"
                value * 10
            }
            .transform { value ->
                trace += "transform:$value"
                emit("item=$value")
                emit("double=${value * 2}")
            }
            .take(3)

        // Finche' non arriva un terminal operator come `collect`, la pipeline
        // non parte: i Flow sono lazy.
        assertTrue(trace.isEmpty())

        val collected = mutableListOf<String>()

        pipeline.collect { value ->
            trace += "collect:$value"
            collected += value
        }

        assertEquals(
            listOf("item=20", "double=40", "item=40"),
            collected
        )
        assertTrue(trace.isNotEmpty())
    }

    @Test
    fun `catch onCompletion and collect show the flow lifecycle`() = runTest {
        val trace = mutableListOf<String>()
        val collected = mutableListOf<Int>()

        flow {
            emit(1)
            emit(2)
            throw IllegalStateException("boom")
        }
            .onEach { value ->
                trace += "onEach:$value"
            }
            .catch { throwable ->
                trace += "catch:${throwable.message}"
                emit(-1)
            }
            .onCompletion { cause ->
                trace += "onCompletion:${cause?.message ?: "completed"}"
            }
            .collect { value ->
                trace += "collect:$value"
                collected += value
            }

        assertEquals(listOf(1, 2, -1), collected)
        assertEquals(
            listOf(
                "onEach:1",
                "collect:1",
                "onEach:2",
                "collect:2",
                "catch:boom",
                "collect:-1",
                "onCompletion:completed"
            ),
            trace
        )
    }
}
