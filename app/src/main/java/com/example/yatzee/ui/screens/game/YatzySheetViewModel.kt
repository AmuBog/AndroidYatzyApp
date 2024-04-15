package com.example.yatzee.ui.screens.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yatzee.YatzyGame
import com.example.yatzee.checkLowerSection
import com.example.yatzee.checkUpperSection
import com.example.yatzee.data.YatzyDatabase
import com.example.yatzee.models.Dice
import com.example.yatzee.models.Score
import com.example.yatzee.models.YatzeeScoreType
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
    val scores: Map<String, Map<YatzeeScoreType, Int>> = mapOf(),
    val possibleOutcomes: Map<YatzeeScoreType, Int> = mapOf(),
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
                        .associate { it.type to it.value }
                }
                val highScore =
                    playerScores.filter { it.type == YatzeeScoreType.Sum }.maxByOrNull { it.value }
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

    fun registerScore(type: YatzeeScoreType, score: Int?) {
        val player = YatzyGame.playerTurn
        val scoreBoard = uiState.value.scores.map { it.key to it.value.toMutableMap() }.toMap().toMutableMap()
        var upperSum = 0
        var totalSum = 0

        addScore(player, type, score ?: 0)
        scoreBoard[player]!![type] = score ?: 0

        // Find new upper sum
        scoreBoard[player]?.map { it.value }?.let { values ->
            for (i in 0..5) {
                upperSum += values[i]
            }
        }

        // Check if player got bonus!
        val bonus: Int = if (upperSum >= 63) 50 else 0

        // Find new total sum
        scoreBoard[player]?.let { scoreMap ->
            val values = scoreMap.map { it.value }
            totalSum = upperSum

            for (i in 7..16) {
                totalSum += values[i]
            }
        }

        addScore(player, YatzeeScoreType.UpperSum, upperSum)
        addScore(player, YatzeeScoreType.Bonus, bonus)
        addScore(player, YatzeeScoreType.Sum, totalSum)

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

    fun resetGame() {
        YatzyGame.resetGame()
    }

    private fun checkPossibleOutcomes(dices: List<Dice>) {
        val possibleOutcomes: MutableMap<YatzeeScoreType, Int> = mutableMapOf()
        possibleOutcomes.putAll(dices.checkUpperSection())
        possibleOutcomes.putAll(dices.checkLowerSection())

        _uiState.update {
            it.copy(
                possibleOutcomes = possibleOutcomes.filter { scores ->
                    scores.key in uiState.value.scores[_uiState.value.playerTurn]!!.filter { score -> score.value == 0 }.keys
                }
            )
        }
    }

    private fun addScore(player: String, type: YatzeeScoreType, score: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val element = db.scoreDao().getSpecificScore(player, type)
                db.scoreDao().addPlayerScore(
                    Score(
                        id = element.id,
                        playerName = player,
                        type = type,
                        value = score
                    )
                )
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { YatzySheetViewModel(db = YatzyDatabase.getInstance(context)) }
        }
    }

}
