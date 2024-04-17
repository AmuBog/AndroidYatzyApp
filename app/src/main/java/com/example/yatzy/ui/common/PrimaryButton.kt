package com.example.yatzy.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.yatzy.ui.theme.YatzyTheme

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    YatzyTheme {
        PrimaryButton(
            text = "Primary Button",
            onClick = {}
        )
    }
}
