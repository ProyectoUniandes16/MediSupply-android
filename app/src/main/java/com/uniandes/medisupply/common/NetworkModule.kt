package com.uniandes.medisupply.common

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "http://192.168.1.77"
    private val userDataProvider: UserDataProvider by inject(UserDataProvider::class.java)

    private class LoggingInterceptor(private val tag: String = "Network") : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            // Log request
            try {
                val requestBody = request.body
                val requestBodyString = if (requestBody != null) {
                    val buffer = Buffer()
                    requestBody.writeTo(buffer)
                    buffer.readUtf8()
                } else null

                Log.d(tag, "---> ${request.method} ${request.url}\nHeaders: ${request.headers}\nBody: $requestBodyString")
            } catch (e: Exception) {
                Log.d(tag, "Request logging failed: ${e.message}")
            }

            val startNs = System.nanoTime()
            val response: Response = try {
                chain.proceed(request)
            } catch (e: Exception) {
                val tookMs = (System.nanoTime() - startNs) / 1_000_000
                Log.e(tag, "HTTP request failed after ${tookMs}ms: ${e::class.java.simpleName} ${e.message}", e)
                throw e
            }
            val tookMs = (System.nanoTime() - startNs) / 1_000_000

            // Log response (peek body so we don't consume it)
            try {
                val peek = response.peekBody(1024L * 64) // limit to 64KB
                Log.d(tag, "<--- ${response.code} ${response.request.url} (${tookMs}ms)\nHeaders: ${response.headers}\nBody: ${peek.string()}")
            } catch (e: Exception) {
                Log.d(tag, "Response logging failed: ${e.message}")
            }

            return response
        }
    }

    private fun getOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private fun getRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient().build())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private fun getAuthRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        Log.i("NetworkModule", "Creating Auth Retrofit with token: ${userDataProvider.getAccessToken()}")
        val client = getOkHttpClient()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer ${userDataProvider.getAccessToken()}") // Replace with actual token retrieval
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T = getRetrofit().create(serviceClass)
    fun <T> createAuthService(serviceClass: Class<T>): T = getAuthRetrofit().create(serviceClass)
}
