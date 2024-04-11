package com.example.yatzee.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.yatzee.models.Score
import com.example.yatzee.models.YatzeeScoreType
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Update
    fun addPlayerScore(score: Score)

    @Insert
    fun addPlayerScores(scores: List<Score>)

    @Query("SELECT * FROM Score WHERE playerName = :playerName")
    fun getPlayerScore(playerName: String): Flow<List<Score>>

    @Query("SELECT * FROM Score WHERE playerName = :playerName AND type = :type")
    fun getSpecificScore(playerName: String, type: YatzeeScoreType) : Score

    @Query("DELETE FROM Score")
    fun deleteAllScores()

    @Query("DELETE FROM sqlite_sequence")
    fun deletePrimaryKeyIndex()
}
