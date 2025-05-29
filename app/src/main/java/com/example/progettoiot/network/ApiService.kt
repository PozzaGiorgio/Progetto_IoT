package com.example.progettoiot.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

data class MoistureResponse(
    val umidita_val: Int,
    val stato: String
)

data class ErrorResponse(
    val error: String
)

interface ApiService {
    @GET("/api/umidita")
    suspend fun getMoistureStatus(): Response<MoistureResponse>

    @POST("/api/pompa")
    suspend fun activatePump(): Response<Unit>
}

object ApiClient {
    val service: ApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(com.example.progettoiot.config.ApiConfig.BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                    })
                    .connectTimeout(com.example.progettoiot.config.ApiConfig.CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(com.example.progettoiot.config.ApiConfig.READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(com.example.progettoiot.config.ApiConfig.WRITE_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(ApiService::class.java)
    }
}