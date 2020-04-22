package com.iesvirgendelcarmen.periodicowordpress

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var instance :App
            private set

        fun getContext() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}