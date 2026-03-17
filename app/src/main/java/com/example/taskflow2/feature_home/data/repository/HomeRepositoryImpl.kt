package com.example.taskflow2.feature_home.data.repository

import com.example.taskflow2.feature_home.data.local.HomeLocalDataSource
import com.example.taskflow2.feature_home.data.local.HomeLocalModel
import com.example.taskflow2.feature_home.data.local.HomeRefreshAuditLogger
import com.example.taskflow2.feature_home.data.remote.HomeRemoteDataSource
import com.example.taskflow2.feature_home.domain.model.HomeInfo
import com.example.taskflow2.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CRC Card - HomeRepositoryImpl
 *
 * Responsabilita':
 * - Implementare `HomeRepository`.
 * - Coordinare local cache e sorgente remota della feature Home.
 * - Mappare i modelli tecnici verso il domain.
 *
 * Serve a:
 * - Fare da adapter tra il domain e le infrastrutture concrete.
 *
 * Collabora con:
 * - `HomeLocalDataSource` per osservazione e salvataggio cache.
 * - `HomeRemoteDataSource` per il recupero remoto.
 * - `HomeInfo` come modello di dominio restituito ai use case.
 */
// `@Singleton` evita di ricreare wiring e cache inutilmente.
// Il repository non conserva riferimenti UI, quindi non introduce leak di
// Activity o ViewModel.
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val localDataSource: HomeLocalDataSource,
    private val remoteDataSource: HomeRemoteDataSource,
    private val homeRefreshAuditLogger: HomeRefreshAuditLogger
) : HomeRepository {

    override fun observeHomeInfo(): Flow<HomeInfo?> {
        // Il repository coordina sorgenti diverse:
        // local = cache, remote = rete.
        // Non applica business logic: si occupa di leggere, salvare e mappare i dati.
        return localDataSource.observeCachedHome().map { cachedModel ->
            cachedModel?.toDomain()
        }
    }

    override suspend fun refreshHomeInfo() {
        val remoteModel = remoteDataSource.fetchHomeInfo()

        localDataSource.save(
            HomeLocalModel(
                welcomeMessage = remoteModel.welcomeMessage,
                serverStatus = remoteModel.serverStatus,
                lastSyncLabel = remoteModel.fetchedAt
            )
        )

        // Esempio pratico:
        // la scrittura su file e' I/O bloccante, quindi il logger usa
        // `withContext(Dispatchers.IO)` invece di girare sul main thread.
        homeRefreshAuditLogger.logRefresh(
            entry = "home-refresh:${remoteModel.fetchedAt}"
        )
    }
}

private fun HomeLocalModel.toDomain(): HomeInfo {
    return HomeInfo(
        welcomeMessage = welcomeMessage,
        serverStatus = serverStatus,
        lastSyncLabel = lastSyncLabel
    )
}
