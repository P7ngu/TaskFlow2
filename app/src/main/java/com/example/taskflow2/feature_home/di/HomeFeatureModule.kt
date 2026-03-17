package com.example.taskflow2.feature_home.di

import com.example.taskflow2.feature_home.data.repository.HomeRepositoryImpl
import com.example.taskflow2.feature_home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * CRC Card - HomeFeatureModule
 *
 * Responsabilita':
 * - Dichiarare a Hilt come collegare l'interfaccia `HomeRepository`
 *   alla sua implementazione concreta.
 *
 * Serve a:
 * - Mostrare il passaggio dalla dependency injection manuale a Hilt.
 *
 * Collabora con:
 * - `HomeRepositoryImpl` come implementazione concreta.
 * - `HomeViewModel` e i use case che chiedono `HomeRepository`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HomeFeatureModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        implementation: HomeRepositoryImpl
    ): HomeRepository
}
