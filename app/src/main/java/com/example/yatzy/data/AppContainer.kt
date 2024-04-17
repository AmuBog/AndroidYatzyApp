package com.example.yatzy.data

import android.content.Context
import com.example.yatzy.data.database.YatzyDatabase
import com.example.yatzy.data.repository.HighscoreRepository
import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.domain.RegisterScoreUseCase

interface AppContainer {
    val scoresRepository: ScoresRepository
    val highscoreRepository: HighscoreRepository
    val registerScoreUseCase: RegisterScoreUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    // Database
    private val database: YatzyDatabase by lazy { YatzyDatabase.getInstance(context) }

    // Repository
    override val scoresRepository: ScoresRepository by lazy {
        ScoresRepository(database.scoreDao())
    }

    override val highscoreRepository: HighscoreRepository by lazy {
        HighscoreRepository(database.highscoreDao())
    }

    // Use case
    override val registerScoreUseCase: RegisterScoreUseCase by lazy {
        RegisterScoreUseCase(scoresRepository)
    }

}
