package com.example.taskflow2.samples.hilt

import android.content.Context
import com.example.taskflow2.core.di.AppPackageName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Esempio didattico di uso di `@ApplicationContext`.
 *
 * Qui usiamo il context dell'app, non quello di una Activity:
 * - e' sicuro da tenere in `@Singleton`
 * - non rischia di trattenere schermate distrutte
 *
 * In piu' usiamo `@AppPackageName`, un qualifier custom, per distinguere
 * esplicitamente quale `String` vogliamo ricevere dal grafo Hilt.
 */
@Singleton
class AppIdentityProvider @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @AppPackageName private val packageName: String
) {
    fun identityLabel(): String {
        return "${applicationContext.applicationInfo.className}::$packageName"
    }
}
