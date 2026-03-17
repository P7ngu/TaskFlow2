package com.example.taskflow2.feature_home.domain.repository

import com.example.taskflow2.feature_home.domain.model.HomeInfo
import kotlinx.coroutines.flow.Flow

/**
 * CRC Card - HomeRepository
 *
 * Responsabilita':
 * - Definire il contratto di dominio per leggere e aggiornare i dati della Home.
 *
 * Serve a:
 * - Isolare il domain da local cache, rete e dettagli di implementazione.
 *
 * Collabora con:
 * - `ObserveHomeInfoUseCase` e `RefreshHomeInfoUseCase`.
 * - `HomeRepositoryImpl` che implementa il contratto nel layer data.
 */
interface HomeRepository {
    fun observeHomeInfo(): Flow<HomeInfo?>

    suspend fun refreshHomeInfo()
}
