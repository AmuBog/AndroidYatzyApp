package com.example.yatzy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yatzy.models.Highscore
import com.example.yatzy.models.Score

@Database(entities = [Score::class, Highscore::class], version = 3, exportSchema = false)
abstract class YatzyDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
    abstract fun highscoreDao(): HighscoreDao
}
