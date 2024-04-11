package com.example.yatzee.models

enum class YatzeeScoreType(val value: String = "") {
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
    Sum
}
