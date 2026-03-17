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
 *
 * Regola pratica:
 * - qui usiamo `@Provides` perche' OkHttpClient, Retrofit e HomeApiService
 *   vanno costruiti esplicitamente tramite builder o factory
 * - se avessimo solo un binding interfaccia -> implementazione con costruttore
 *   `@Inject`, allora sarebbe piu' adatto `@Binds`
 * - questi oggetti sono `@Singleton` perche' sono infrastruttura condivisa;
 *   non devono conoscere Activity, View o `ActivityContext`, altrimenti si
 *   rischierebbero memory leak o stato UI trattenuto troppo a lungo
 * - usiamo anche qualifier custom quando il semplice tipo (`String`,
 *   `OkHttpClient`) non basta a distinguere il significato della dipendenza
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @TeachingBaseUrl
    fun provideTeachingBaseUrl(): String {
        return "https://taskflow.didactic/"
    }

    @Provides
    @Singleton
    @TeachingHttpClient
    fun provideOkHttpClient(
        teachingMockInterceptor: TeachingMockInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(teachingMockInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @TeachingHttpClient okHttpClient: OkHttpClient,
        @TeachingBaseUrl baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
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
