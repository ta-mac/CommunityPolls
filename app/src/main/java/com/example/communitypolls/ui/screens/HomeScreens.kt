package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communitypolls.ui.polls.PollListRoute

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
    onPollClick: (String) -> Unit
) = HomeScaffold(
    title = "Admin Home",
    onSignOut = onSignOut,
    actions = { Button(onClick = onCreatePoll) { Text("Create poll") } }
) {
    PollListRoute(onPollClick = onPollClick)
}
