package com.example.taskflow2.coroutines

import com.example.taskflow2.core.coroutines.CoroutineErrorHandlingExamples
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineErrorHandlingExamplesTest {

    @Test
    fun `safeCall converts exception into Result failure`() = runTest {
        val result = CoroutineErrorHandlingExamples.safeCall<String> {
            error("boom")
        }

        assertTrue(result.isFailure)
        assertEquals("boom", result.exceptionOrNull()?.message)
    }

    @Test
    fun `CoroutineExceptionHandler catches uncaught launch exception`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val scope = CoroutineScope(dispatcher)
        val captured = CompletableDeferred<String>()

        CoroutineErrorHandlingExamples.launchWithExceptionHandler(
            scope = scope,
            onException = { throwable ->
                captured.complete(throwable.message ?: "no-message")
            }
        ) {
            error("handler-error")
        }

        advanceUntilIdle()
        assertEquals("handler-error", captured.await())
    }

    @Test
    fun `supervisorScope keeps sibling result available when one fails`() = runTest {
        val (first, second) = CoroutineErrorHandlingExamples.loadIndependentResults(
            firstBlock = {
                delay(100)
                "ok-first"
            },
            secondBlock = {
                delay(50)
                error("second-failed")
            }
        )

        assertTrue(first.isSuccess)
        assertEquals("ok-first", first.getOrNull())
        assertTrue(second.isFailure)
        assertEquals("second-failed", second.exceptionOrNull()?.message)
    }
}
