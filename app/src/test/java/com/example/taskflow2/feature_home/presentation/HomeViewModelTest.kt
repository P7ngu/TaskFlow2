package com.example.taskflow2.feature_home.presentation

import com.example.taskflow2.feature_home.domain.model.HomeInfo
import com.example.taskflow2.feature_home.domain.repository.HomeRepository
import com.example.taskflow2.feature_home.domain.usecase.ObserveHomeInfoUseCase
import com.example.taskflow2.feature_home.domain.usecase.RefreshHomeInfoUseCase
import com.example.taskflow2.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `constructor injected dependencies make Hilt viewmodel testable without Hilt runtime`() = runTest {
        val repository = FakeHomeRepository()

        val viewModel = HomeViewModel(
            observeHomeInfoUseCase = ObserveHomeInfoUseCase(repository),
            refreshHomeInfoUseCase = RefreshHomeInfoUseCase(repository),
            refreshSession = HomeRefreshSession()
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isRefreshing)
        assertEquals("Dati remoti caricati dal fake repository", uiState.welcomeMessage)
        assertEquals(1, repository.refreshCalls)
    }

    @Test
    fun `refresh event triggers repository again without touching Android UI objects`() = runTest {
        val repository = FakeHomeRepository()
        val viewModel = HomeViewModel(
            observeHomeInfoUseCase = ObserveHomeInfoUseCase(repository),
            refreshHomeInfoUseCase = RefreshHomeInfoUseCase(repository),
            refreshSession = HomeRefreshSession()
        )

        advanceUntilIdle()
        viewModel.onEvent(HomeUiEvent.RefreshClicked)
        advanceUntilIdle()

        assertEquals(2, repository.refreshCalls)
        assertEquals("Sync numero 2", viewModel.uiState.value.lastSyncLabel)
    }
}

private class FakeHomeRepository : HomeRepository {
    private val homeState = MutableStateFlow<HomeInfo?>(null)
    var refreshCalls: Int = 0
        private set

    override fun observeHomeInfo(): Flow<HomeInfo?> = homeState

    override suspend fun refreshHomeInfo() {
        refreshCalls += 1
        homeState.value = HomeInfo(
            welcomeMessage = "Dati remoti caricati dal fake repository",
            serverStatus = "Fake backend stabile",
            lastSyncLabel = "Sync numero $refreshCalls"
        )
    }
}
