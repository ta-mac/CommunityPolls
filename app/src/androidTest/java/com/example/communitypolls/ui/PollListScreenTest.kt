package com.example.communitypolls.ui.polls  // ✅ make sure it matches file path

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.communitypolls.model.Poll
import com.example.communitypolls.model.PollOption
import org.junit.Rule
import org.junit.Test

// ✅ Match your actual PollListUiState data class structure
data class PollListUiState(
    val items: List<Poll> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

enum class PollSort { NEWEST, OLDEST, TITLE_AZ, TITLE_ZA }

class PollListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pollListScreen_displaysEmptyState() {
        composeTestRule.setContent {
            PollListScreen(
                state = PollListUiState(items = emptyList(), loading = false, error = null),
                onRetry = {},
                onPollClick = {},
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = PollSort.NEWEST,
                onCreatePoll = {},
                externalSearchQuery = "",
                onViewVotes = { _, _ -> }
            )
        }

        // ✅ Matches the actual text from EmptyState in PollListScreen
        composeTestRule.onNodeWithText("No polls yet").assertExists()
    }

    @Test
    fun pollListScreen_displaysPollTitle() {
        val poll = Poll(
            id = "1",
            title = "Demo Poll",
            description = "A test poll",
            options = listOf(PollOption("1", "Option 1")),
            createdBy = "admin",
            createdAt = System.currentTimeMillis(),
            closesAt = null,
            isActive = true
        )

        composeTestRule.setContent {
            PollListScreen(
                state = PollListUiState(items = listOf(poll), loading = false, error = null),
                onRetry = {},
                onPollClick = {},
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = PollSort.NEWEST,
                onCreatePoll = {},
                externalSearchQuery = "",
                onViewVotes = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("Demo Poll").assertExists()
    }
}
