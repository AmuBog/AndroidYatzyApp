package com.example.yatzy.data

import android.content.Context
import com.example.yatzy.data.database.YatzyDatabase
import com.example.yatzy.data.repository.HighscoreRepository
import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.domain.CalculatePossibleScoresUseCase
import com.example.yatzy.domain.CalculatePossibleStrokeUseCase
import com.example.yatzy.domain.RegisterHighscoreUseCase
import com.example.yatzy.domain.RegisterScoreUseCase

interface AppContainer {
    val scoresRepository: ScoresRepository
    val highscoreRepository: HighscoreRepository
    val calculatePossibleScoresUseCase: CalculatePossibleScoresUseCase
    val calculatePossibleStrokeUseCase: CalculatePossibleStrokeUseCase
    val registerScoreUseCase: RegisterScoreUseCase
    val registerHighscoreUseCase: RegisterHighscoreUseCase
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

    override val calculatePossibleScoresUseCase: CalculatePossibleScoresUseCase by lazy {
        CalculatePossibleScoresUseCase(scoresRepository)
    }

    override val calculatePossibleStrokeUseCase: CalculatePossibleStrokeUseCase by lazy {
        CalculatePossibleStrokeUseCase(scoresRepository)
    }

    override val registerHighscoreUseCase: RegisterHighscoreUseCase by lazy {
        RegisterHighscoreUseCase(scoresRepository, highscoreRepository)
    }

}
