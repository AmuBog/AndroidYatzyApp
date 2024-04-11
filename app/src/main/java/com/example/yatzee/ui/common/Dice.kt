package com.example.yatzee.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yatzee.models.Dice

@Composable
fun Dice(
    dice: Dice,
    enabled: Boolean = false,
    onDiceLocked: () -> Unit
) {
    val boxSize = if (!dice.isLocked) 55.dp else 50.dp
    val modifier = if(enabled) {
        Modifier
            .size(55.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onDiceLocked() }
    } else {
        Modifier
            .size(55.dp)
            .background(MaterialTheme.colorScheme.primary)
    }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(boxSize)
                .background(Color.Gray),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = dice.value.toString()
            )
        }
    }
}
