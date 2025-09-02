package com.example.communitypolls.model

/**
 * A selectable option on a poll.
 * id: a stable key for the option (e.g., "opt1", "yes", "no")
 * text: what the user sees
 */
data class PollOption(
    val id: String = "",
    val text: String = ""
)

/**
 * Poll document stored at: polls/{pollId}
 *
 * Notes:
 * - `id` is NOT stored in Firestore; it's attached from the document id on read.
 * - `options` is stored as a list of maps [{id, text}, ...] for easy reads.
 * - `createdAt` is millis since epoch (System.currentTimeMillis()).
 * - `closesAt` can be null for “no deadline”.
 */
data class Poll(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val options: List<PollOption> = emptyList(),
    val createdBy: String = "",
    val createdAt: Long = 0L,
    val closesAt: Long? = null,
    val isActive: Boolean = true
)

/** Helper to write a Poll (without id) to Firestore. */
fun Poll.toMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "description" to description,
    "options" to options.map { mapOf("id" to it.id, "text" to it.text) },
    "createdBy" to createdBy,
    "createdAt" to createdAt,
    "closesAt" to closesAt,
    "isActive" to isActive
)
