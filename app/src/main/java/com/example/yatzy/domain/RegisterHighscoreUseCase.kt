package com.example.yatzy.domain

import com.example.yatzy.data.repository.HighscoreRepository
import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.models.Highscore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterHighscoreUseCase(
    private val scoresRepository: ScoresRepository,
    private val highscoreRepository: HighscoreRepository
) {

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        registerHighScore()
    }

    private suspend fun registerHighScore() {
        scoresRepository.getPlayerSums().forEach {
            highscoreRepository.addHighscore(
                Highscore(
                    playerName = it.playerName,
                    score = it.value
                )
            )
        }
    }

}
