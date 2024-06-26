package com.example.yatzy

import com.example.yatzy.models.DiceModel
import com.example.yatzy.models.YatzyScoreType
import com.example.yatzy.models.YatzyScoreType.Chance
import com.example.yatzy.models.YatzyScoreType.Fives
import com.example.yatzy.models.YatzyScoreType.FourOfAKind
import com.example.yatzy.models.YatzyScoreType.Fours
import com.example.yatzy.models.YatzyScoreType.FullHouse
import com.example.yatzy.models.YatzyScoreType.LargeStraight
import com.example.yatzy.models.YatzyScoreType.OnePair
import com.example.yatzy.models.YatzyScoreType.Ones
import com.example.yatzy.models.YatzyScoreType.Sixes
import com.example.yatzy.models.YatzyScoreType.SmallStraight
import com.example.yatzy.models.YatzyScoreType.ThreeOfAKind
import com.example.yatzy.models.YatzyScoreType.Threes
import com.example.yatzy.models.YatzyScoreType.TwoPairs
import com.example.yatzy.models.YatzyScoreType.Twos
import com.example.yatzy.models.YatzyScoreType.Yatzy

fun List<DiceModel>.checkUpperSection() = mapOf(
    Ones to this.filter { it.value == 1 }.sumOf { it.value },
    Twos to this.filter { it.value == 2 }.sumOf { it.value },
    Threes to this.filter { it.value == 3 }.sumOf { it.value },
    Fours to this.filter { it.value == 4 }.sumOf { it.value },
    Fives to this.filter { it.value == 5 }.sumOf { it.value },
    Sixes to this.filter { it.value == 6 }.sumOf { it.value }
).filter { it.value != 0 }

fun List<DiceModel>.checkLowerSection() =
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

private fun getMostValuablePair(dices: List<DiceModel>): Int {
    val pairs = dices.groupingBy { it.value }
        .eachCount()
        .filter { it.value >= 2 }.map { it.key }
        .sortedByDescending { it }
    return if (pairs.isNotEmpty()) pairs.first() * 2 else 0
}

private fun getTwoPairs(dices: List<DiceModel>): Int {
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

private fun getSomeOfAKind(numberOfKind: Int, dices: List<DiceModel>): Int {
    val someOfAKind =
        dices.groupingBy { it.value }.eachCount().filter { it.value >= numberOfKind }.keys
    return if (someOfAKind.isNotEmpty()) someOfAKind.first() * numberOfKind else 0
}

private fun getHouse(dices: List<DiceModel>): Int {
    val count = dices.groupingBy { it.value }.eachCount()

    return if (count.size == 2 && count.values.any { it == 3 }) dices.sumOf { it.value } else 0
}

fun MutableMap<YatzyScoreType, String>.initializeScores(): MutableMap<YatzyScoreType, String> {
    YatzyScoreType.entries.forEach {
        set(it, "0")
    }
    return this
}
