package com.example.yatzy.data

import androidx.compose.runtime.mutableStateListOf
import com.example.yatzy.models.DiceModel

object DicePool {

    val dices =
        mutableStateListOf(DiceModel(1), DiceModel(2), DiceModel(3), DiceModel(4), DiceModel(5))

    fun throwDices(): List<DiceModel> {
        val dices = dices
        val dicesAfterThrow = mutableListOf<DiceModel>()

        for (i in dices.indices) {
            if (dices[i].isLocked) {
                dicesAfterThrow.add(dices[i])
            } else {
                dicesAfterThrow.add(dices[i].throwIt())
            }
        }

        return dicesAfterThrow
    }

    fun lockDice(index: Int): List<DiceModel> {
        dices[index] = dices[index].copy(isLocked = !dices[index].isLocked)
        return dices
    }

    fun resetDices(): List<DiceModel> {
        dices.clear()
        dices.addAll(listOf(DiceModel(1), DiceModel(2), DiceModel(3), DiceModel(4), DiceModel(5)))
        return dices
    }

}
