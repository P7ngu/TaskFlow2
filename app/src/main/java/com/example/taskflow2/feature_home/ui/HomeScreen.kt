package com.example.taskflow2.feature_home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflow2.feature_home.presentation.HomeUiEvent
import com.example.taskflow2.feature_home.presentation.HomeUiState
import com.example.taskflow2.ui.theme.TaskFlow2Theme

/**
 * CRC Card - HomeScreen
 *
 * Responsabilita':
 * - Disegnare la UI della feature Home a partire da `HomeUiState`.
 * - Tradurre il click di refresh in un `HomeUiEvent`.
 *
 * Serve a:
 * - Dimostrare una schermata Compose indipendente dal layer data.
 *
 * Collabora con:
 * - `HomeViewModel` indirettamente tramite stato ed eventi.
 * - `HomeUiState` e `HomeUiEvent` come contratto della schermata.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Feature Home",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Esempio di feature verticale con Hilt, Retrofit, cache locale, repository, use case e ViewModel dedicati.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when {
                uiState.isLoading -> {
                    Text(
                        text = "Caricamento home...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    Text(
                        text = uiState.welcomeMessage,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = uiState.serverStatus,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = uiState.lastSyncLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onEvent(HomeUiEvent.RefreshClicked) },
                    enabled = !uiState.isRefreshing
                ) {
                    Text(
                        text = if (uiState.isRefreshing) "Aggiornamento..." else "Refresh remoto"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TaskFlow2Theme {
        HomeScreen(
            uiState = HomeUiState(
                welcomeMessage = "Bentornato dalla cache",
                serverStatus = "Server remoto operativo",
                lastSyncLabel = "Ultimo sync alle 10:15:00",
                isLoading = false,
                isRefreshing = false
            ),
            onEvent = {}
        )
    }
}
