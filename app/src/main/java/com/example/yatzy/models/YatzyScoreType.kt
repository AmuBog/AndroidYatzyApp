package com.example.yatzy.models

enum class YatzyScoreType(val value: String = "") {
    Ones,
    Twos,
    Threes,
    Fours,
    Fives,
    Sixes,
    UpperSum("Sum"),
    Bonus,
    OnePair("One pair"),
    TwoPairs("Two pairs"),
    ThreeOfAKind("Three of a kind"),
    FourOfAKind("Four of a kind"),
    SmallStraight("Small straight"),
    LargeStraight("Large Straight"),
    FullHouse("Full house"),
    Chance,
    Yatzy,
    Sum;

    fun isUpperHalf(): Boolean {
        return when(this) {
            Ones, Twos, Threes, Fours, Fives, Sixes -> true
            else -> false
        }
    }
}
