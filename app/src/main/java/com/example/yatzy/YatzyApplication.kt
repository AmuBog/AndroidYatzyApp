package com.example.yatzy

import android.app.Application
import com.example.yatzy.data.AppContainer
import com.example.yatzy.data.DefaultAppContainer

class YatzyApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}