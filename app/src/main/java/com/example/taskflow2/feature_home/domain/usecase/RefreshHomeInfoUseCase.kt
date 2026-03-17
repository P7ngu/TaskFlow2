package com.example.taskflow2.feature_home.domain.usecase

import com.example.taskflow2.feature_home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * CRC Card - RefreshHomeInfoUseCase
 *
 * Responsabilita':
 * - Rappresentare l'azione di dominio che chiede un aggiornamento dei dati Home.
 *
 * Serve a:
 * - Tenere il ViewModel lontano dalla logica di coordinamento con il repository.
 *
 * Collabora con:
 * - `HomeRepository` che si occupa del refresh tecnico.
 * - `HomeViewModel` che invoca questo use case in risposta alla UI.
 */
class RefreshHomeInfoUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke() {
        // ✅ Il caso d'uso rappresenta un'azione del dominio dell'app:
        // "aggiorna i dati della home".
        // Il repository esegue il coordinamento tecnico tra cache locale e remoto.
        repository.refreshHomeInfo()
    }
}
