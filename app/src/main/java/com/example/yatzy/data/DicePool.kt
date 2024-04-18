package com.example.yatzy.data

import com.example.yatzy.models.Dice

object DicePool {

    val dices = mutableListOf<Dice>()

    init {
        resetDices()
    }

    fun throwDices(): List<Dice> {
        val dices = dices
        val dicesAfterThrow = mutableListOf<Dice>()

        for (i in dices.indices) {
            if (dices[i].isLocked) {
                dicesAfterThrow.add(dices[i])
            } else {
                dicesAfterThrow.add(dices[i].throwIt())
            }
        }

        return dicesAfterThrow
    }

    fun lockDice(index: Int): List<Dice> {
        dices[index] = dices[index].copy(isLocked = !dices[index].isLocked)
        return dices
    }

    fun resetDices(): List<Dice> {
        dices.clear()
        dices.addAll(initialDices)
        return dices
    }

    private val initialDices = listOf(Dice(1), Dice(2), Dice(3), Dice(4), Dice(5))

}
