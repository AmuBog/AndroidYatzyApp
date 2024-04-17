package com.example.yatzee.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.yatzee.models.Highscore

@Dao
interface HighscoreDao {

    @Insert
    fun addHighscore(highscore: Highscore)

    @Query("SELECT * FROM highscore ORDER BY score DESC")
    fun getHighscores(): List<Highscore>

    @Query("DELETE FROM highscore")
    fun deleteHighscores()

}
