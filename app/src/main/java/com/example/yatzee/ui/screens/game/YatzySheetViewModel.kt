package com.example.yatzee.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yatzee.YatzyApplication
import com.example.yatzee.YatzyGame
import com.example.yatzee.checkLowerSection
import com.example.yatzee.checkUpperSection
import com.example.yatzee.data.repository.HighscoreRepository
import com.example.yatzee.data.repository.ScoresRepository
import com.example.yatzee.models.Dice
import com.example.yatzee.models.Highscore
import com.example.yatzee.models.Score
import com.example.yatzee.models.YatzyScoreType
import com.example.yatzee.throwDice
import com.example.yatzee.ui.screens.menu.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class YatzySheetUiState(
    val viewState: ViewState = ViewState.Idle,
    val numberOfThrows: Int = 3,
    val playerTurn: String = YatzyGame.playerTurn,
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
        val dices = uiState.value.dices
        val dicesAfterThrow = mutableListOf<Dice>()

        for (i in dices.indices) {
            if (dices[i].isLocked) {
                dicesAfterThrow.add(dices[i])
            } else {
                dicesAfterThrow.add(Dice(throwDice()))
            }
        }

        _uiState.update {
            it.copy(
                dices = dicesAfterThrow,
                numberOfThrows = it.numberOfThrows - 1
            )
        }

        checkPossibleOutcomes(dicesAfterThrow)
        checkPossibleToStroke()
    }

    fun changeLockedState(index: Int) {
        val dices = mutableListOf<Dice>()
        dices.addAll(uiState.value.dices)
        dices[index] = dices[index].copy(isLocked = !dices[index].isLocked)

        _uiState.update {
            it.copy(
                dices = dices
            )
        }
    }

    fun completeTurn(score: Score) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { scoresRepository.registerScore(score) }
        }
        nextTurn()
    }

    fun resetGame() {
        viewModelScope.launch {
            YatzyGame.resetGame()
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
        YatzyGame.nextPlayer()
        _uiState.update {
            it.copy(
                numberOfThrows = 3,
                playerTurn = YatzyGame.playerTurn,
                possibleOutcomes = mapOf(),
                dices = listOf(Dice(1), Dice(2), Dice(3), Dice(4), Dice(5)),
                turn = YatzyGame.turn
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
                    application.container.highscoreRepository
                )
            }
        }
    }

}
