package com.example.yatzy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val type: YatzyScoreType,
    val value: Int,
    val isStroke: Boolean = false
)
