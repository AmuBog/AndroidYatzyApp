package com.example.yatzy.data.repository

import com.example.yatzy.data.database.HighscoreDao
import com.example.yatzy.models.Highscore

class HighscoreRepository(val highscoreDao: HighscoreDao) {

    fun addHighscore(highscore: Highscore) {
        highscoreDao.addHighscore(highscore)
    }

    fun getHighscores(): List<Highscore> = highscoreDao.getHighscores()

}
