package com.example.taskflow2.feature_example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflow2.ui.theme.TaskFlow2Theme

/**
 * Schermata finale di esempio.
 *
 * E' volutamente minimale: mostra solo `helloworld` per far vedere il flusso
 * completo "lista in home -> apertura pagina -> ritorno con back".
 *
 * Best practice mostrata qui:
 * una schermata di dettaglio puo' restare molto semplice ma beneficia di
 * un'azione di ritorno visibile in alto a sinistra.
 *
 * Questo aiuta chi studia a vedere due cose:
 * - la UI espone un `onBack`
 * - il livello superiore decide che `onBack` corrisponde a `navigateUp()`
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            // Best practice per una schermata di dettaglio:
            // mostrare un'azione di ritorno esplicita nella top bar oltre al
            // back di sistema. Usiamo un `TextButton` per mantenere
            // l'esempio semplice e senza dipendenze aggiuntive.
            TopAppBar(
                title = {
                    Text("Esempio")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            // Il contenuto e' centrato proprio per sottolineare che questa
            // pagina e' soltanto un placeholder didattico da sostituire o
            // estendere con contenuti reali.
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Il contenuto resta minimale: la navigazione visibile e' raccolta
            // nella top bar per rendere piu' chiaro il pattern di dettaglio.
            Text(
                text = "helloworld",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExampleScreenPreview() {
    TaskFlow2Theme {
        ExampleScreen(onBack = {})
    }
}
