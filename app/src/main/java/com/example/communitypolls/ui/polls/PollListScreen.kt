package com.example.communitypolls.ui.polls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.model.Poll

/**
 * Plug-and-play route for showing the poll list.
 * We construct the VM here so you can just drop this into a screen.
 */
@Composable
fun PollListRoute(
    onPollClick: (String) -> Unit = {},
    limit: Int = 50
) {
    val vm: PollListViewModel = viewModel(factory = PollVmFactory(limit))
    val state by vm.state.collectAsState()

    PollListScreen(
        state = state,
        onRetry = { vm.refresh() },
        onPollClick = onPollClick
    )
}

/** Pure UI: render the list and states */
@Composable
fun PollListScreen(
    state: PollListUiState,
    onRetry: () -> Unit,
    onPollClick: (String) -> Unit
) {
    when {
        state.loading -> LoadingState()
        state.error != null -> ErrorState(message = state.error!!, onRetry = onRetry)
        else -> PollList(items = state.items, onPollClick = onPollClick)
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Loading polls…")
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Refresh") }
    }
}

@Composable
private fun PollList(
    items: List<Poll>,
    onPollClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(items, key = { it.id }) { poll ->
            PollItemCard(poll = poll, onClick = { onPollClick(poll.id) })
            Divider()
        }
    }
}

@Composable
private fun PollItemCard(
    poll: Poll,
    onClick: () -> Unit
) {
    val subtitle = remember(poll.description) {
        poll.description.ifBlank { "No description" }
    }
    val optionsSummary = remember(poll.options) {
        "${poll.options.size} option${if (poll.options.size == 1) "" else "s"}"
    }
    val statusText = remember(poll) { if (poll.isActive) "Active" else "Closed" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                poll.title.ifBlank { "(Untitled poll)" },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "$optionsSummary • $statusText",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
