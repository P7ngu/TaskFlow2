package com.example.taskflow2.flow

import java.util.concurrent.Executors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowDispatcherTest {

    @Test
    fun `flowOn moves upstream work to a different dispatcher but not the collector`() = runTest {
        val upstreamDispatcher = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "UpstreamFlowDispatcher")
        }.asCoroutineDispatcher()
        val collectorDispatcher = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "CollectorDispatcher")
        }.asCoroutineDispatcher()

        try {
            val trace = mutableListOf<String>()

            withContext(collectorDispatcher) {
                flow {
                    trace += "flow:${Thread.currentThread().name}"
                    emit(1)
                }
                    .map { value ->
                        trace += "map:${Thread.currentThread().name}"
                        value * 10
                    }
                    .flowOn(upstreamDispatcher)
                    .onEach {
                        trace += "onEach:${Thread.currentThread().name}"
                    }
                    .collect {
                        trace += "collect:${Thread.currentThread().name}"
                    }
            }

            assertTrue(trace.any { entry -> entry.startsWith("flow:UpstreamFlowDispatcher") })
            assertTrue(trace.any { entry -> entry.startsWith("map:UpstreamFlowDispatcher") })
            assertTrue(trace.any { entry -> entry.startsWith("onEach:CollectorDispatcher") })
            assertTrue(trace.any { entry -> entry.startsWith("collect:CollectorDispatcher") })
        } finally {
            upstreamDispatcher.close()
            collectorDispatcher.close()
        }
    }
}
