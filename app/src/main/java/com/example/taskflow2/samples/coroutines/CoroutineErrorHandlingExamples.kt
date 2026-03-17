package com.example.taskflow2.samples.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * Mini raccolta di esempi didattici su error handling nelle coroutine.
 *
 * Obiettivi:
 * - mostrare quando usare `try/catch`
 * - mostrare quando ha senso `CoroutineExceptionHandler`
 * - mostrare perche' `supervisorScope` evita di cancellare tutti i figli
 *   quando uno fallisce
 */
object CoroutineErrorHandlingExamples {

    /**
     * Usa `try/catch` per gestire localmente un errore e trasformarlo in un
     * risultato sicuro per il chiamante.
     *
     * Nota importante:
     * - NON bisogna mangiare `CancellationException`
     * - la cancellazione va propagata, quindi qui la rilanciamo
     */
    suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    /**
     * Usa `CoroutineExceptionHandler` come ultimo livello di gestione per
     * errori non catturati dentro una coroutine lanciata con `launch`.
     *
     * E' utile soprattutto per logging, telemetry o fallback globale.
     * Non sostituisce `try/catch` quando vuoi recuperare localmente.
     */
    fun launchWithExceptionHandler(
        scope: CoroutineScope,
        onException: (Throwable) -> Unit,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            onException(throwable)
        }

        return scope.launch(exceptionHandler) {
            block()
        }
    }

    /**
     * Usa `supervisorScope` per lanciare figli indipendenti:
     * se uno fallisce, l'altro non viene cancellato automaticamente.
     *
     * Questo e' utile quando vuoi provare a raccogliere risultati parziali.
     */
    suspend fun loadIndependentResults(
        firstBlock: suspend () -> String,
        secondBlock: suspend () -> String
    ): Pair<Result<String>, Result<String>> = supervisorScope {
        val first: Deferred<Result<String>> = async {
            safeCall { firstBlock() }
        }
        val second: Deferred<Result<String>> = async {
            safeCall { secondBlock() }
        }

        first.await() to second.await()
    }

    /**
     * Piccolo helper utile nei test per mostrare che un job puo' completare
     * o fallire senza bisogno di una UI reale.
     */
    fun createCompletionSignal(): CompletableDeferred<String> {
        return CompletableDeferred()
    }
}
