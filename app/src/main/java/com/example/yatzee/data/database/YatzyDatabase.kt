package com.example.yatzee.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yatzee.models.Highscore
import com.example.yatzee.models.Score

@Database(entities = [Score::class, Highscore::class], version = 3)
abstract class YatzyDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
    abstract fun highscoreDao(): HighscoreDao

    companion object {
        private const val Database_NAME = "yatzy.db"

        /**
         * As we need only one instance of db in our app will use to store
         * This is to avoid memory leaks in android when there exist multiple instances of db
         */
        @Volatile
        private var INSTANCE: YatzyDatabase? = null

        fun getInstance(context: Context): YatzyDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        YatzyDatabase::class.java,
                        Database_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
