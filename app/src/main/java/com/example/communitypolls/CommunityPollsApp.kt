package com.example.communitypolls

import android.app.Application
import com.google.firebase.FirebaseApp
<<<<<<< HEAD
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.perf.FirebasePerformance
=======
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.perf.FirebasePerformance
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)

class CommunityPollsApp : Application() {
    override fun onCreate() {
        super.onCreate()
<<<<<<< HEAD
<<<<<<< HEAD

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
=======
        FirebaseApp.initializeApp(this)
=======

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
>>>>>>> 5f6ea81 (Updated App Icon)

        // Enable Firestore offline caching (disk persistence)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // 🔥 Enables offline support
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Enable Firebase Performance Monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
<<<<<<< HEAD
=======
        FirebaseApp.initializeApp(this)
>>>>>>> 0af30b8 (Added some security measures)
=======
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
    }
}
