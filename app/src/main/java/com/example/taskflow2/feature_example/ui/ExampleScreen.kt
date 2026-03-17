package com.example.taskflow2.feature_example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
 * una schermata statica non deve introdurre stato, ViewModel o callback se
 * non ne ha davvero bisogno. Tenere il placeholder semplice aiuta a capire
 * meglio cosa appartiene alla UI e cosa invece appartiene alla navigazione.
 */
@Composable
fun ExampleScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
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
            // Non aggiungiamo bottoni di navigazione alla UI: il back di sistema
            // torna automaticamente alla schermata precedente nello stack.
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
        ExampleScreen()
    }
}
