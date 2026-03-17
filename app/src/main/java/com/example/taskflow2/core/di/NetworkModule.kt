package com.example.taskflow2.core.di

import com.example.taskflow2.core.network.TeachingMockInterceptor
import com.example.taskflow2.feature_home.data.remote.HomeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Modulo Hilt che espone lo stack di rete condiviso.
 *
 * L'esempio usa Retrofit davvero, ma il traffico HTTP viene intercettato
 * da un mock locale così il progetto resta didattico e subito eseguibile.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        teachingMockInterceptor: TeachingMockInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(teachingMockInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://taskflow.didactic/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideHomeApiService(retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }
}
