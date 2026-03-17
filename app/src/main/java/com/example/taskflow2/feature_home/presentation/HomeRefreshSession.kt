package com.example.taskflow2.feature_home.presentation

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * Esempio didattico di dipendenza `@ViewModelScoped`.
 *
 * Tiene un piccolo stato tecnico che appartiene a un solo `HomeViewModel`.
 * Non deve essere `@Singleton`, altrimenti il contatore verrebbe condiviso tra
 * schermate o sessioni diverse, mescolando stato che invece appartiene a uno
 * specifico ViewModel.
 *
 * Regola pratica anti-leak / anti-state-leak:
 * - evita di usare `@Singleton` per stato temporaneo legato alla UI
 * - se uno stato deve vivere quanto un solo `ViewModel`, `@ViewModelScoped`
 *   e' spesso la scelta piu' corretta
 */
@ViewModelScoped
class HomeRefreshSession @Inject constructor() {
    private var refreshCount: Int = 0

    fun markRefreshStarted() {
        refreshCount += 1
    }

    fun currentRefreshCount(): Int = refreshCount
}
