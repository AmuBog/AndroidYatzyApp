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
import com.example.yatzee.data.database.YatzyDatabase
import com.example.yatzee.models.Dice
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

class YatzySheetViewModel(private val db: YatzyDatabase) : ViewModel() {

    private val _uiState = MutableStateFlow(YatzySheetUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            db.scoreDao().getPlayerScores().collect { playerScores ->
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
        val dices = _uiState.value.dices
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
        dices.addAll(_uiState.value.dices)
        dices[index] = dices[index].copy(isLocked = !dices[index].isLocked)

        _uiState.update {
            it.copy(
                dices = dices
            )
        }
    }

    fun completeTurn(type: YatzyScoreType, score: Int?) {
        registerScore(type, score)
        startNextTurn()
    }

    fun resetGame() {
        YatzyGame.resetGame()
    }

    private fun registerScore(type: YatzyScoreType, score: Int?) {
        val player = YatzyGame.playerTurn

        if (score == null) {
            addScore(player, type, null)
            return
        }

        uiState.value.scores[player]?.let { playerScore ->
            var upperSum = playerScore.find { it.type == YatzyScoreType.UpperSum }?.value ?: 0
            var totalSum = playerScore.find { it.type == YatzyScoreType.Sum }?.value ?: 0
            upperSum += if (type.isUpperHalf()) score else 0
            totalSum += score

            val bonus: Int =
                if (playerScore.find { it.type == YatzyScoreType.Bonus }?.value == 50) 0
                else if (upperSum >= 63) 50
                else 0

            if (type.isUpperHalf()) {
                addScore(player, YatzyScoreType.UpperSum, upperSum)
            }

            if (bonus == 50) {
                addScore(player, YatzyScoreType.Bonus, bonus)
            }

            addScore(player, type, score)
            addScore(player, YatzyScoreType.Sum, totalSum + bonus)
        }

    }

    private fun startNextTurn() {
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
                    scores.key in uiState.value.scores[_uiState.value.playerTurn]!!.filter { score -> score.value == 0 }
                        .map { it.type }
                }
            )
        }
    }

    private fun checkPossibleToStroke() {
        uiState.value.scores[_uiState.value.playerTurn]?.let { playerScore ->
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

    private fun addScore(player: String, type: YatzyScoreType, score: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val element = db.scoreDao().getSpecificScore(player, type)
                db.scoreDao().addPlayerScore(
                    Score(
                        id = element.id,
                        playerName = player,
                        type = type,
                        value = score ?: 0,
                        isStroke = score == null
                    )
                )
            }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as YatzyApplication

                YatzySheetViewModel(db = YatzyDatabase.getInstance(application))
            }
        }
    }

}
