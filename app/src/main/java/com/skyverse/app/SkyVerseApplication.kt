package com.skyverse.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SkyVerseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
