package com.example.yatzy.ui.screens.highscore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yatzy.data.repository.HighscoreRepository
import com.example.yatzy.models.Highscore
import com.example.yatzy.ui.screens.menu.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HighscoreUiState(
    val viewState: ViewState = ViewState.Idle,
    val highscores: List<Highscore> = listOf()
)

@HiltViewModel
class HighscoreViewModel @Inject constructor(private val repository: HighscoreRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(HighscoreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getHighscores()
    }

    private fun getHighscores() = viewModelScope.launch {
        _uiState.update { it.copy(highscores = repository.getHighscores()) }
    }

}
