package com.example.communitypolls.ui.polls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator

@Composable
fun PollVoteRoute(
    pollId: String,
    onClose: () -> Unit,
    onViewResults: (String) -> Unit = {} // NEW
) {
    val repo = ServiceLocator.pollRepository
    val vm: PollVoteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PollVoteViewModel(repo, pollId) as T
            }
        }
    )
    val ui by vm.ui.collectAsState()

    when {
        ui.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        ui.poll == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Poll not found")
        }
        else -> {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text(ui.poll!!.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                if (ui.poll!!.description.isNotBlank()) {
                    Text(ui.poll!!.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                }
                ui.poll!!.options.forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = ui.selectedOptionId == opt.id,
                                onClick = { vm.select(opt.id) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = ui.selectedOptionId == opt.id,
                            onClick = { vm.select(opt.id) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(opt.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (ui.error != null) {
                    Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = vm::submit,
                    enabled = !ui.submitting && ui.selectedOptionId != null && (ui.poll?.isActive == true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (ui.submitting) CircularProgressIndicator(strokeWidth = 2.dp)
                    else Text("Submit vote")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { onViewResults(pollId) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("View results") }

                if (ui.submitted) {
                    Spacer(Modifier.height(12.dp))
                    Text("Thanks! Your vote was recorded.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
