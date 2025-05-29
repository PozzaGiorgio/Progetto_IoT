package com.example.progettoiot.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettoiot.network.MoistureResponse
import com.example.progettoiot.repository.GardenRepository
import com.example.progettoiot.repository.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GardenUiState(
    val moistureData: MoistureResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPumpActivating: Boolean = false,
    val showSuccessMessage: Boolean = false,
    val lastUpdateTime: Long = 0L
)

class GardenViewModel(
    private val repository: GardenRepository = GardenRepository()
) : ViewModel() {

    private val _uiState = mutableStateOf(GardenUiState())
    val uiState: State<GardenUiState> = _uiState

    init {
        refreshMoistureStatus()
        startPeriodicRefresh()
    }

    fun refreshMoistureStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = repository.getMoistureStatus()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        moistureData = result.data,
                        isLoading = false,
                        lastUpdateTime = System.currentTimeMillis()
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun activatePump() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPumpActivating = true, errorMessage = null)

            when (val result = repository.activatePump()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isPumpActivating = false,
                        showSuccessMessage = true
                    )
                    // Nascondi il messaggio di successo dopo 3 secondi
                    delay(3000)
                    _uiState.value = _uiState.value.copy(showSuccessMessage = false)
                    // Aggiorna lo stato dopo l'attivazione
                    delay(2000)
                    refreshMoistureStatus()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isPumpActivating = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isPumpActivating = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30000) // Aggiorna ogni 30 secondi
                if (!_uiState.value.isLoading && !_uiState.value.isPumpActivating) {
                    refreshMoistureStatus()
                }
            }
        }
    }
}