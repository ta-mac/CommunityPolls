package com.example.communitypolls.data.poll

import android.util.Log
import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
<<<<<<< HEAD
import com.google.firebase.perf.FirebasePerformance              //  NEW: Performance monitoring
import com.google.firebase.perf.metrics.Trace                   //  NEW: Performance tracing
=======
>>>>>>> 0af30b8 (Added some security measures)
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebasePollRepository"

class FirebasePollRepository(
    private val db: FirebaseFirestore
) : PollRepository {

<<<<<<< HEAD
    // 🔥 NEW: In-memory cache to reduce reads
    private val pollCache = mutableMapOf<String, Poll>()

=======
>>>>>>> 0af30b8 (Added some security measures)
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
                    trySend(emptyList()); return@addSnapshotListener
                }
<<<<<<< HEAD

                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()

                // 🔥 NEW: Cache results locally
                list.forEach { pollCache[it.id] = it }

=======
                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()
>>>>>>> 0af30b8 (Added some security measures)
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
                        err.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
<<<<<<< HEAD
=======
                        // Missing composite index: use fallback so items don't vanish
>>>>>>> 0af30b8 (Added some security measures)
                        reg?.remove()
                        listenFallback()
                        trySend(emptyList())
                    } else {
                        trySend(emptyList())
                    }
                    return@addSnapshotListener
                }
<<<<<<< HEAD

                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()

                // 🔥 NEW: Cache results locally
                list.forEach { pollCache[it.id] = it }

=======
                val list = snap?.documents?.mapNotNull { it.toPoll() } ?: emptyList()
>>>>>>> 0af30b8 (Added some security measures)
                trySend(list)
            }
        }

<<<<<<< HEAD
=======
        // Start ordered (preferred); fall back if index is missing
>>>>>>> 0af30b8 (Added some security measures)
        listenOrdered()
        awaitClose { reg?.remove() }
    }

    override fun observePoll(pollId: String): Flow<Poll?> = callbackFlow {
<<<<<<< HEAD
        //  NEW: Serve from cache first if available
        pollCache[pollId]?.let { trySend(it) }

=======
>>>>>>> 0af30b8 (Added some security measures)
        val reg = polls.document(pollId).addSnapshotListener { snap, err ->
            if (err != null) {
                Log.e(TAG, "observePoll failed: ${err.message}", err)
                trySend(null); return@addSnapshotListener
            }
<<<<<<< HEAD
            val poll = snap?.toPoll()

            //  NEW: Update cache
            if (poll != null) pollCache[poll.id] = poll

            trySend(poll)
=======
            trySend(snap?.toPoll())
>>>>>>> 0af30b8 (Added some security measures)
        }
        awaitClose { reg.remove() }
    }

    override fun observeResults(pollId: String): Flow<Map<String, Int>> = callbackFlow {
        val reg = polls.document(pollId)
            .collection("votes")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e(TAG, "observeResults failed: ${err.message}", err)
                    trySend(emptyMap()); return@addSnapshotListener
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
<<<<<<< HEAD
        val trace: Trace = FirebasePerformance.getInstance().newTrace("create_poll_trace") // NEW: Trace start
        trace.start()

        val trimmedOptions = options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (title.isBlank()) return CreatePollResult.Error("Title is required")
        if (trimmedOptions.size < 2) return CreatePollResult.Error("Add at least two options")
=======
        val trimmedOptions = options.map { it.copy(id = it.id.trim(), text = it.text.trim()) }
        if (title.isBlank()) return CreatePollResult.Error("Title is required")
        if (trimmedOptions.size < 2) return CreatePollResult.Error("Add at least two options")
        if (trimmedOptions.any { it.id.isBlank() || it.text.isBlank() }) {
            return CreatePollResult.Error("Each option needs an id and text")
        }
>>>>>>> 0af30b8 (Added some security measures)

        val data = hashMapOf(
            "title" to title.trim(),
            "description" to description.trim(),
            "options" to trimmedOptions.map { mapOf("id" to it.id, "text" to it.text) },
            "createdBy" to createdByUid,
<<<<<<< HEAD
=======
            // Rules expect numeric (int/long), not a Firestore timestamp
>>>>>>> 0af30b8 (Added some security measures)
            "createdAt" to System.currentTimeMillis(),
            "closesAt" to closesAtMillis,
            "isActive" to isActive
        )

        return try {
            val doc = polls.document()
            doc.set(data).await()
<<<<<<< HEAD

            //  NEW: Add to cache
            val newPoll = Poll(doc.id, title, description, trimmedOptions, createdByUid, System.currentTimeMillis(), closesAtMillis, isActive)
            pollCache[newPoll.id] = newPoll

            trace.stop() //  NEW: Trace stop
            CreatePollResult.Success(doc.id)
        } catch (e: Exception) {
            trace.stop()
=======
            CreatePollResult.Success(doc.id)
        } catch (e: Exception) {
>>>>>>> 0af30b8 (Added some security measures)
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
<<<<<<< HEAD
        val trace = FirebasePerformance.getInstance().newTrace("update_poll_trace") //  NEW
        trace.start()

=======
>>>>>>> 0af30b8 (Added some security measures)
        val updates = HashMap<String, Any?>()
        title?.let { updates["title"] = it }
        description?.let { updates["description"] = it }
        options?.let {
            if (it.size < 2) return OpResult.Error("At least two options required")
            val cleaned = it.map { o ->
                val id = o.id.trim()
                val text = o.text.trim()
                if (id.isBlank() || text.isBlank()) return OpResult.Error("Option id/text required")
                mapOf("id" to id, "text" to text)
            }
            updates["options"] = cleaned
        }
        if (closesAtMillis != null) updates["closesAt"] = closesAtMillis
        if (isActive != null) updates["isActive"] = isActive

        return try {
            polls.document(pollId).update(updates as Map<String, Any>).await()
<<<<<<< HEAD

            pollCache.remove(pollId) // 🔥 NEW: Invalidate cache
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
=======
            OpResult.Success
        } catch (e: Exception) {
>>>>>>> 0af30b8 (Added some security measures)
            OpResult.Error(e.message ?: "Failed to update poll")
        }
    }

    override suspend fun deletePoll(pollId: String): OpResult {
<<<<<<< HEAD
        val trace = FirebasePerformance.getInstance().newTrace("delete_poll_trace") //  NEW
        trace.start()
        return try {
            polls.document(pollId).delete().await()
            pollCache.remove(pollId) //  NEW: Remove from cache
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
=======
        return try {
            polls.document(pollId).delete().await()
            OpResult.Success
        } catch (e: Exception) {
>>>>>>> 0af30b8 (Added some security measures)
            OpResult.Error(e.message ?: "Failed to delete poll")
        }
    }

    override suspend fun castVote(
        pollId: String,
        optionId: String,
        voterUid: String
    ): OpResult {
<<<<<<< HEAD
        val trace = FirebasePerformance.getInstance().newTrace("cast_vote_trace") //  NEW
        trace.start()
=======
>>>>>>> 0af30b8 (Added some security measures)
        return try {
            val vote = mapOf(
                "optionId" to optionId,
                "createdAt" to System.currentTimeMillis()
            )
            polls.document(pollId)
                .collection("votes")
<<<<<<< HEAD
                .document(voterUid)
                .set(vote)
                .await()
            trace.stop()
            OpResult.Success
        } catch (e: Exception) {
            trace.stop()
=======
                .document(voterUid) // one doc per voter -> prevents double vote
                .set(vote)          // will fail if already exists (per rules)
                .await()
            OpResult.Success
        } catch (e: Exception) {
>>>>>>> 0af30b8 (Added some security measures)
            OpResult.Error(e.message ?: "Failed to cast vote")
        }
    }

    // ---------- Mapping ----------

    private fun DocumentSnapshot.toPoll(): Poll? {
        if (!exists()) return null
        val data = data ?: return null
        val id = id
        val opts = (data["options"] as? List<*>)?.mapNotNull { raw ->
            val map = raw as? Map<*, *> ?: return@mapNotNull null
            val oid = map["id"] as? String ?: return@mapNotNull null
            val txt = map["text"] as? String ?: ""
            PollOption(id = oid, text = txt)
        } ?: emptyList()

        return Poll(
            id = id,
            title = (data["title"] as? String).orElseEmpty(),
            description = (data["description"] as? String).orElseEmpty(),
            options = opts,
            createdBy = (data["createdBy"] as? String).orElseEmpty(),
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
            closesAt = (data["closesAt"] as? Number)?.toLong(),
            isActive = (data["isActive"] as? Boolean) ?: true
        )
    }

    private fun String?.orElseEmpty(): String = this ?: ""
}
