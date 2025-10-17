package com.example.communitypolls.ui.polls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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

@Composable
fun PollListRoute(
    onPollClick: (String) -> Unit = {},
    limit: Int = 50,
    showAdminActions: Boolean = false,
    onEditPoll: (String) -> Unit = {},
    onDeletePoll: (String) -> Unit = {},
    onCreatePoll: (() -> Unit)? = null,
    sort: PollSort = PollSort.NEWEST
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
        sort = sort
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollListScreen(
    state: PollListUiState,
    onRetry: () -> Unit,
    onPollClick: (String) -> Unit,
    showAdminActions: Boolean,
    onEditPoll: (String) -> Unit,
    onDeletePoll: (String) -> Unit,
    onCreatePoll: (() -> Unit)?,
    sort: PollSort
) {
    // Wrap in Scaffold to add TopAppBar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Polls", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                state.loading -> LoadingState()
                state.error != null -> ErrorState(message = state.error!!, onRetry = onRetry)
                state.items.isEmpty() -> EmptyState(onCreatePoll)
                else -> {
                    val itemsSorted = remember(state.items, sort) {
                        when (sort) {
                            PollSort.NEWEST   -> state.items.sortedByDescending { it.createdAt }
                            PollSort.OLDEST   -> state.items.sortedBy { it.createdAt }
                            PollSort.TITLE_AZ -> state.items.sortedBy { it.title.lowercase() }
                            PollSort.TITLE_ZA -> state.items.sortedByDescending { it.title.lowercase() }
                        }
                    }
                    PollList(
                        items = itemsSorted,
                        onPollClick = onPollClick,
                        showAdminActions = showAdminActions,
                        onEditPoll = onEditPoll,
                        onDeletePoll = onDeletePoll
                    )
                }
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
    onDeletePoll: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { poll ->
            PollItemCard(
                poll = poll,
                onClick = { onPollClick(poll.id) },
                showAdminActions = showAdminActions,
                onEdit = { onEditPoll(poll.id) },
                onDelete = { onDeletePoll(poll.id) }
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
    onDelete: () -> Unit
) {
    val subtitle = poll.description.ifBlank { "No description" }
    val optionsSummary = "${poll.options.size} option${if (poll.options.size == 1) "" else "s"}"
    val statusText = if (poll.isActive) "Active" else "Closed"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with title and overflow icon
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(
                    poll.title.ifBlank { "(Untitled poll)" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(6.dp))

            // Description
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            // Status + options summary
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(color = if (poll.isActive) Color(0xFF22C55E) else MaterialTheme.colorScheme.outline)
                Spacer(Modifier.width(6.dp))
                Text(
                    "$statusText • $optionsSummary",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Admin action buttons
            if (showAdminActions) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
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
private fun EmptyState(onCreatePoll: (() -> Unit)?) {
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
                Text("No polls yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                if (onCreatePoll != null) {
                    Button(onClick = onCreatePoll) { Text("+ Create Poll") }
                }
            }
        }
    }
}
