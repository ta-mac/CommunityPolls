package com.example.communitypolls

import android.app.Application
import com.google.firebase.FirebaseApp

class CommunityPollsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
