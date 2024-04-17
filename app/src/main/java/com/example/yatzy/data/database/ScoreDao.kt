package com.example.yatzy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.yatzy.models.Score
import com.example.yatzy.models.YatzyScoreType
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Update
    fun addPlayerScore(score: Score)

    @Insert
    fun addPlayerScores(scores: List<Score>)

    @Query("SELECT * FROM Score")
    fun getPlayerScores(): Flow<List<Score>>

    @Query("SELECT * FROM Score WHERE playerName = :playerName")
    fun getPlayerScores(playerName: String): List<Score>

    @Query("SELECT * FROM Score WHERE playerName = :playerName AND type = :type")
    fun getSpecificScore(playerName: String, type: YatzyScoreType) : Score

    @Query("DELETE FROM Score")
    fun deleteAllScores()

    @Query("DELETE FROM sqlite_sequence")
    fun deletePrimaryKeyIndex()
}
