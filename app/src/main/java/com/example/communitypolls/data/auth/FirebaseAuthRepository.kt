package com.example.communitypolls.data.auth

import com.example.communitypolls.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    private val usersCol get() = db.collection("users")

    override val currentUser: Flow<AppUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            val fUser = fa.currentUser
            if (fUser == null) {
                trySend(null)
            } else {
                // Load profile to get role/displayName; be resilient if profile missing
                usersCol.document(fUser.uid).get()
                    .addOnSuccessListener { snap ->
                        val role = snap.getString("role")
                            ?: if (fUser.isAnonymous) "guest" else "user"
                        val displayName = snap.getString("displayName") ?: (fUser.displayName ?: "")
                        val email = fUser.email ?: ""
                        trySend(AppUser(fUser.uid, email, displayName, role))
                    }
                    .addOnFailureListener {
                        val email = fUser.email ?: ""
                        val displayName = fUser.displayName ?: ""
                        val role = if (fUser.isAnonymous) "guest" else "user"
                        trySend(AppUser(fUser.uid, email, displayName, role))
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val fUser = result.user ?: return AuthResult.Error("No Firebase user")
            // Create profile doc with default role=user
            ensureUserProfile(fUser, role = "user", displayName = displayName)
            AuthResult.Success(AppUser(fUser.uid, fUser.email ?: "", displayName, "user"))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val fUser = result.user ?: return AuthResult.Error("No Firebase user")
            // Ensure profile exists (do not overwrite role if present)
            ensureUserProfile(fUser, role = null, displayName = fUser.displayName ?: "")
            val prof = usersCol.document(fUser.uid).get().await()
            val role = prof.getString("role") ?: "user"
            val displayName = prof.getString("displayName") ?: (fUser.displayName ?: "")
            AuthResult.Success(AppUser(fUser.uid, fUser.email ?: "", displayName, role))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signInAnonymously(): AuthResult {
        return try {
            val result = auth.signInAnonymously().await()
            val fUser = result.user ?: return AuthResult.Error("No Firebase user")
            ensureUserProfile(fUser, role = "guest", displayName = "Guest")
            AuthResult.Success(AppUser(fUser.uid, "", "Guest", "guest"))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Guest sign-in failed")
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun refreshRole(): AppUser? {
        val fUser = auth.currentUser ?: return null
        val prof = usersCol.document(fUser.uid).get().await()
        val role = prof.getString("role") ?: if (fUser.isAnonymous) "guest" else "user"
        val displayName = prof.getString("displayName") ?: (fUser.displayName ?: "")
        val email = fUser.email ?: ""
        return AppUser(fUser.uid, email, displayName, role)
    }

    private suspend fun ensureUserProfile(fUser: FirebaseUser, role: String?, displayName: String) {
        val doc = usersCol.document(fUser.uid).get().await()
        if (doc.exists()) {
            // Merge: fill displayName/email if missing; keep role unchanged unless a non-null role is provided
            val updates = hashMapOf<String, Any?>(
                "email" to (fUser.email ?: ""),
                "displayName" to displayName,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (role != null) updates["role"] = role
            usersCol.document(fUser.uid).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
        } else {
            // Create new
            val data = hashMapOf(
                "email" to (fUser.email ?: ""),
                "displayName" to displayName,
                "role" to (role ?: if (fUser.isAnonymous) "guest" else "user"),
                "createdAt" to FieldValue.serverTimestamp()
            )
            usersCol.document(fUser.uid).set(data).await()
        }
    }
}
