package com.example.taskflow2.feature_todo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.presentation.TodoUiEvent
import com.example.taskflow2.feature_todo.presentation.TodoUiState
import com.example.taskflow2.ui.theme.TaskFlow20Theme

/**
 * CRC Card - TodoScreen
 *
 * Responsabilita':
 * - Renderizzare la schermata TODO in base a `TodoUiState`.
 * - Convertire le interazioni dell'utente in `TodoUiEvent`.
 *
 * Serve a:
 * - Mantenere la UI dichiarativa e priva di accesso diretto al layer data.
 *
 * Collabora con:
 * - `TodoViewModel` indirettamente, tramite stato ed eventi.
 * - `TodoItem` per la visualizzazione di ogni elemento della lista.
 * - Eventuale `headerContent` per comporre altre feature senza accoppiarle al data layer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    uiState: TodoUiState,
    onEvent: (TodoUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    headerContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            headerContent?.invoke(this)

            if (headerContent != null) {
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(
                text = "TODO App didattica",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "UDF: la UI invia eventi, il ViewModel aggiorna lo StateFlow e Compose ridisegna lo schermo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.inputTitle,
                    onValueChange = { onEvent(TodoUiEvent.InputChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Nuova attivita'") },
                    singleLine = true
                )

                Button(
                    onClick = { onEvent(TodoUiEvent.AddClicked) }
                ) {
                    Text("Aggiungi")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.isLoading) {
                Text(
                    text = "Caricamento TODO...",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.todos,
                        key = { todo -> todo.id }
                    ) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggle = { onEvent(TodoUiEvent.ToggleClicked(todo)) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * CRC Card - TodoItem
 *
 * Responsabilita':
 * - Mostrare un singolo TODO e la sua azione di toggle.
 *
 * Serve a:
 * - Tenere la UI piu' modulare e riusabile.
 *
 * Collabora con:
 * - `Todo` come dato da rappresentare.
 * - `TodoScreen` che gli passa callback e contenuto.
 */
@Composable
private fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = todo.completed,
                onCheckedChange = { onToggle() }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (todo.completed) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )
                Text(
                    text = if (todo.completed) "Completata" else "Da fare",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoScreenPreview() {
    TaskFlow20Theme {
        TodoScreen(
            uiState = TodoUiState(
                todos = listOf(
                    Todo(id = 1L, title = "Studiare StateFlow", completed = false),
                    Todo(id = 2L, title = "Capire i UseCase", completed = true)
                ),
                inputTitle = "Scrivere una nota",
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
