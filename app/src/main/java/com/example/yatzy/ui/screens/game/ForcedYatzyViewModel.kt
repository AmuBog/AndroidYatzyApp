package com.example.yatzy.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yatzy.GameState
import com.example.yatzy.data.DicePool
import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.domain.CalculatePossibleScoresUseCase
import com.example.yatzy.domain.RegisterHighscoreUseCase
import com.example.yatzy.domain.RegisterScoreUseCase
import com.example.yatzy.models.DiceModel
import com.example.yatzy.models.Score
import com.example.yatzy.models.YatzyScoreType
import com.example.yatzy.ui.screens.menu.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForcedYatzyUiState(
    val viewState: ViewState = ViewState.Idle,
    val numberOfThrows: Int = 3,
    val playerTurn: String = GameState.playerTurn,
    val scores: Map<YatzyScoreType, List<Score>> = mapOf(),
    val possibleOutcomes: Map<YatzyScoreType, Int> = mapOf(),
    val dices: List<DiceModel> = listOf(
        DiceModel(1),
        DiceModel(2),
        DiceModel(3),
        DiceModel(4),
        DiceModel(5)
    ),
    val turn: Int = 1,
    val highscore: Int = 0,
    val winner: String = "",
    val finished: Boolean = false
)

@HiltViewModel
class ForcedYatzyViewModel @Inject constructor(
    private val scoresRepository: ScoresRepository,
    private val calculatePossibleScoresUseCase: CalculatePossibleScoresUseCase,
    private val registerScoreUseCase: RegisterScoreUseCase,
    private val registerHighscoreUseCase: RegisterHighscoreUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForcedYatzyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getScores()
    }

    private fun getScores() = viewModelScope.launch {
        scoresRepository.getScoreBoardBasedOnType().collect { scoreBoard ->
            val highScore = scoreBoard.flatMap { it.value }.maxByOrNull { it.value }

            _uiState.update {
                it.copy(
                    scores = scoreBoard,
                    highscore = highScore?.value ?: 0,
                    winner = highScore?.playerName ?: "Unknown"
                )
            }
        }
    }

    fun throwDices() = viewModelScope.launch {
        val player = uiState.value.playerTurn
        val dicesAfterThrow = DicePool.throwDices()

        _uiState.update {
            it.copy(
                dices = dicesAfterThrow,
                possibleOutcomes = calculatePossibleScoresUseCase(player, dicesAfterThrow),
                numberOfThrows = it.numberOfThrows - 1
            )
        }
    }

    fun lockDice(index: Int) {
        _uiState.update {
            it.copy(dices = DicePool.lockDice(index))
        }
    }

    fun completeTurn(score: Score) {
        viewModelScope.launch { registerScoreUseCase(score) }
        nextTurn()
    }

    fun resetGame() = viewModelScope.launch {
        GameState.resetGame()
        registerHighscoreUseCase()
        scoresRepository.clearScores()
    }

    private fun nextTurn() {
        GameState.nextPlayer()
        _uiState.update {
            it.copy(
                numberOfThrows = 3,
                playerTurn = GameState.playerTurn,
                possibleOutcomes = mapOf(),
                dices = DicePool.resetDices(),
                turn = GameState.turn
            )
        }
    }

    fun quitGame() = viewModelScope.launch {
        GameState.resetGame()
        scoresRepository.clearScores()
    }

    fun finishGame() {
        _uiState.update { it.copy(finished = true) }
    }

}