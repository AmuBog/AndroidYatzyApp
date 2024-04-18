package com.example.yatzy.ui.screens.highscore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yatzy.YatzyApplication
import com.example.yatzy.data.repository.HighscoreRepository
import com.example.yatzy.models.Highscore
import com.example.yatzy.ui.screens.menu.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private fun getHighscores() = viewModelScope.launch {
        _uiState.update { it.copy(highscores = repository.getHighscores()) }
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
