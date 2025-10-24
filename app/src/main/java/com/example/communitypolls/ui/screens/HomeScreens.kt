<<<<<<< HEAD
<<<<<<< HEAD
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
package com.example.communitypolls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.communitypolls.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
=======
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
import androidx.compose.ui.unit.dp
import com.example.communitypolls.ui.ServiceLocator
import com.example.communitypolls.ui.polls.PollListRoute
import com.example.communitypolls.ui.polls.PollSort
import kotlinx.coroutines.launch

<<<<<<< HEAD
<<<<<<< HEAD
/* ---------------------------- SHARED TOP BAR ---------------------------- */

@Composable
private fun PollsTopBar(
    onSuggestClick: () -> Unit,
    onSortClick: () -> Unit,
    sort: PollSort,
    onSignOut: () -> Unit,
    showSort: Boolean = true,
    suggestLabel: String = "Suggest"
) {
    TopAppBar(
        title = { Text("Polls", style = MaterialTheme.typography.titleLarge) },
        actions = {
            AssistChip(
                onClick = onSuggestClick,
                label = { Text(suggestLabel) },
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                shape = RoundedCornerShape(50)
            )
            if (showSort) {
                AssistChip(
                    onClick = onSortClick,
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
            TextButton(onClick = onSignOut) {
                Text("Sign Out", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

/* ------------------------------- GUEST ------------------------------- */
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    title: String,
    onSignOut: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                actions = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        actions()
                        TextButton(onClick = onSignOut) { Text("Sign out") }
                    }
                }
            )
        }
    ) { padding -> Box(Modifier.padding(padding).fillMaxSize()) { content() } }
}

/* ------------------------------- Sort menu ------------------------------- */

@Composable
private fun SortMenu(
    sort: PollSort,
    onChange: (PollSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Sort, contentDescription = "Sort")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            @Composable
            fun item(label: String, value: PollSort) {
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { expanded = false; onChange(value) },
                    trailingIcon = { if (sort == value) Text("•") else null }
                )
            }
            item("Newest", PollSort.NEWEST)
            item("Oldest", PollSort.OLDEST)
            item("A → Z",  PollSort.TITLE_AZ)
            item("Z → A",  PollSort.TITLE_ZA)
        }
    }
}

/* ------------------------------- Guest ------------------------------- */
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
/* ---------------------------- SHARED TOP BAR ---------------------------- */

@Composable
private fun PollsTopBar(
    onSuggestClick: () -> Unit,
    onSortClick: () -> Unit,
    sort: PollSort,
    onSignOut: () -> Unit,
    showSort: Boolean = true,
    suggestLabel: String = "Suggest"
) {
    TopAppBar(
        title = { Text("Polls", style = MaterialTheme.typography.titleLarge) },
        actions = {
            AssistChip(
                onClick = onSuggestClick,
                label = { Text(suggestLabel) },
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                shape = RoundedCornerShape(50)
            )
            if (showSort) {
                AssistChip(
                    onClick = onSortClick,
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
            TextButton(onClick = onSignOut) {
                Text("Sign Out", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

/* ------------------------------- GUEST ------------------------------- */
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)

@Composable
fun HomeGuestScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

<<<<<<< HEAD
<<<<<<< HEAD
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("Suggest Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort
            )
        }
    }
}


/* ------------------------------- USER ------------------------------- */
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
    HomeScaffold(
        title = "Community Polls",
        onSignOut = onSignOut,
        actions = {
            Button(onClick = onSuggestClick) { Text("Suggest a poll") }
            SortMenu(sort = sort, onChange = { sort = it })
        }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = false,
            sort = sort
        )
    }
}

/* -------------------------------- User ------------------------------- */
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("Suggest Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort
            )
        }
    }
}


/* ------------------------------- USER ------------------------------- */
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)

@Composable
fun HomeUserScreen(
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onSuggestClick: () -> Unit
) {
    var sort by remember { mutableStateOf(PollSort.NEWEST) }

<<<<<<< HEAD
<<<<<<< HEAD
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("Suggest Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Poll list
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort
            )
        }
    }
}




/* ------------------------------- ADMIN ------------------------------- */
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
    HomeScaffold(
        title = "Community Polls",
        onSignOut = onSignOut,
        actions = {
            Button(onClick = onSuggestClick) { Text("Suggest a poll") }
            SortMenu(sort = sort, onChange = { sort = it })
        }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = false,
            sort = sort
        )
    }
}

/* ------------------------------- Admin ------------------------------- */
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("Suggest Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "Newest" else "Oldest"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Poll list
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = false,
                onEditPoll = {},
                onDeletePoll = {},
                sort = sort
            )
        }
    }
}




/* ------------------------------- ADMIN ------------------------------- */
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)

@Composable
fun HomeAdminScreen(
    onCreatePoll: () -> Unit,
    onSignOut: () -> Unit,
    onPollClick: (String) -> Unit,
    onEditPoll: (String) -> Unit,
<<<<<<< HEAD
<<<<<<< HEAD
    onSuggestClick: () -> Unit
=======
    onSuggestClick: () -> Unit // opens the review list
>>>>>>> 0af30b8 (Added some security measures)
=======
    onSuggestClick: () -> Unit // opens the review list
=======
    onSuggestClick: () -> Unit
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
) {
    val repo = ServiceLocator.pollRepository
    val scope = rememberCoroutineScope()
    var sort by remember { mutableStateOf(PollSort.NEWEST) }
<<<<<<< HEAD
<<<<<<< HEAD
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("User Polls") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.weight(1f))

                AssistChip(
                    onClick = onCreatePoll,
                    label = { Text("Create Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.weight(1f))

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "New" else "Old"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        }
        ,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = true,
                onEditPoll = onEditPoll,
                onDeletePoll = { id -> pendingDeleteId = id },
                sort = sort
            )
        }
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)

    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    HomeScaffold(
        title = "Admin • Community Polls",
        onSignOut = onSignOut,
        actions = {
            Button(onClick = onSuggestClick) { Text("User Poll") }
            Button(onClick = onCreatePoll) { Text("Create poll") }
            SortMenu(sort = sort, onChange = { sort = it })
        }
    ) {
        PollListRoute(
            onPollClick = onPollClick,
            showAdminActions = true,
            onEditPoll = onEditPoll,
            onDeletePoll = { id -> pendingDeleteId = id },
            sort = sort
        )
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onSuggestClick,
                    label = { Text("User Polls") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.weight(1f))

                AssistChip(
                    onClick = onCreatePoll,
                    label = { Text("Create Poll") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.weight(1f))

                AssistChip(
                    onClick = {
                        sort = if (sort == PollSort.NEWEST) PollSort.OLDEST else PollSort.NEWEST
                    },
                    label = { Text("Sort: ${if (sort == PollSort.NEWEST) "New" else "Old"}") },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null) },
                    shape = RoundedCornerShape(50)
                )
            }
        }
        ,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onSignOut) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PollListRoute(
                onPollClick = onPollClick,
                showAdminActions = true,
                onEditPoll = onEditPoll,
                onDeletePoll = { id -> pendingDeleteId = id },
                sort = sort
            )
        }
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { if (!deleting) pendingDeleteId = null },
            confirmButton = {
                TextButton(
                    enabled = !deleting,
                    onClick = {
                        val id = pendingDeleteId ?: return@TextButton
                        scope.launch {
                            deleting = true
                            try {
                                repo.deletePoll(id)
                                pendingDeleteId = null
                            } finally {
                                deleting = false
                            }
                        }
                    }
<<<<<<< HEAD
<<<<<<< HEAD
                ) {
                    Text(if (deleting) "Deleting…" else "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !deleting,
                    onClick = { pendingDeleteId = null }
                ) {
=======
=======
>>>>>>> 5f6ea81 (Updated App Icon)
                ) { Text(if (deleting) "Deleting…" else "Delete") }
            },
            dismissButton = {
                TextButton(enabled = !deleting, onClick = { pendingDeleteId = null }) {
<<<<<<< HEAD
>>>>>>> 0af30b8 (Added some security measures)
=======
=======
                ) {
                    Text(if (deleting) "Deleting…" else "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !deleting,
                    onClick = { pendingDeleteId = null }
                ) {
>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
                    Text("Cancel")
                }
            },
            title = { Text("Delete poll") },
            text = { Text("Are you sure you want to delete this poll? This cannot be undone.") }
        )
    }
}
<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> 0af30b8 (Added some security measures)
=======
=======

>>>>>>> 71da6fb (Updated App Icon)
>>>>>>> 5f6ea81 (Updated App Icon)
