package com.example.taskflow2.flow

import java.util.concurrent.Executors
import kotlinx.coroutines.ExecutorCoroutineDispatcher
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
        val upstreamDispatcher = namedDispatcher("UpstreamFlowDispatcher")
        val collectorDispatcher = namedDispatcher("CollectorDispatcher")

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

            assertTrue(trace.containsThread("flow", "UpstreamFlowDispatcher"))
            assertTrue(trace.containsThread("map", "UpstreamFlowDispatcher"))
            assertTrue(trace.containsThread("onEach", "CollectorDispatcher"))
            assertTrue(trace.containsThread("collect", "CollectorDispatcher"))
        } finally {
            upstreamDispatcher.close()
            collectorDispatcher.close()
        }
    }
}

private fun namedDispatcher(name: String): ExecutorCoroutineDispatcher {
    return Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, name)
    }.asCoroutineDispatcher()
}

private fun List<String>.containsThread(
    stage: String,
    threadName: String
): Boolean {
    return any { entry ->
        entry.startsWith("$stage:$threadName")
    }
}
