package com.example.yatzy.data.repository

import com.example.yatzy.data.database.HighscoreDao
import com.example.yatzy.models.Highscore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HighscoreRepository(private val highscoreDao: HighscoreDao) {

    suspend fun addHighscore(highscore: Highscore) {
        withContext(Dispatchers.IO) { highscoreDao.addHighscore(highscore) }
    }

    suspend fun getHighscores(): List<Highscore> =
        withContext(Dispatchers.IO) { highscoreDao.getHighscores() }

}
