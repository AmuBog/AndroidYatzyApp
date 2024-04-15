package com.example.yatzee.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.yatzee.models.Dice as DiceModel
import com.example.yatzee.ui.theme.YatzeeTheme

@Composable
fun Dice(
    dice: DiceModel,
    enabled: Boolean = false,
    onDiceLocked: () -> Unit
) {
    val boxSize = if (!dice.isLocked) 55.dp else 50.dp
    val modifier = if(enabled) {
        Modifier
            .size(55.dp)
            .clickable { onDiceLocked() }
    } else {
        Modifier.size(55.dp)
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(boxSize)
                .background(Color.Gray, RoundedCornerShape(4.dp)),
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = dice.value.toString()
            )
        }
    }
}

@Preview
@Composable
private fun DicePreview() {
    YatzeeTheme {
        Dice(
            dice = DiceModel(1, false, 6),
            enabled = false,
            onDiceLocked = {}
        )
    }
}
