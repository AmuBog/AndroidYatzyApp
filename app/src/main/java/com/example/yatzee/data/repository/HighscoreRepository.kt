package com.example.yatzee.data.repository

import com.example.yatzee.data.database.HighscoreDao
import com.example.yatzee.models.Highscore

class HighscoreRepository(val highscoreDao: HighscoreDao) {

    fun addHighscore(highscore: Highscore) {
        highscoreDao.addHighscore(highscore)
    }

    fun getHighscores(): List<Highscore> = highscoreDao.getHighscores()

}
