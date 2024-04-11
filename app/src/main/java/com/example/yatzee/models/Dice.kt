package com.example.yatzee.models

data class Dice(
    var value: Int,
    var isLocked: Boolean = false,
    val sides: Int = 6,
)
