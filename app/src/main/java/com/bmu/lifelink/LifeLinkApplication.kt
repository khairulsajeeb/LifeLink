package com.bmu.lifelink

import android.app.Application
import com.google.firebase.FirebaseApp

class LifeLinkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
    }
}
