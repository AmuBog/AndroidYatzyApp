package com.example.yatzy.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yatzy.GameState
import com.example.yatzy.R
import com.example.yatzy.models.YatzyScoreType
import com.example.yatzy.ui.common.Dice
import com.example.yatzy.ui.common.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForcedYatzyScreen(
    viewModel: ForcedYatzyViewModel = hiltViewModel(),
    backNavigation: () -> Unit
) {

    val players = GameState.players
    var turnNum = 0
    var playerNumber = 0
    var currentPlayer by remember { mutableStateOf(players.first()) }
    var turn by remember { mutableStateOf(YatzyScoreType.Ones) }

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
        Column {
            Column(Modifier.padding(it)) {
                val longestName = YatzyScoreType.entries.maxBy { it.name.length }.name
                // Names row
                Row {
                    // Blank that should be put above the scores
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = longestName,
                        color = Color.Transparent
                    )
                    players.forEach { player ->
                        Text(
                            text = player,
                            color = if (player == currentPlayer) MaterialTheme.colorScheme.primary else Color.Black,
                            fontWeight = if (player == currentPlayer) FontWeight.Bold else FontWeight.Normal
                        )
                        if (player != players.last()) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
                // Score rows
                uiState.scores.forEach { (t, u) ->
                    val mod = if (t == turn) {
                        Modifier.border(BorderStroke(1.dp, Color.Red))
                    } else {
                        Modifier
                    }
                    Row(
                        modifier = mod
                    ) {
                        Box(
                            Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = longestName,
                                color = Color.Transparent
                            )
                            Text(text = t.name)
                        }
                        u.forEach { score ->
                            val value =
                                if (turn == score.type && score.playerName == currentPlayer) {
                                    uiState.possibleOutcomes[turn] ?: 0
                                } else {
                                    score.value
                                }
                            Box {
                                Text(
                                    text = score.playerName,
                                    color = Color.Transparent
                                )
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = if (score.isStroke) "-" else value.toString(),
                                    color = if (score.type == turn && score.playerName == currentPlayer) MaterialTheme.colorScheme.primary else Color.Black,
                                    fontWeight = if (score.type == turn && score.playerName == currentPlayer) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (score.playerName != players.last()) {
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                }
            }

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
            if (!uiState.finished) {
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    text = if (uiState.numberOfThrows > 0) "Roll dice (${uiState.numberOfThrows}/3)" else "Next player",
                    onClick = {
                        if (uiState.numberOfThrows > 0) {
                            viewModel.throwDices()
                        } else {
                            val score = uiState.scores[turn]?.get(playerNumber)!!
                            viewModel.completeTurn(
                                score.copy(
                                    value = uiState.possibleOutcomes[turn] ?: 0,
                                    isStroke = uiState.possibleOutcomes[turn] == null
                                )
                            )

                            if (playerNumber < players.size - 1) {
                                playerNumber++
                                currentPlayer = players[playerNumber]
                            } else {
                                playerNumber = 0
                                currentPlayer = players[0]
                                turnNum++

                                val nextTurn = YatzyScoreType.entries[turnNum]
                                if (nextTurn == YatzyScoreType.UpperSum) {
                                    turnNum += 2
                                }
                                if (nextTurn == YatzyScoreType.Sum) {
                                    viewModel.finishGame()
                                } else {
                                    turn = YatzyScoreType.entries[turnNum]
                                }
                            }
                        }
                    }
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
