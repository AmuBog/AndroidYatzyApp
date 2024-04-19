package com.example.yatzy.domain

import com.example.yatzy.checkLowerSection
import com.example.yatzy.checkUpperSection
import com.example.yatzy.data.repository.ScoresRepository
import com.example.yatzy.models.Dice
import com.example.yatzy.models.YatzyScoreType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculatePossibleScoresUseCase @Inject constructor(private val scoresRepository: ScoresRepository) {

    suspend operator fun invoke(player: String, dices: List<Dice>): Map<YatzyScoreType, Int> =
        withContext(Dispatchers.IO) { checkPossibleOutcomes(player, dices) }

    private fun checkPossibleOutcomes(player: String, dices: List<Dice>): Map<YatzyScoreType, Int> {
        val potentialScores = scoresRepository.getPlayerScores(player).filter { it.value == 0 }
        val possibleOutcomes: MutableMap<YatzyScoreType, Int> = mutableMapOf()
        possibleOutcomes.putAll(dices.checkUpperSection())
        possibleOutcomes.putAll(dices.checkLowerSection())

        return possibleOutcomes.filter { scores -> scores.key in potentialScores.map { it.type } }
    }
}
