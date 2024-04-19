package com.example.yatzy.domain

import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.models.YatzyScoreType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculatePossibleStrokeUseCase @Inject constructor(private val scoresRepository: ScoresRepository) {

    suspend operator fun invoke(player: String) = withContext(Dispatchers.IO) {
        checkPossibleToStroke(player)
    }

    private fun checkPossibleToStroke(player: String): Map<YatzyScoreType, Int> {
        val playerScore = scoresRepository.getPlayerScores(player)
        val possibleToStroke: Map<YatzyScoreType, Int>
        val tempList = playerScore.associateBy { it.type }.toMutableMap().apply {
            remove(YatzyScoreType.Bonus)
            remove(YatzyScoreType.UpperSum)
            remove(YatzyScoreType.Sum)
            filter { !it.value.isStroke }
        }.map { it.key to it.value.value }.toMap()
        possibleToStroke = tempList.filter { it.value == 0 }

        return possibleToStroke
    }
}
