package com.example.taskflow2.feature_home.data.remote

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CRC Card - HomeRemoteDataSource
 *
 * Responsabilita':
 * - Simulare una sorgente dati remota della feature Home.
 * - Restituire un payload tecnico come farebbe una API.
 *
 * Serve a:
 * - Mostrare dove vivono le operazioni di rete in una struttura per feature.
 *
 * Collabora con:
 * - `HomeRepositoryImpl` che lo interroga durante il refresh.
 * - `HomeRemoteModel` come risposta tecnica del layer data.
 */
@Singleton
class HomeRemoteDataSource @Inject constructor(
    private val homeApiService: HomeApiService
) {

    // Qui la chiamata passa davvero da Retrofit.
    // Per tenere l'esempio eseguibile senza backend, la risposta viene mockata da OkHttp.
    suspend fun fetchHomeInfo(): HomeRemoteModel {
        return runCatching {
            homeApiService.fetchHomeInfo().toModel()
        }.getOrElse {
            HomeRemoteModel(
                welcomeMessage = "Fallback locale dopo errore Retrofit",
                serverStatus = "Mock remoto non disponibile: ${it::class.simpleName}",
                fetchedAt = "Aggiornato dal fallback alle ${currentTimeLabel()}"
            )
        }
    }
}

/**
 * CRC Card - HomeRemoteModel
 *
 * Responsabilita':
 * - Modellare la risposta tecnica proveniente dal remote data source.
 *
 * Serve a:
 * - Tenere separato il formato remoto dal modello di dominio.
 *
 * Collabora con:
 * - `HomeRemoteDataSource` che lo crea.
 * - `HomeRepositoryImpl` che lo converte per il local/domain.
 */
data class HomeRemoteModel(
    val welcomeMessage: String,
    val serverStatus: String,
    val fetchedAt: String
)

private fun HomeRemoteDto.toModel(): HomeRemoteModel {
    return HomeRemoteModel(
        welcomeMessage = welcomeMessage,
        serverStatus = serverStatus,
        fetchedAt = fetchedAt
    )
}

private fun currentTimeLabel(): String {
    return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
}
