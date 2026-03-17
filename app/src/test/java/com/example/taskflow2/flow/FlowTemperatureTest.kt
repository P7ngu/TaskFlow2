package com.example.taskflow2.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowTemperatureTest {

    @Test
    fun `cold flow restarts upstream work for each collector`() = runTest {
        var starts = 0

        val coldFlow = flow {
            starts += 1
            emit("start-$starts")
        }

        assertEquals("start-1", coldFlow.first())
        assertEquals("start-2", coldFlow.first())
    }

    @Test
    fun `shared flow is hot and misses old values without replay`() = runTest {
        val collectorDispatcher = UnconfinedTestDispatcher(testScheduler)
        val sharedFlow = MutableSharedFlow<String>()
        val collected = mutableListOf<String>()

        // Essendo hot, questa emissione avviene anche senza collector attivi.
        // Con replay = 0 il valore non verra' ricevuto da chi si iscrive dopo.
        sharedFlow.emit("prima")

        val job = launch(collectorDispatcher) {
            sharedFlow.take(2).collect { value ->
                collected += value
            }
        }

        runCurrent()
        sharedFlow.emit("seconda")
        sharedFlow.emit("terza")
        job.join()

        assertEquals(listOf("seconda", "terza"), collected)
    }

    @Test
    fun `state flow is hot and immediately exposes the latest state`() = runTest {
        val collectorDispatcher = UnconfinedTestDispatcher(testScheduler)
        val stateFlow = MutableStateFlow("idle")
        val collected = mutableListOf<String>()

        stateFlow.value = "loading"

        val job = launch(collectorDispatcher) {
            stateFlow.take(2).collect { value ->
                collected += value
            }
        }

        runCurrent()
        stateFlow.value = "done"
        job.join()

        assertEquals(listOf("loading", "done"), collected)
        assertEquals("done", stateFlow.value)
    }
}
