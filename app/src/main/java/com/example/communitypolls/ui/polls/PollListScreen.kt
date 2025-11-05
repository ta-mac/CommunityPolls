package com.example.communitypolls.ui.polls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.model.Poll
import java.text.SimpleDateFormat
import androidx.compose.foundation.shape.RoundedCornerShape

import java.util.*

@Composable
fun PollListRoute(
    onPollClick: (String) -> Unit = {},
    limit: Int = 50,
    showAdminActions: Boolean = false,
    onEditPoll: (String) -> Unit = {},
    onDeletePoll: (String) -> Unit = {},
    onCreatePoll: (() -> Unit)? = null,
    sort: PollSort = PollSort.NEWEST,
    searchQuery: String = "",
    onViewVotes: (String, String) -> Unit = { _, _ -> } // NEW callback
) {
    val vm: PollListViewModel = viewModel(factory = PollVmFactory(limit))
    val state by vm.state.collectAsState()

    PollListScreen(
        state = state,
        onRetry = { vm.refresh() },
        onPollClick = onPollClick,
        showAdminActions = showAdminActions,
        onEditPoll = onEditPoll,
        onDeletePoll = onDeletePoll,
        onCreatePoll = onCreatePoll,
        sort = sort,
        externalSearchQuery = searchQuery,
        onViewVotes = onViewVotes // NEW
    )
}

@Composable
fun PollListScreen(
    state: PollListUiState,
    onRetry: () -> Unit,
    onPollClick: (String) -> Unit,
    showAdminActions: Boolean,
    onEditPoll: (String) -> Unit,
    onDeletePoll: (String) -> Unit,
    onCreatePoll: (() -> Unit)?,
    sort: PollSort,
    externalSearchQuery: String,
    onViewVotes: (String, String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.loading -> LoadingState()
            state.error != null -> ErrorState(message = state.error!!, onRetry = onRetry)
            state.items.isEmpty() -> EmptyState(onCreatePoll)
            else -> {
                val itemsSorted = remember(state.items, sort) {
                    when (sort) {
                        PollSort.NEWEST -> state.items.sortedByDescending { it.createdAt }
                        PollSort.OLDEST -> state.items.sortedBy { it.createdAt }
                        PollSort.TITLE_AZ -> state.items.sortedBy { it.title.lowercase() }
                        PollSort.TITLE_ZA -> state.items.sortedByDescending { it.title.lowercase() }
                    }
                }
                val filteredItems = remember(itemsSorted, externalSearchQuery) {
                    if (externalSearchQuery.isBlank()) itemsSorted
                    else {
                        val q = externalSearchQuery.trim()
                        itemsSorted.filter { poll ->
                            poll.title.contains(q, ignoreCase = true) ||
                                    poll.description.contains(q, ignoreCase = true)
                        }
                    }
                }

                PollList(
                    items = filteredItems,
                    onPollClick = onPollClick,
                    showAdminActions = showAdminActions,
                    onEditPoll = onEditPoll,
                    onDeletePoll = onDeletePoll,
                    onViewVotes = onViewVotes // pass callback
                )
            }
        }
    }
}

@Composable
private fun PollList(
    items: List<Poll>,
    onPollClick: (String) -> Unit,
    showAdminActions: Boolean,
    onEditPoll: (String) -> Unit,
    onDeletePoll: (String) -> Unit,
    onViewVotes: (String, String) -> Unit
)
{
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, start = 12.dp, end = 12.dp, top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { poll ->
            PollItemCard(
                poll = poll,
                onClick = { onPollClick(poll.id) },
                showAdminActions = showAdminActions,
                onEdit = { onEditPoll(poll.id) },
                onDelete = { onDeletePoll(poll.id) },
                onViewVotes = onViewVotes
            )
        }
    }
}

@Composable
private fun PollItemCard(
    poll: Poll,
    onClick: () -> Unit,
    showAdminActions: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewVotes: (String, String) -> Unit
) {
    val now = System.currentTimeMillis()
    val isActive = poll.isActive && (poll.closesAt == null || poll.closesAt > now)
    val statusColor = if (isActive) Color(0xFF22C55E) else Color(0xFFF44336)
    val statusText = if (isActive) "Active" else "Closed"
    val optionsText = "${poll.options.size} option${if (poll.options.size == 1) "" else "s"}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                poll.title.ifBlank { "(Untitled poll)" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // Description
            Text(
                poll.description.ifBlank { "No description" },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            // Status row
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(color = statusColor)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$statusText • $optionsText",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )

                poll.closesAt?.let { closeMillis ->
                    val sdf = remember {
                        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    }
                    val formatted = sdf.format(Date(closeMillis))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Closes: $formatted",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Buttons (compact + accent highlight for "View Votes")
            if (showAdminActions) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 36.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val buttonModifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                    val textStyle = MaterialTheme.typography.labelLarge.copy(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )

                    OutlinedButton(
                        onClick = onEdit,
                        modifier = buttonModifier,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(50)
                    ) { Text("Edit", style = textStyle) }

                    OutlinedButton(
                        onClick = onDelete,
                        modifier = buttonModifier,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(50)
                    ) { Text("Delete", style = textStyle) }

                    // Accent "View Votes" button
                    Button(
                        onClick = { onViewVotes(poll.id, poll.title) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("View Votes", style = textStyle)
                    }
                }
            }
        }
    }
}


@Composable
private fun StatusDot(color: Color, size: Dp = 10.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(MaterialTheme.shapes.small)
            .background(color)
    )
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Loading polls…")
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun EmptyState(onCreatePoll: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🗒️", style = MaterialTheme.typography.headlineLarge)
                Text("No polls yet", style = MaterialTheme.typography.titleLarge)
                Text("Create your first poll to get started!", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                if (onCreatePoll != null) {
                    Button(onClick = onCreatePoll) { Text("+ Create Poll") }
                }
            }
        }
    }
}

