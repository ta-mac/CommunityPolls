package com.example.communitypolls

import android.app.Application
import com.google.firebase.FirebaseApp
<<<<<<< HEAD
=======
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.perf.FirebasePerformance
>>>>>>> 71da6fb (Updated App Icon)

class CommunityPollsApp : Application() {
    override fun onCreate() {
        super.onCreate()
<<<<<<< HEAD
        FirebaseApp.initializeApp(this)
=======

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firestore offline caching (disk persistence)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // 🔥 Enables offline support
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Enable Firebase Performance Monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
>>>>>>> 71da6fb (Updated App Icon)
    }
}
