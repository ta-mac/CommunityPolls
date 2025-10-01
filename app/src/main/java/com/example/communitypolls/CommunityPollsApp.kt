package com.example.communitypolls

import android.app.Application
import com.google.firebase.FirebaseApp
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.perf.FirebasePerformance
=======
>>>>>>> 0af30b8 (Added some security measures)

class CommunityPollsApp : Application() {
    override fun onCreate() {
        super.onCreate()
<<<<<<< HEAD

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firestore offline caching (disk persistence)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // 🔥 Enables offline support
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Enable Firebase Performance Monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
=======
        FirebaseApp.initializeApp(this)
>>>>>>> 0af30b8 (Added some security measures)
    }
}
