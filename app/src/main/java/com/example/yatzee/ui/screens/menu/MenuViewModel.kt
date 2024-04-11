package com.example.yatzee.ui.screens.menu

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yatzee.YatzyGame
import com.example.yatzee.data.YatzyDatabase
import com.example.yatzee.initializeScores
import com.example.yatzee.models.Score
import com.example.yatzee.models.YatzeeScoreType
import com.example.yatzee.ui.screens.game.YatzySheetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ViewState {
    data object Idle : ViewState()
    data object Loading : ViewState()
    class Error(val message: String) : ViewState()
}

data class MenuUIState(
    val viewState: ViewState = ViewState.Idle,
    val players: List<String> = listOf(""),
    val hasOngoingGame: Boolean = false,
    val showBottomSheet: Boolean = false,
    val dailyQuote: String = ""
)

class MenuViewModel(val db: YatzyDatabase) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUIState())
    val uiState = _uiState.asStateFlow()

    private var quotes = listOf(
        "A dice a day keeps the doctor away!",
        "Some say using a D20 would make Yatzy way more interesting..",
        "Chat GPT does not know how to depict a healthy dice..",
        "Yatzy is random, so why can't you be?",
        "Yatzy does not work very well as a palindrome",
        "!!YZTAY",
        "A turn of forced Yatzy is pretty pointless, except you do get the points..",
        "If adding a dice makes it Maxi, is Yatzy with 8 dices Super Duper Maxi Yatzy?",
        "I'm tired of lame Yatzy quotes..",
        "Let's play!",
        "You look good at throwing dices!",
        "Are you feeling lucky?",
        "Minecraft and Terraria are better at this...",
        "Don't play Minecraft instead!",
        "Did you figure it out?",
        "Am I getting crazy, or is that dice looking at me?",
        "You know why 6 is afraid of 7? Because 7 8 9",
        "What kind of dice has a 9 ? 0.o",
        "Finally got tired of solitaire?",
        "🎲😄🎲",
        "5, 4, 3, 2, 1 Yatzy!"
    )

    init {
        _uiState.update { it.copy(dailyQuote = quotes.random()) }
    }

    fun checkOngoingGame() {
        _uiState.update { it.copy(hasOngoingGame = YatzyGame.players.isNotEmpty()) }
    }

    fun addPlayer() {
        val newList = mutableListOf<String>()

        newList.addAll(_uiState.value.players)
        newList.add("")

        updatePlayerList(newList)
    }

    fun updatePlayer(index: Int, value: String) {
        val newList = mutableListOf<String>()

        newList.addAll(_uiState.value.players)
        newList[index] = value

        updatePlayerList(newList)
    }

    fun removePlayer(index: Int) {
        val newList = mutableListOf<String>()

        newList.addAll(_uiState.value.players)
        newList.removeAt(index)

        updatePlayerList(newList)
    }

    fun startGame() {
        _uiState.update { it.copy(viewState = ViewState.Loading) }
        YatzyGame.players.clear()
        YatzyGame.players.addAll(uiState.value.players)
        YatzyGame.players.forEach { player ->
            val playerScoreCard = mutableListOf<Score>()
            YatzyGame.scores[player] = mutableMapOf<YatzeeScoreType, String>().initializeScores()
            YatzeeScoreType.entries.forEach { type ->
                playerScoreCard.add(
                    Score(playerName = player, type = type, value = 0)
                )
            }
            addScoresToDb(playerScoreCard)
        }

        toggleBottomSheet(false)
        resetPlayers()
    }

    fun toggleBottomSheet(showBottomSheet: Boolean) {
        _uiState.update { it.copy(showBottomSheet = showBottomSheet) }
    }

    private fun resetPlayers() {
        updatePlayerList(listOf(""))
    }

    private fun updatePlayerList(newList: List<String>) {
        _uiState.update { it.copy(players = newList) }
    }

    private fun addScoresToDb(scores: List<Score>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { db.scoreDao().addPlayerScores(scores) }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { MenuViewModel(db = YatzyDatabase.getInstance(context)) }
        }
    }

}
