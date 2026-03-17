package com.example.taskflow2.core.di

import javax.inject.Qualifier

/**
 * Qualifier custom per la base URL didattica.
 *
 * Qui la usiamo per mostrare un caso reale: una `String` semplice non basta a
 * raccontare a Hilt quale significato abbia quel valore dentro il grafo.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TeachingBaseUrl

/**
 * Qualifier custom per distinguere l'OkHttpClient usato dall'esempio didattico
 * da eventuali altri client futuri.
 *
 * Anche se oggi c'e' un solo client, introdurre un qualifier qui mostra bene
 * la regola: quando lo stesso tipo puo' esistere piu' volte, qualificalo.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TeachingHttpClient
