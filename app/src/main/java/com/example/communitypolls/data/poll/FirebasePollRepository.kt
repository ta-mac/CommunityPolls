package com.example.communitypolls.data.poll

import android.util.Log
import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebasePollRepository"

class FirebasePollRepository(
    private val db: FirebaseFirestore
) : PollRepository {

    private val pollCache = mutableMapOf<String, Poll>()
    private val polls get() = db.collection("polls")

    // ---------- Streams ----------

    override fun observeActivePolls(limit: Int?): Flow<List<Poll>> = callbackFlow {
        var reg: ListenerRegistration? = null

        fun listenFallback() {
            var q: Query = polls.whereEqualTo("isActive", true)
            if (limit != null) q = q.limit(limit.toLong())
            reg = q.addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "Fallback query failed: ${err.message}", err)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()
                list.forEach { pollCache[it.id] = it }
                trySend(list)
            }
        }

        fun listenOrdered() {
            var q: Query = polls
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
            if (limit != null) q = q.limit(limit.toLong())

            reg = q.addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "Ordered query failed: ${err.message}", err)
                    if (err is FirebaseFirestoreException &&
                        err.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION
                    ) {
                        reg?.remove()
                        listenFallback()
                        trySend(emptyList())
                    } else trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()
                list.forEach { pollCache[it.id] = it }
                trySend(list)
            }
        }

        listenOrdered()
        awaitClose { reg?.remove() }
    }

    override fun observePoll(pollId: String): Flow<Poll?> = callbackFlow {
        pollCache[pollId]?.let { trySend(it) }

        val reg = polls.document(pollId).addSnapshotListener { snap, err ->
            if (err != null) {
                Log.e(TAG, "observePoll failed: ${err.message}", err)
                trySend(null)
                return@addSnapshotListener
            }
            val poll = snap?.toPoll()
            if (poll != null) pollCache[poll.id] = poll
            trySend(poll)
        }
        awaitClose { reg.remove() }
    }

    override fun observeResults(pollId: String): Flow<Map<String, Int>> = callbackFlow {
        val reg = polls.document(pollId)
            .collection("votes")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "observeResults failed: ${err.message}", err)
                    trySend(emptyMap())
                    return@addSnapshotListener
                }
                val counts = mutableMapOf<String, Int>()
                snap?.documents?.forEach { d ->
                    val optionId = d.getString("optionId") ?: return@forEach
                    counts[optionId] = (counts[optionId] ?: 0) + 1
                }
                trySend(counts)
            }
        awaitClose { reg.remove() }
    }

    // ---------- Mutations ----------

    override suspend fun createPoll(
        title: String,
        description: String,
        options: List<PollOption>,
        createdByUid: String,
        closesAtMillis: Long?,
        isActive: Boolean
    ): CreatePollResult {
        val trace: Trace = FirebasePerformance.getInstance().newTrace("create_poll_trace")
        trace.start()

        val trimmedOptions = options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (title.isBlank()) return CreatePollResult.Error("Title is required")
        if (trimmedOptions.size < 2) return CreatePollResult.Error("Add at least two options")

        val data = hashMapOf(
            "title" to title.trim(),
            "description" to description.trim(),
            "options" to trimmedOptions.map { mapOf("id" to it.id, "text" to it.text) },
            "createdBy" to createdByUid,
            "createdAt" to System.currentTimeMillis(),
            "closesAt" to closesAtMillis,
            "isActive" to isActive
        )

        return try {
            val doc = polls.document()
            doc.set(data).await()
            pollCache[doc.id] = Poll(
                doc.id, title, description, trimmedOptions,
                createdByUid, System.currentTimeMillis(), closesAtMillis, isActive
            )
            trace.stop()
            CreatePollResult.Success(doc.id)
        } catch (e: Exception) {
            trace.stop()
            CreatePollResult.Error(e.message ?: "Failed to create poll")
        }
    }

    override suspend fun updatePoll(
        pollId: String,
        title: String?,
        description: String?,
        options: List<PollOption>?,
        closesAtMillis: Long?,
        isActive: Boolean?
    ): OpResult {
        val trace = FirebasePerformance.getInstance().newTrace("update_poll_trace")
        trace.start()

        val updates = HashMap<String, Any?>()
        title?.let { updates["title"] = it }
        description?.let { updates["description"] = it }
        options?.let {
            if (it.size < 2) return OpResult.Error("At least two options required")
            updates["options"] = it.map { o ->
                mapOf("id" to o.id.trim(), "text" to o.text.trim())
            }
        }
        closesAtMillis?.let { updates["closesAt"] = it }
        isActive?.let { updates["isActive"] = it }

        return try {
            polls.document(pollId).update(updates as Map<String, Any>).await()
            pollCache.remove(pollId)
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
            OpResult.Error(e.message ?: "Failed to update poll")
        }
    }

    override suspend fun deletePoll(pollId: String): OpResult {
        val trace = FirebasePerformance.getInstance().newTrace("delete_poll_trace")
        trace.start()
        return try {
            polls.document(pollId).delete().await()
            pollCache.remove(pollId)
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
            OpResult.Error(e.message ?: "Failed to delete poll")
        }
    }

    // ---------- Updated: castVote now stores option text, anonymous flag, and voter email ----------

    override suspend fun castVote(
        pollId: String,
        optionId: String,
        voterUid: String,
        anonymous: Boolean
    ): OpResult {
        val trace = FirebasePerformance.getInstance().newTrace("cast_vote_trace")
        trace.start()
        return try {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val voterEmail = user?.email ?: voterUid

            // 🔍 Fetch the poll so we can find the option text
            val pollSnap = polls.document(pollId).get().await()
            val pollData = pollSnap.data
            val pollOptions = pollData?.get("options") as? List<Map<String, Any>> ?: emptyList()

            // ✅ Match the optionId to its text
            val optionText = pollOptions.find { it["id"] == optionId }?.get("text") as? String
                ?: "Unknown option"

            // 🧾 Build vote record
            val voteData = hashMapOf(
                "optionId" to optionId,
                "optionText" to optionText,
                "createdAt" to System.currentTimeMillis(),
                "anonymous" to anonymous,
                "voterEmail" to if (anonymous) null else voterEmail
            )

            // 📝 Store the vote (overwrites same voter if already voted)
            polls.document(pollId)
                .collection("votes")
                .document(voterUid)
                .set(voteData)
                .await()

            trace.stop()
            OpResult.Success
        }
        catch (e: Exception) {
            trace.stop()
            Log.e(TAG, "Error casting vote: ${e.message}", e)

            val customMessage = when {
                e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true ->
                    "You’ve already voted on this poll."
                else ->
                    "Something went wrong while submitting your vote."
            }

            return OpResult.Error(customMessage)
        }

    }


    // ---------- Admin reads votes ----------
    suspend fun getVotesForPoll(pollId: String): List<Map<String, Any>> {
        return try {
            val snapshot = db.collection("polls")
                .document(pollId)
                .collection("votes")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------- Mapping ----------

    private fun DocumentSnapshot.toPoll(): Poll? {
        if (!exists()) return null
        val data = data ?: return null
        val opts = (data["options"] as? List<*>)?.mapNotNull { raw ->
            val map = raw as? Map<*, *> ?: return@mapNotNull null
            val oid = map["id"] as? String ?: return@mapNotNull null
            val txt = map["text"] as? String ?: ""
            PollOption(id = oid, text = txt)
        } ?: emptyList()

        return Poll(
            id = id,
            title = (data["title"] as? String).orEmpty(),
            description = (data["description"] as? String).orEmpty(),
            options = opts,
            createdBy = (data["createdBy"] as? String).orEmpty(),
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
            closesAt = (data["closesAt"] as? Number)?.toLong(),
            isActive = (data["isActive"] as? Boolean) ?: true
        )
    }
}
