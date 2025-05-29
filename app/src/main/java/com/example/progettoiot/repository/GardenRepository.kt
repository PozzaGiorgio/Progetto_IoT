package com.example.progettoiot.repository

import com.example.progettoiot.network.ApiClient
import com.example.progettoiot.network.MoistureResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error<out T>(val message: String) : Result<T>()
    data object Loading : Result<Nothing>()
}

class GardenRepository {

    suspend fun getMoistureStatus(): Result<MoistureResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.service.getMoistureStatus()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Risposta vuota dal server")
            } else {
                when (response.code()) {
                    404 -> Result.Error("Nessun dato disponibile")
                    else -> Result.Error("Errore del server: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Result.Error("Errore di connessione: ${e.localizedMessage}")
        }
    }

    suspend fun activatePump(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.service.activatePump()
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Errore nell'attivazione della pompa: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Errore di connessione: ${e.localizedMessage}")
        }
    }
}