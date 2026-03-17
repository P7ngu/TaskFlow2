package com.example.taskflow2.feature_todo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow2.feature_todo.domain.model.Todo
import com.example.taskflow2.feature_todo.domain.usecase.AddTodoUseCase
import com.example.taskflow2.feature_todo.domain.usecase.GetTodosUseCase
import com.example.taskflow2.feature_todo.domain.usecase.ToggleTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * CRC Card - TodoViewModel
 *
 * Responsabilita':
 * - Gestire lo stato UI della schermata TODO.
 * - Ricevere eventi utente e orchestrarne l'esecuzione tramite i use case.
 *
 * Serve a:
 * - Tenere separati presentation e business logic.
 * - Esportare uno `StateFlow` osservabile dalla UI.
 * - Restare concentrato sul comportamento della schermata, senza contenere wiring DI.
 *
 * Collabora con:
 * - `GetTodosUseCase`, `AddTodoUseCase`, `ToggleTodoUseCase` per il comportamento applicativo.
 * - `TodoUiState` e `TodoUiEvent` come contratto della UI.
 * - `TodoScreen` che osserva `uiState` e invia eventi.
 */
class TodoViewModel(
    private val getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        observeTodos()
    }

    fun onEvent(event: TodoUiEvent) {
        when (event) {
            is TodoUiEvent.InputChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(inputTitle = event.value)
                }
            }

            TodoUiEvent.AddClicked -> addTodo()
            is TodoUiEvent.ToggleClicked -> toggleTodo(event.todo)
        }
    }

    private fun observeTodos() {
        viewModelScope.launch {
            // `collect` resta in ascolto del `Flow`.
            // Non sta facendo pull continuo dei dati: il repository emette solo
            // quando la lista cambia, e il ViewModel reagisce aggiornando la UI.
            getTodosUseCase().collect { todos ->
                _uiState.update { currentState ->
                    currentState.copy(
                        todos = todos,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun addTodo() {
        val currentTitle = uiState.value.inputTitle

        viewModelScope.launch {
            // Il ViewModel NON conosce da dove arrivano i dati.
            // Non valida il titolo, non genera ID e non decide regole:
            // delega tutto al caso d'uso e si limita a reagire al risultato.
            val hasBeenAdded = addTodoUseCase(currentTitle)

            if (hasBeenAdded) {
                _uiState.update { currentState ->
                    currentState.copy(inputTitle = "")
                }
            }
        }
    }

    private fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            toggleTodoUseCase(todo)
        }
    }
}
