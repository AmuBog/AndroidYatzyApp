package com.example.yatzy.di

import android.content.Context
import androidx.room.Room
import com.example.yatzy.data.database.HighscoreDao
import com.example.yatzy.data.database.ScoreDao
import com.example.yatzy.data.database.YatzyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): YatzyDatabase {
        return Room.databaseBuilder(
            appContext,
            YatzyDatabase::class.java,
            "yatzy.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideScoreDao(appDatabase: YatzyDatabase): ScoreDao {
        return appDatabase.scoreDao()
    }

    @Provides
    @Singleton
    fun provideHighscoreDao(appDatabase: YatzyDatabase): HighscoreDao {
        return appDatabase.highscoreDao()
    }
}
