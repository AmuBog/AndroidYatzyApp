package com.example.yatzee.ui.screens.highscore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yatzee.YatzyApplication
import com.example.yatzee.data.repository.HighscoreRepository
import com.example.yatzee.models.Highscore
import com.example.yatzee.ui.screens.menu.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HighscoreUiState(
    val viewState: ViewState = ViewState.Idle,
    val highscores: List<Highscore> = listOf()
)

class HighscoreViewModel(private val repository: HighscoreRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HighscoreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getHighscores()
    }

    private fun getHighscores() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.update {
                    it.copy(
                        highscores = repository.getHighscores()
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val yatzyApplication = this[APPLICATION_KEY] as YatzyApplication
                HighscoreViewModel(yatzyApplication.container.highscoreRepository)
            }
        }
    }
}
