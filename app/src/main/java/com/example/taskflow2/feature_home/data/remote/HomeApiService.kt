package com.example.taskflow2.feature_home.data.remote

import retrofit2.http.GET

/**
 * Contratto Retrofit della feature Home.
 *
 * In un progetto reale questo file descriverebbe endpoint, query param e DTO
 * della API remota. Qui lo teniamo minimale per mostrare il ruolo di Retrofit.
 */
interface HomeApiService {

    @GET("teaching/home")
    suspend fun fetchHomeInfo(): HomeRemoteDto
}

data class HomeRemoteDto(
    val welcomeMessage: String,
    val serverStatus: String,
    val fetchedAt: String
)
