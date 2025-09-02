package com.example.communitypolls

import android.app.Application
import com.google.firebase.FirebaseApp

class CommunityPollsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase once for the whole app.
        // This expects google-services.json in app/ (we'll add it soon).
        FirebaseApp.initializeApp(this)
    }
}
