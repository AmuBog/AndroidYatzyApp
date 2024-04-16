package com.example.yatzee.data

import android.content.Context
import com.example.yatzee.data.database.YatzyDatabase
import com.example.yatzee.data.repository.ScoresRepository

interface AppContainer {
    val scoresRepository: ScoresRepository
}
class DefaultAppContainer(val context: Context): AppContainer {

    private val database: YatzyDatabase by lazy { YatzyDatabase.getInstance(context) }

    override val scoresRepository: ScoresRepository by lazy {
        ScoresRepository(database.scoreDao())
    }

}