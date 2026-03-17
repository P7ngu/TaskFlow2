package com.example.taskflow2.feature_home.domain.usecase

import com.example.taskflow2.feature_home.domain.model.HomeInfo
import com.example.taskflow2.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * CRC Card - ObserveHomeInfoUseCase
 *
 * Responsabilita':
 * - Esporre al presentation layer il flusso dei dati Home.
 *
 * Serve a:
 * - Incapsulare l'azione di dominio "osserva le informazioni della home".
 *
 * Collabora con:
 * - `HomeRepository` da cui ottiene i dati.
 * - `HomeViewModel` che aggiorna lo stato UI osservando questo flusso.
 */
class ObserveHomeInfoUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    // Anche in una feature che usa dati remoti il dominio continua a parlare
    // solo con un repository astratto: non sa nulla di Retrofit, Room o Android.
    operator fun invoke(): Flow<HomeInfo?> = repository.observeHomeInfo()
}
