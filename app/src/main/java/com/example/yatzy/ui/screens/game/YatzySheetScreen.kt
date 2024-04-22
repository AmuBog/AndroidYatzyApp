package com.example.yatzy.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yatzy.R
import com.example.yatzy.ui.common.Dice
import com.example.yatzy.ui.common.PrimaryButton
import com.example.yatzy.ui.theme.YatzyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YatzySheetScreen(
    backNavigation: () -> Unit,
    viewModel: YatzySheetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.yatzy)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = backNavigation) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.quitGame()
                        backNavigation()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "")
                    }
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .border(BorderStroke(1.dp, Color.Black))
            ) {
                // Score types
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    // One blank to push the list down
                    Text(modifier = Modifier.padding(8.dp), text = "")
                    uiState.scores[uiState.playerTurn]?.forEach { score ->
                        if (uiState.possibleOutcomes[score.type] != null && !score.isStroke) {
                            Text(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(4.dp)
                                    .clickable {
                                        viewModel.completeTurn(
                                            score.copy(value = uiState.possibleOutcomes[score.type]!!)
                                        )
                                    },
                                text = score.type.name,
                                color = Color.White
                            )
                        } else if (uiState.numberOfThrows == 0 && uiState.possibleStrokes[score.type] != null) {
                            Text(
                                modifier = Modifier
                                    .background(
                                        Color.Red,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(4.dp)
                                    .clickable {
                                        viewModel.completeTurn(score.copy(isStroke = true))
                                    },
                                text = score.type.name
                            )
                        } else {
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = score.type.value.ifEmpty { score.type.name }
                            )
                        }
                    }
                }
                // Score card
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    uiState.scores.forEach { scores ->
                        val modifier =
                            if (uiState.playerTurn == scores.key && uiState.turn < 16) Modifier.border(
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            ) else Modifier
                        // Player name + scores column
                        Column(
                            modifier = modifier.padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val player = scores.key
                            val gameFinished = uiState.turn > 15
                            val isPlayersTurn = player == uiState.playerTurn && !gameFinished

                            // Player name
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = player,
                                fontWeight = if (isPlayersTurn) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPlayersTurn) MaterialTheme.colorScheme.primary else Color.Black
                            )

                            // Player scores
                            scores.value.forEach { score ->
                                Box {
                                    // Adding the name as it probably is the longest string of the column.
                                    // region Stupid fix
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .align(Alignment.Center),
                                        text = player,
                                        color = Color.Transparent
                                    )
                                    // endregion
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(4.dp),
                                        text = if (isPlayersTurn) {
                                            if (score.value == 0 && !score.isStroke) {
                                                (uiState.possibleOutcomes[score.type]
                                                    ?: 0).toString()
                                            } else if (score.isStroke) "-" else score.value.toString()
                                        } else {
                                            if (score.isStroke) "-" else score.value.toString()
                                        },
                                        fontWeight = if (isPlayersTurn && uiState.possibleOutcomes[score.type] != null) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isPlayersTurn && uiState.possibleOutcomes[score.type] != null) MaterialTheme.colorScheme.primary else Color.Black,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (uiState.turn < 16) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    uiState.dices.forEachIndexed { index, dice ->
                        Dice(
                            dice = dice,
                            enabled = uiState.numberOfThrows < 3,
                            onDiceLocked = { viewModel.lockDice(index) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    text = "Roll dice (${uiState.numberOfThrows}/3)",
                    onClick = { viewModel.throwDices() },
                    enabled = uiState.numberOfThrows > 0
                )
            } else {
                Column(Modifier.padding(16.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Congratulation ${uiState.winner}! You win with a score of ${uiState.highscore} !")
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Finish",
                        onClick = {
                            viewModel.resetGame()
                            backNavigation()
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun YatzySheetPreview() {
    YatzyTheme {
        YatzySheetScreen(
            backNavigation = {}
        )
    }
}
