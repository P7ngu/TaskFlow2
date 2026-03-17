package com.example.taskflow2.feature_home.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CRC Card - HomeLocalDataSource
 *
 * Responsabilita':
 * - Gestire la cache locale della feature Home.
 * - Salvare e restituire l'ultimo snapshot disponibile.
 *
 * Serve a:
 * - Mostrare la differenza tra sorgente locale tecnica e repository.
 *
 * Collabora con:
 * - `HomeRepositoryImpl` che lo usa come cache.
 * - `HomeLocalModel` come struttura dati locale.
 */
@Singleton
class HomeLocalDataSource @Inject constructor() {

    // Data source locale = cache tecnica.
    // Non decide regole di business: conserva solo l'ultimo snapshot disponibile.
    private val cachedState = MutableStateFlow(
        HomeLocalModel(
            welcomeMessage = "Bentornato! Cache locale disponibile.",
            serverStatus = "Ultimo stato noto: stabile",
            lastSyncLabel = "Sincronizzazione locale iniziale"
        )
    )

    fun observeCachedHome(): Flow<HomeLocalModel?> = cachedState.asStateFlow()

    suspend fun save(home: HomeLocalModel) {
        cachedState.value = home
    }
}

/**
 * CRC Card - HomeLocalModel
 *
 * Responsabilita':
 * - Rappresentare il formato salvato nella cache locale della Home.
 *
 * Serve a:
 * - Separare il modello tecnico locale dal modello di dominio `HomeInfo`.
 *
 * Collabora con:
 * - `HomeLocalDataSource` che lo salva.
 * - `HomeRepositoryImpl` che lo mappa verso il domain.
 */
data class HomeLocalModel(
    val welcomeMessage: String,
    val serverStatus: String,
    val lastSyncLabel: String
)
