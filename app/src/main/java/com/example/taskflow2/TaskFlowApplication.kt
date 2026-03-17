package com.example.taskflow2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Entry point dell'app per Hilt.
 *
 * Da qui Hilt prepara il grafo globale delle dipendenze che poi useremo
 * in Activity, ViewModel, repository e data source.
 */
@HiltAndroidApp
class TaskFlowApplication : Application()
