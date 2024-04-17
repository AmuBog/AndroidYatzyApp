package com.example.yatzy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Highscore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String = "",
    val score: Int = 0
)
