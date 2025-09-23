package com.example.communitypolls.ui

import com.example.communitypolls.data.auth.AuthRepository
import com.example.communitypolls.data.auth.FirebaseAuthRepository
import com.example.communitypolls.data.poll.FirebasePollRepository
import com.example.communitypolls.data.poll.PollRepository
import com.example.communitypolls.data.sugg.FirebaseSuggestionRepository          // NEW
import com.example.communitypolls.data.sugg.SuggestionRepository           // NEW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore                      // for PollRepository

object ServiceLocator {

    // --- Firebase singletons ---
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // --- Repositories ---
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository(auth, db)
    }

    val pollRepository: PollRepository by lazy {
        FirebasePollRepository(db) // requires storage in your latest repo version
    }

    // NEW: suggestions repo
    val suggestionRepository: SuggestionRepository by lazy {
        FirebaseSuggestionRepository(db)
    }
}
