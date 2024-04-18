package com.example.yatzy.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yatzy.GameState
import com.example.yatzy.YatzyApplication
import com.example.yatzy.checkLowerSection
import com.example.yatzy.checkUpperSection
import com.example.yatzy.data.DicePool
import com.example.yatzy.data.repository.HighscoreRepository
import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.domain.RegisterScoreUseCase
import com.example.yatzy.models.Dice
import com.example.yatzy.models.Highscore
import com.example.yatzy.models.Score
import com.example.yatzy.models.YatzyScoreType
import com.example.yatzy.ui.screens.menu.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class YatzySheetUiState(
    val viewState: ViewState = ViewState.Idle,
    val numberOfThrows: Int = 3,
    val playerTurn: String = GameState.playerTurn,
    val scores: Map<String, List<Score>> = mapOf(),
    val possibleOutcomes: Map<YatzyScoreType, Int> = mapOf(),
    val possibleStrokes: Map<YatzyScoreType, Int> = mapOf(),
    val dices: List<Dice> = listOf(Dice(1), Dice(2), Dice(3), Dice(4), Dice(5)),
    val turn: Int = 1,
    val highscore: Int = 0,
    val winner: String = ""
)

class YatzySheetViewModel(
    private val scoresRepository: ScoresRepository,
    private val highscoreRepository: HighscoreRepository,
    private val registerScoreUseCase: RegisterScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(YatzySheetUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getScores()
    }

    private fun getScores() {
        viewModelScope.launch {
            scoresRepository.getPlayerScores().collect { playerScores ->
                val scoreBoard = playerScores.map { it.playerName }.associateWith { player ->
                    playerScores.filter { it.playerName == player }
                }
                val highScore =
                    playerScores.filter { it.type == YatzyScoreType.Sum }.maxByOrNull { it.value }
                val winner = highScore?.playerName

                _uiState.update {
                    it.copy(
                        scores = scoreBoard,
                        highscore = highScore?.value ?: 0,
                        winner = winner ?: "Unknown"
                    )
                }
            }
        }
    }

    fun throwDices() {
        val dicesAfterThrow = DicePool.throwDices()

        _uiState.update {
            it.copy(
                dices = dicesAfterThrow,
                numberOfThrows = it.numberOfThrows - 1
            )
        }

        checkPossibleOutcomes(dicesAfterThrow)
        checkPossibleToStroke()
    }

    fun lockDice(index: Int) {
        _uiState.update {
            it.copy(
                dices = DicePool.lockDice(index)
            )
        }
    }

    fun completeTurn(score: Score) {
        viewModelScope.launch { registerScoreUseCase(score) }
        nextTurn()
    }

    fun resetGame() {
        viewModelScope.launch {
            GameState.resetGame()
            withContext(Dispatchers.IO) {
                uiState.value.scores.map {
                    it.key to it.value.find { it.type == YatzyScoreType.Sum }
                }.forEach {
                    highscoreRepository.addHighscore(
                        Highscore(playerName = it.first, score = it.second?.value ?: 0)
                    )
                }
            }
            scoresRepository.clearScores()
        }
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

    private fun checkPossibleOutcomes(dices: List<Dice>) {
        val possibleOutcomes: MutableMap<YatzyScoreType, Int> = mutableMapOf()
        possibleOutcomes.putAll(dices.checkUpperSection())
        possibleOutcomes.putAll(dices.checkLowerSection())

        _uiState.update { currentUiState ->
            currentUiState.copy(
                possibleOutcomes = possibleOutcomes.filter { scores ->
                    scores.key in uiState.value.scores[uiState.value.playerTurn]!!.filter { score -> score.value == 0 }
                        .map { it.type }
                }
            )
        }
    }

    private fun checkPossibleToStroke() {
        uiState.value.scores[uiState.value.playerTurn]?.let { playerScore ->
            val tempList: MutableMap<YatzyScoreType, Score> =
                playerScore.associateBy { it.type }.toMutableMap()
            tempList.remove(YatzyScoreType.Bonus)
            tempList.remove(YatzyScoreType.UpperSum)
            tempList.remove(YatzyScoreType.Sum)

            val possibleToStroke = tempList.filter { !it.value.isStroke && it.value.value == 0 }
                .map { it.key to it.value.value }.toMap()

            _uiState.update { currentUiState ->
                currentUiState.copy(
                    possibleStrokes = possibleToStroke
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as YatzyApplication

                YatzySheetViewModel(
                    application.container.scoresRepository,
                    application.container.highscoreRepository,
                    application.container.registerScoreUseCase
                )
            }
        }
    }

}
