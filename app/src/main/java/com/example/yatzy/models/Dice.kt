package com.example.yatzy.models

import kotlin.random.Random
import kotlin.random.nextInt

data class Dice(
    var value: Int,
    var isLocked: Boolean = false,
    val sides: Int = 6,
) {
    fun throwIt(): Dice {
        this.value = Random.nextInt(1..sides)
        return this
    }

}
