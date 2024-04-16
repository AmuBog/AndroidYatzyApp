package com.example.yatzee.data.repository

import com.example.yatzee.data.database.ScoreDao
import com.example.yatzee.models.Score
import com.example.yatzee.models.YatzyScoreType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScoresRepository(private val scoreDao: ScoreDao) {
    fun getPlayerScores() = scoreDao.getPlayerScores()

    private fun getPlayerScores(playerName: String) = scoreDao.getPlayerScores(playerName)

    private fun getSpecificScore(playerName: String, yatzyScoreType: YatzyScoreType) =
        scoreDao.getSpecificScore(playerName, yatzyScoreType)

    suspend fun registerScore(score: Score) {
        if (score.isStroke) {
            addScore(score)
            return
        }
        addScore(score)
        addSumAndBonus(score.playerName, score.type, score.value)
    }

    private suspend fun addSumAndBonus(player: String, type: YatzyScoreType, score: Int) {
        val playerScore = getPlayerScores(player)

        var upperSum = playerScore.find { it.type == YatzyScoreType.UpperSum }?.value ?: 0
        var totalSum = playerScore.find { it.type == YatzyScoreType.Sum }?.value ?: 0
        upperSum += if (type.isUpperHalf()) score else 0
        totalSum += score

        val bonus: Int =
            if (playerScore.find { it.type == YatzyScoreType.Bonus }?.value == 50) 0
            else if (upperSum >= 63) 50
            else 0

        if (type.isUpperHalf()) {
            val element = getSpecificScore(player, YatzyScoreType.UpperSum)
            addScore(element.copy(value = upperSum))
        }

        if (bonus == 50) {
            val element = getSpecificScore(player, YatzyScoreType.Bonus)
            addScore(element.copy(value = bonus))
        }

        val element = getSpecificScore(player, YatzyScoreType.Sum)
        addScore(element.copy(value = totalSum + bonus))
    }

    private suspend fun addScore(score: Score) {
        withContext(Dispatchers.IO) {
            scoreDao.addPlayerScore(score)
        }
    }

    suspend fun clearScores() {
        withContext(Dispatchers.IO) {
            scoreDao.deleteAllScores()
            scoreDao.deletePrimaryKeyIndex()
        }
    }

}
