package com.example.yatzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.yatzee.ui.YatzyApp
import com.example.yatzee.ui.theme.YatzeeTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Clear DB whenever we launch the app for now..
        GlobalScope.launch {
            (application as YatzyApplication).container.scoresRepository.clearScores()
        }

        setContent {
            YatzeeTheme {
                YatzyApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuScreenPreview() {
    YatzeeTheme {
        YatzyApp()
    }
}
