package com.example.yatzee

import android.app.Application
import com.example.yatzee.data.AppContainer
import com.example.yatzee.data.DefaultAppContainer

class YatzyApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}