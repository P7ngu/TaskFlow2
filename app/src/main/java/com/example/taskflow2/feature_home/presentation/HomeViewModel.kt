package com.example.taskflow2.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow2.feature_home.domain.usecase.ObserveHomeInfoUseCase
import com.example.taskflow2.feature_home.domain.usecase.RefreshHomeInfoUseCase
import com.example.taskflow2.samples.hilt.HomeRefreshSession
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CRC Card - HomeViewModel
 *
 * Responsabilita':
 * - Gestire lo stato UI della feature Home.
 * - Osservare il flusso dei dati e reagire agli eventi della schermata.
 *
 * Serve a:
 * - Tenere la UI scollegata dai dettagli di local/remote.
 * - Restare focalizzato su stato ed eventi, non sulla creazione delle dipendenze.
 *
 * Collabora con:
 * - `ObserveHomeInfoUseCase` e `RefreshHomeInfoUseCase`.
 * - `HomeUiState` e `HomeUiEvent`.
 * - `HomeScreen` che osserva lo stato e invia eventi.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeHomeInfoUseCase: ObserveHomeInfoUseCase,
    private val refreshHomeInfoUseCase: RefreshHomeInfoUseCase,
    private val refreshSession: HomeRefreshSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeHomeInfo()
        refresh()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshClicked -> refresh()
        }
    }

    private fun observeHomeInfo() {
        viewModelScope.launch {
            // Questo `collect` non fa polling.
            // La cache locale emette un nuovo valore solo quando cambia, e la UI
            // si riallinea da sola tramite `uiState`.
            observeHomeInfoUseCase().collect { homeInfo ->
                _uiState.update { currentState ->
                    currentState.copy(
                        welcomeMessage = homeInfo?.welcomeMessage.orEmpty(),
                        serverStatus = homeInfo?.serverStatus.orEmpty(),
                        lastSyncLabel = homeInfo?.lastSyncLabel.orEmpty(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isRefreshing = true,
                    errorMessage = null
                )
            }

            // `HomeRefreshSession` e' `@ViewModelScoped`:
            // il suo stato appartiene a questo ViewModel e non deve essere
            // condiviso globalmente. Usare `@Singleton` qui porterebbe a uno
            // state leak tra schermate o istanze diverse del ViewModel.
            refreshSession.markRefreshStarted()

            try {
                refreshHomeInfoUseCase()
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                // `try/catch` e' il posto giusto quando vogliamo gestire
                // localmente l'errore e trasformarlo in stato UI.
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = exception.message ?: "Errore durante il refresh"
                    )
                }
            } finally {
                _uiState.update { currentState ->
                    currentState.copy(isRefreshing = false)
                }
            }
        }
    }
}
