package com.example.taskflow2.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Hilt per esempi legati ai qualifiers applicativi.
 *
 * Qui mostriamo due idee importanti:
 * - `@ApplicationContext` e' un qualifier built-in di Hilt
 * - i qualifier custom servono a distinguere dipendenze dello stesso tipo
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @AppPackageName
    fun provideAppPackageName(
        @ApplicationContext applicationContext: Context
    ): String {
        return applicationContext.packageName
    }
}
