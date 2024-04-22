package com.example.yatzy.models

import com.example.yatzy.R
import kotlin.random.Random
import kotlin.random.nextInt

data class DiceModel(
    var value: Int,
    var isLocked: Boolean = false,
    val sides: Int = 6,
) {
    fun throwIt(): DiceModel {
        this.value = Random.nextInt(1..sides)
        return this
    }

    fun getDrawable(): Int {
        return when (this.value) {
            1 -> R.drawable.dice_six_faces_one
            2 -> R.drawable.dice_six_faces_two
            3 -> R.drawable.dice_six_faces_three
            4 -> R.drawable.dice_six_faces_four
            5 -> R.drawable.dice_six_faces_five
            6 -> R.drawable.dice_six_faces_six
            else -> R.drawable.dice_six_faces_one
        }
    }
}
