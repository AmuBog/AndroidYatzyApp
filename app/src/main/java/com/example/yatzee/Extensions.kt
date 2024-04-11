package com.example.yatzee

import com.example.yatzee.models.Dice
import com.example.yatzee.models.YatzeeScoreType
import com.example.yatzee.models.YatzeeScoreType.Chance
import com.example.yatzee.models.YatzeeScoreType.Fives
import com.example.yatzee.models.YatzeeScoreType.FourOfAKind
import com.example.yatzee.models.YatzeeScoreType.Fours
import com.example.yatzee.models.YatzeeScoreType.FullHouse
import com.example.yatzee.models.YatzeeScoreType.LargeStraight
import com.example.yatzee.models.YatzeeScoreType.OnePair
import com.example.yatzee.models.YatzeeScoreType.Ones
import com.example.yatzee.models.YatzeeScoreType.Sixes
import com.example.yatzee.models.YatzeeScoreType.SmallStraight
import com.example.yatzee.models.YatzeeScoreType.ThreeOfAKind
import com.example.yatzee.models.YatzeeScoreType.Threes
import com.example.yatzee.models.YatzeeScoreType.TwoPairs
import com.example.yatzee.models.YatzeeScoreType.Twos
import com.example.yatzee.models.YatzeeScoreType.Yatzy
import kotlin.random.Random
import kotlin.random.nextInt


fun throwDice() = Random.nextInt(1..6)

fun List<Dice>.checkUpperSection() = mapOf(
    Ones to this.filter { it.value == 1 }.sumOf { it.value },
    Twos to this.filter { it.value == 2 }.sumOf { it.value },
    Threes to this.filter { it.value == 3 }.sumOf { it.value },
    Fours to this.filter { it.value == 4 }.sumOf { it.value },
    Fives to this.filter { it.value == 5 }.sumOf { it.value },
    Sixes to this.filter { it.value == 6 }.sumOf { it.value }
).filter { it.value != 0 }

fun List<Dice>.checkLowerSection() =
    mapOf(
        OnePair to getMostValuablePair(this),
        TwoPairs to getTwoPairs(this),
        ThreeOfAKind to getSomeOfAKind(3, this),
        FourOfAKind to getSomeOfAKind(4, this),
        SmallStraight to if (this.map { it.value }.sorted() == listOf(1, 2, 3, 4, 5)) 15 else 0,
        LargeStraight to if (this.map { it.value }.sorted() == listOf(2, 3, 4, 5, 6)) 20 else 0,
        FullHouse to getHouse(this),
        Chance to this.sumOf { it.value },
        Yatzy to if (this.all { it.value == this[0].value }) 50 else 0,
    ).filter { it.value != 0 }

private fun getMostValuablePair(dices: List<Dice>): Int {
    val pairs = dices.groupingBy { it.value }
        .eachCount()
        .filter { it.value >= 2 }.map { it.key }
        .sortedByDescending { it }
    return if (pairs.isNotEmpty()) pairs.first() * 2 else 0
}

private fun getTwoPairs(dices: List<Dice>): Int {
    val pairs = dices.groupingBy { it.value }
        .eachCount()
        .filter { it.value >= 2 }
        .map { it.key }
    var score = 0

    if (pairs.size == 2) {
        score = (pairs.first() * 2) + (pairs.last() * 2)
    }

    return score
}

private fun getSomeOfAKind(numberOfKind: Int, dices: List<Dice>): Int {
    val someOfAKind =
        dices.groupingBy { it.value }.eachCount().filter { it.value >= numberOfKind }.keys
    return if (someOfAKind.isNotEmpty()) someOfAKind.first() * numberOfKind else 0
}

private fun getHouse(dices: List<Dice>): Int {
    val count = dices.groupingBy { it.value }.eachCount()

    return if (count.size == 2 && count.values.any { it == 3 }) dices.sumOf { it.value } else 0
}

fun MutableMap<YatzeeScoreType, String>.initializeScores(): MutableMap<YatzeeScoreType, String> {
    YatzeeScoreType.entries.forEach {
        set(it, "0")
    }
    return this
}
