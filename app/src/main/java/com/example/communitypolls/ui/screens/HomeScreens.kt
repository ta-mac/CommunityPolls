package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.ServiceLocator
import kotlinx.coroutines.launch

@Composable
private fun HomeScaffold(
    title: String,
    onSignOut: () -> Unit,
    actions: @Composable () -> Unit = {},
    body: @Composable () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            OutlinedButton(onClick = onSignOut) { Text("Sign out") }
        }
        Spacer(Modifier.height(12.dp))
        actions()
        Spacer(Modifier.height(12.dp))
        Box(Modifier.weight(1f).fillMaxWidth()) { body() }
    }
}

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit
) = HomeScaffold(
    title = "Guest Home",
    onSignOut = onSignOut
) {
    PollListRoute(onPollClick = onPollClick)
}

@Composable
fun HomeUserScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit
) = HomeScaffold(
    title = "User Home",
    onSignOut = onSignOut
) {
    PollListRoute(onPollClick = onPollClick)
}

@Composable
fun HomeAdminScreen(
    onSignOut: () -> Unit,
    onCreatePoll: () -> Unit,
    onPollClick: (String) -> Unit,
    onEditPoll: (String) -> Unit
) {
    val repo = ServiceLocator.pollRepository
    val scope = rememberCoroutineScope()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    HomeScaffold(
        title = "Admin Home",
        onSignOut = onSignOut,
        actions = { Button(onClick = onCreatePoll) { Text("Create poll") } }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = true,
            onEditPoll = onEditPoll,
            onDeletePoll = { id -> pendingDeleteId = id }
        )

        if (pendingDeleteId != null) {
            AlertDialog(
                onDismissRequest = { if (!deleting) pendingDeleteId = null },
                confirmButton = {
                    Button(enabled = !deleting, onClick = {
                        val id = pendingDeleteId ?: return@Button
                        deleting = true
                        scope.launch {
                            repo.deletePoll(id)
                            deleting = false
                            pendingDeleteId = null
                        }
                    }) { Text(if (deleting) "Deleting…" else "Delete") }
                },
                dismissButton = {
                    TextButton(enabled = !deleting, onClick = { pendingDeleteId = null }) { Text("Cancel") }
                },
                title = { Text("Delete poll") },
                text = { Text("Are you sure you want to delete this poll? This cannot be undone.") }
            )
        }
    }
}
