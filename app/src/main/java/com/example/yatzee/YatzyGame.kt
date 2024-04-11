package com.example.yatzee

import com.example.yatzee.models.YatzeeScoreType

object YatzyGame {
    private var playerTurnIndex = 0
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
    val scores = mutableMapOf<String, MutableMap<YatzeeScoreType, String>>()

    fun resetGame() {
        playerTurnIndex = 0
        turn = 1
        players.clear()
        scores.clear()
    }

    fun registerScore(type: YatzeeScoreType, score: Int) {
        val scoreBoard = scores[playerTurn]
        if (score == 0) {
            scoreBoard?.put(type, "-")
        } else {
            var upperSum = 0
            var totalSum = 0

            // Register new score
            scoreBoard?.put(type, score.toString())

            // Find new upper sum
            scoreBoard?.let { scoreMap ->
                val values = scoreMap.map { it.value }
                for (i in 0..5) {
                    upperSum += try {
                        values[i].toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }
            }

            // Check if player got bonus!
            val bonus: Int = if (upperSum >= 63) 50 else 0

            // Find new total sum
            scoreBoard?.let { scoreMap ->
                val values = scoreMap.map { it.value }
                totalSum = upperSum

                for (i in 7..16) {
                    totalSum += try {
                        values[i].toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }
            }

            // Register new sums
            scoreBoard?.put(YatzeeScoreType.UpperSum, upperSum.toString())
            scoreBoard?.put(YatzeeScoreType.Bonus, bonus.toString())
            scoreBoard?.put(YatzeeScoreType.Sum, totalSum.toString())
        }

        nextPlayer()
    }

    private fun nextPlayer() {
        if (playerTurnIndex + 1 >= scores.size) {
            turn++
            playerTurnIndex = 0
        } else {
            playerTurnIndex++
        }
    }
}