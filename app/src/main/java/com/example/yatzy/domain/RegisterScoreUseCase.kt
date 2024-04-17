package com.example.yatzy.domain

import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.models.Score
import com.example.yatzy.models.YatzyScoreType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterScoreUseCase(private val repository: ScoresRepository) {

    suspend operator fun invoke(score: Score) = withContext(Dispatchers.IO) {
        registerScore(score)
    }

    private suspend fun registerScore(score: Score) {
        repository.addScore(score)
        if (score.isStroke) return
        addSumAndBonus(score)
    }

    private suspend fun addSumAndBonus(score: Score) {
        val playerScore = repository.getPlayerScores(score.playerName)

        var upperSum = playerScore.find { it.type == YatzyScoreType.UpperSum }?.value ?: 0
        var totalSum = playerScore.find { it.type == YatzyScoreType.Sum }?.value ?: 0
        upperSum += if (score.type.isUpperHalf()) score.value else 0
        totalSum += score.value

        val bonus: Int =
            if (playerScore.find { it.type == YatzyScoreType.Bonus }?.value == 50) 0
            else if (upperSum >= 63) 50
            else 0

        if (score.type.isUpperHalf()) {
            val element = repository.getSpecificScore(score.playerName, YatzyScoreType.UpperSum)
            repository.addScore(element.copy(value = upperSum))
        }

        if (bonus == 50) {
            val element = repository.getSpecificScore(score.playerName, YatzyScoreType.Bonus)
            repository.addScore(element.copy(value = bonus))
        }

        val element = repository.getSpecificScore(score.playerName, YatzyScoreType.Sum)
        repository.addScore(element.copy(value = totalSum + bonus))
    }

}
