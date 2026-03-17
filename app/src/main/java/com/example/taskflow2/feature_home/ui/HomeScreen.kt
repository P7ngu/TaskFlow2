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

// Per l'esempio teniamo la configurazione del menu direttamente qui:
// la home mostra una piccola lista statica e notifica solo che l'utente
// vuole aprire la pagina di esempio.
//
// Best practice per un esempio piccolo:
// se i dati servono solo a questa schermata e non hanno logica propria,
// tenerli vicini alla UI puo' essere piu' leggibile che estrarli troppo presto.
private data class HomeMenuItem(
    val title: String,
    val description: String
)

private val homeMenuItems = listOf(
    HomeMenuItem(
        title = "Matteo",
        description = "Voce di esempio della lista home."
    ),
    HomeMenuItem(
        title = "Esempio",
        description = "Apre la pagina di esempio con contenuto minimale."
    )
    // Per aggiungere una nuova voce visuale nella lista:
    // - aggiungi qui un altro `HomeMenuItem(...)`
    // - poi estendi i parametri della schermata con una callback dedicata
    //   come `onOpenProfile`
    // - infine collega quella callback in `MainActivity`
)

/**
 * CRC Card - HomeScreen
 *
 * Responsabilita':
 * - Disegnare la UI della feature Home a partire da `HomeUiState`.
 * - Tradurre il click di refresh in un `HomeUiEvent`.
 * - Mostrare una lista di pagine custom cliccabili.
 *
 * Serve a:
 * - Dimostrare una schermata Compose indipendente dal layer data.
 * - Mostrare come aggiungere un menu semplice senza introdurre subito
 *   una soluzione di navigazione piu' pesante.
 *
 * Collabora con:
 * - `HomeViewModel` indirettamente tramite stato ed eventi.
 * - `HomeUiState` e `HomeUiEvent` come contratto della schermata.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    // La schermata non sa *come* si naviga, sa solo che deve chiedere
    // l'apertura dell'esempio. Questo mantiene la UI riusabile e piu' facile
    // da testare o da leggere in isolamento.
    //
    // Se aggiungi una nuova schermata, la best practice e' aggiungere qui
    // un'altra callback semantica, ad esempio `onOpenProfile: () -> Unit`,
    // invece di passare un `NavController` direttamente alla UI.
    onOpenExample: () -> Unit,
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

            Text(
                text = "Pagine custom",
                style = MaterialTheme.typography.titleMedium
            )

            // Renderizziamo una piccola lista statica di voci.
            // Per mantenere l'esempio semplice, ogni elemento apre la stessa
            // schermata `Esempio`.
            //
            // Best practice:
            // la UI emette intenti semplici (`onOpenExample`) invece di
            // chiamare direttamente `navController.navigate(...)`.
            //
            // Se vuoi far aprire schermate diverse:
            // - puoi aggiungere piu' callback (`onOpenProfile`, `onOpenSettings`, ...)
            // - oppure trasformare questo esempio in una lista con una callback
            //   piu' generica e un mapping nel livello superiore
            homeMenuItems.forEach { item ->
                Button(
                    onClick = onOpenExample,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Titolo principale della voce, utile a identificare
                        // rapidamente la destinazione dall'elenco.
                        Text(item.title)
                        Text(
                            // Descrizione secondaria: qui possiamo spiegare
                            // cosa fara' la schermata una volta aperta.
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
            onEvent = {},
            onOpenExample = {}
        )
    }
}
