package com.example.yatzee.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yatzee.R
import com.example.yatzee.ui.common.OnLifecycleEvent
import com.example.yatzee.ui.common.PrimaryButton
import com.example.yatzee.ui.common.SecondaryButton
import com.example.yatzee.ui.theme.YatzeeTheme

@Composable
fun MenuScreen(
    navigateToYatzyGame: () -> Unit,
    navigateToHighscores: () -> Unit,
    viewModel: MenuViewModel = viewModel(
        factory = MenuViewModel.factory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkOngoingGame()
            }

            else -> {}
        }
    }

    if (uiState.showBottomSheet) {
        AddPlayersBottomSheet(
            players = uiState.players,
            onDismissRequest = { viewModel.toggleBottomSheet(false) },
            onUpdatePlayer = { index, player ->
                viewModel.updatePlayer(index, player)
            },
            onRemovePlayer = { viewModel.removePlayer(it) },
            onAddPlayer = { viewModel.addPlayer() },
            onStartGame = {
                viewModel.startGame()
                navigateToYatzyGame()
            }
        )
    }

    Box {
        if (uiState.viewState is ViewState.Loading) {
            CircularProgressIndicator(Modifier.size(100.dp))
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.yatzy_logo),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.play),
                    onClick = {
                        if (!uiState.hasOngoingGame) {
                            viewModel.toggleBottomSheet(true)
                        } else {
                            navigateToYatzyGame()
                        }
                    }
                )
                SecondaryButton(
                    text = stringResource(R.string.highscore),
                    icon = Icons.Default.List,
                    onClick = {
                        navigateToHighscores()
                    }
                )
                SecondaryButton(
                    text = stringResource(R.string.previous_games),
                    icon = Icons.Default.Check,
                    onClick = {}
                )
                SecondaryButton(
                    text = stringResource(R.string.settings),
                    icon = Icons.Default.Settings,
                    onClick = {}
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = uiState.dailyQuote)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun MenuScreenPreview() {
    YatzeeTheme {
        MenuScreen(
            navigateToYatzyGame = {},
            navigateToHighscores = {}
        )
    }
}
