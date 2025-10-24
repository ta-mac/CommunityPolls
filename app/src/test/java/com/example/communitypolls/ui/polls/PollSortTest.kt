package com.example.communitypolls.ui.polls

import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import org.junit.Assert.assertEquals
import org.junit.Test

// Enum for sort options
enum class PollSort {
    NEWEST, OLDEST, TITLE_AZ, TITLE_ZA
}

// Sorting function
fun sortPolls(polls: List<Poll>, sort: PollSort): List<Poll> {
    return when (sort) {
        PollSort.NEWEST -> polls.sortedByDescending { it.createdAt }
        PollSort.OLDEST -> polls.sortedBy { it.createdAt }
        PollSort.TITLE_AZ -> polls.sortedBy { it.title.lowercase() }
        PollSort.TITLE_ZA -> polls.sortedByDescending { it.title.lowercase() }
    }
}

class PollSortTest {

    // Helper to create polls
    private fun p(id: String, title: String, createdAt: Long) = Poll(
        id = id,
        title = title,
        description = "",
        options = listOf(PollOption("a","A"), PollOption("b","B")),
        createdBy = "u1",
        createdAt = createdAt,
        closesAt = null,
        isActive = true
    )

    private val items = listOf(
        p("1", "Banana", 1700000000000),
        p("2", "apple",  1800000000000), // lower-case to test case-insensitive title
        p("3", "Cherry", 1600000000000)
    )

    @Test fun newest_first() {
        val sorted = sortPolls(items, PollSort.NEWEST)
        assertEquals(listOf("2","1","3"), sorted.map { it.id })
    }

    @Test fun oldest_first() {
        val sorted = sortPolls(items, PollSort.OLDEST)
        assertEquals(listOf("3","1","2"), sorted.map { it.id })
    }

    @Test fun title_az() {
        val sorted = sortPolls(items, PollSort.TITLE_AZ)
        // apple (2), Banana (1), Cherry (3)
        assertEquals(listOf("2","1","3"), sorted.map { it.id })
    }

    @Test fun title_za() {
        val sorted = sortPolls(items, PollSort.TITLE_ZA)
        // Cherry (3), Banana (1), apple (2)
        assertEquals(listOf("3","1","2"), sorted.map { it.id })
    }
}
