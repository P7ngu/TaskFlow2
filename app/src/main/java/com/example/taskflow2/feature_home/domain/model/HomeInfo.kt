package com.example.taskflow2.feature_home.domain.model

/**
 * CRC Card - HomeInfo
 *
 * Responsabilita':
 * - Rappresentare il dato di dominio mostrato nella feature Home.
 *
 * Serve a:
 * - Offrire un modello pulito e indipendente da cache locale o risposta remota.
 *
 * Collabora con:
 * - `HomeRepository` e use case della feature.
 * - `HomeUiState` e `HomeScreen` che ne mostrano il contenuto.
 */
data class HomeInfo(
    val welcomeMessage: String,
    val serverStatus: String,
    val lastSyncLabel: String
)
