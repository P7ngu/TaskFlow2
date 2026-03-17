package com.example.taskflow2.core.network

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Interceptor pensato per spiegare Retrofit senza introdurre un backend reale.
 *
 * Se la request corrisponde all'endpoint didattico della Home, restituisce
 * un JSON finto ma realistico. In questo modo il data source continua a usare
 * `HomeApiService` come in un'app vera.
 */
@Singleton
class TeachingMockInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath == "/teaching/home") {
            val jsonBody = """
                {
                  "welcomeMessage": "Dati remoti caricati con Retrofit",
                  "serverStatus": "Risposta mockata via OkHttp Interceptor",
                  "fetchedAt": "Aggiornato dal remote alle ${currentTimeLabel()}"
                }
            """.trimIndent()

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(jsonBody.toResponseBody("application/json".toMediaType()))
                .addHeader("content-type", "application/json")
                .build()
        }

        return chain.proceed(request)
    }
}

private fun currentTimeLabel(): String {
    return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
}
