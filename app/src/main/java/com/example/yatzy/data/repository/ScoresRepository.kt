package com.example.yatzy.data.repository

import com.example.yatzy.data.database.ScoreDao
import com.example.yatzy.models.Score
import com.example.yatzy.models.YatzyScoreType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScoresRepository(private val scoreDao: ScoreDao) {
    fun getPlayerScores() = scoreDao.getPlayerScores()

    fun getPlayerScores(playerName: String) = scoreDao.getPlayerScores(playerName)

    fun getSpecificScore(playerName: String, yatzyScoreType: YatzyScoreType) =
        scoreDao.getSpecificScore(playerName, yatzyScoreType)

    fun addScore(score: Score) {
        scoreDao.addPlayerScore(score)
    }

    suspend fun clearScores() {
        withContext(Dispatchers.IO) {
            scoreDao.deleteAllScores()
            scoreDao.deletePrimaryKeyIndex()
        }
    }

}
