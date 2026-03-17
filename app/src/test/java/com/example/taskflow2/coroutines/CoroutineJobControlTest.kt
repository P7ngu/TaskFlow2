package com.example.taskflow2.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineJobControlTest {

    @Test
    fun `job finishes normally and exposes completed state`() = runTest {
        val job = launch {
            delay(1_000)
        }

        runCurrent()
        assertTrue(job.isActive)
        assertFalse(job.isCompleted)
        assertFalse(job.isCancelled)

        advanceTimeBy(1_000)
        runCurrent()
        job.join()

        assertFalse(job.isActive)
        assertTrue(job.isCompleted)
        assertFalse(job.isCancelled)
    }

    @Test
    fun `cancelAndJoin cancels cooperative coroutine and waits for termination`() = runTest {
        val job = launch {
            while (isActive) {
                delay(100)
            }
        }

        runCurrent()
        assertTrue(job.isActive)
        assertFalse(job.isCompleted)
        assertFalse(job.isCancelled)

        job.cancelAndJoin()

        assertFalse(job.isActive)
        assertTrue(job.isCompleted)
        assertTrue(job.isCancelled)
    }
}
