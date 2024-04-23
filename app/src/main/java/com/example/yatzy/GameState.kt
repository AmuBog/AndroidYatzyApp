package com.example.yatzy

import com.example.yatzy.models.GameType

object GameState {

    private var playerTurnIndex = 0
    var gameType = GameType.FREE
    var turn = 1
    val players = mutableListOf<String>()
    val playerTurn
        get() = if (players.isNotEmpty()) {
            try {
                players[playerTurnIndex]
            } catch (e: IndexOutOfBoundsException) {
                playerTurnIndex = 0
                players[playerTurnIndex]
            }
        } else ""

    fun resetGame() {
        playerTurnIndex = 0
        turn = 1
        players.clear()
    }

    fun nextPlayer() {
        if (playerTurnIndex + 1 >= players.size) {
            turn++
            playerTurnIndex = 0
        } else {
            playerTurnIndex++
        }
    }
}
