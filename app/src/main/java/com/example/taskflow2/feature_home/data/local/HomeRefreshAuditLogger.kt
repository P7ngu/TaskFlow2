package com.example.taskflow2.feature_home.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Esempio didattico di uso corretto di `withContext(Dispatchers.IO)`.
 *
 * Qui facciamo una vera operazione di I/O bloccante: scriviamo su un file
 * interno dell'app. Questo lavoro non dovrebbe stare sul main thread, quindi
 * lo spostiamo esplicitamente sul dispatcher `IO`.
 *
 * Perche' e' un buon esempio:
 * - mostra un caso reale in cui `withContext(Dispatchers.IO)` ha senso
 * - evita di bloccare il thread UI
 * - usa `@ApplicationContext`, quindi non trattiene riferimenti a una Activity
 *
 * Nota didattica:
 * - NON stiamo usando `withContext(IO)` attorno a Retrofit "solo perche' fa rete"
 * - nel caso di Retrofit suspend, spesso quel wrapping sarebbe superfluo o
 *   fuorviante; qui invece il lavoro e' davvero I/O bloccante su file
 */
@Singleton
class HomeRefreshAuditLogger @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    private val logFile: File
        get() = File(applicationContext.filesDir, "home-refresh-audit.log")

    suspend fun logRefresh(entry: String) {
        withContext(Dispatchers.IO) {
            logFile.appendText("$entry\n")
        }
    }
}
