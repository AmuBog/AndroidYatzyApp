package com.example.yatzy.ui.screens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.yatzy.R
import com.example.yatzy.ui.common.PrimaryButton
import com.example.yatzy.ui.common.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayersBottomSheet(
    players: List<String> = listOf(""),
    onDismissRequest: () -> Unit = {},
    onUpdatePlayer: (Int, String) -> Unit = { _, _ -> },
    onRemovePlayer: (Int) -> Unit = {},
    onAddPlayer: () -> Unit = {},
    onStartGame: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = rememberModalBottomSheetState()
    ) {
        // Sheet content
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                stringResource(R.string.add_players),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.padding(bottom = 8.dp))
            players.forEachIndexed { index, name ->
                Row {
                    OutlinedTextField(
                        modifier = Modifier.padding(bottom = 8.dp),
                        value = name,
                        onValueChange = { onUpdatePlayer(index, it) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    IconButton(onClick = { onRemovePlayer(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = Color.Red,
                            contentDescription = ""
                        )
                    }
                }
            }
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onAddPlayer() },
                    text = stringResource(R.string.add_player)
                )
                Spacer(Modifier.width(8.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onStartGame() },
                    text = stringResource(R.string.start_game)
                )
            }
        }
    }
}