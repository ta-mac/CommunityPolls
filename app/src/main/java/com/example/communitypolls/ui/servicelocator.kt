package com.example.communitypolls.ui

import com.example.communitypolls.data.auth.AuthRepository
import com.example.communitypolls.data.auth.FirebaseAuthRepository
import com.example.communitypolls.data.poll.FirebasePollRepository
import com.example.communitypolls.data.poll.PollRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Simple service locator for app-wide singletons.
 */
object ServiceLocator {

    // --- Firebase singletons ---
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // --- Repositories ---
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(auth, db)
    }

    val pollRepository: PollRepository by lazy {
        FirebasePollRepository(db)
    }
}
