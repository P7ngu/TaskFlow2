package com.example.taskflow2.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTimeControlTest {

    @Test
    fun `runTest lets us skip delay with virtual time`() = runTest {
        val useCase = DelayedGreetingUseCase()

        val deferred = async { useCase() }

        advanceTimeBy(999)
        assertFalse(deferred.isCompleted)

        advanceTimeBy(1)
        assertEquals("Ciao dopo delay", deferred.await())
    }

    @Test
    fun `advanceUntilIdle completes pending coroutine work`() = runTest {
        val useCase = DeferredCompletionUseCase()
        val result = CompletableDeferred<String>()

        val job = async {
            result.complete(useCase())
        }

        advanceUntilIdle()

        assertTrue(job.isCompleted)
        assertEquals("Operazione completata", result.await())
    }
}

private class DelayedGreetingUseCase {
    suspend operator fun invoke(): String {
        delay(1_000)
        return "Ciao dopo delay"
    }
}

private class DeferredCompletionUseCase {
    suspend operator fun invoke(): String {
        delay(300)
        delay(700)
        return "Operazione completata"
    }
}
