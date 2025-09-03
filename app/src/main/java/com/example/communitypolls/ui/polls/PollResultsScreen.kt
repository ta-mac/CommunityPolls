package com.example.communitypolls.ui.polls

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communitypolls.ui.ServiceLocator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollResultsRoute(
    pollId: String,
    onClose: () -> Unit
) {
    val repo = ServiceLocator.pollRepository
    val vm: PollResultsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PollResultsViewModel(repo, pollId) as T
            }
        }
    )
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current

    fun buildCsv(): String {
        val title = ui.poll?.title ?: "Poll"
        val header = "Option,Votes,Percent\n"
        val rows = ui.results.joinToString("\n") { r ->
            val pct = (r.pct * 100).toInt()
            "\"${r.label.replace("\"","\"\"")}\",${r.count},$pct%"
        }
        return "Title,\"$title\"\nTotal Votes,${ui.total}\n\n$header$rows\n"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
                navigationIcon = { TextButton(onClick = onClose) { Text("Back") } }
            )
        }
    ) { pad ->
        when {
            ui.loading -> Box(
                Modifier.fillMaxSize().padding(pad),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            ui.poll == null -> Box(
                Modifier.fillMaxSize().padding(pad),
                contentAlignment = Alignment.Center
            ) { Text(ui.error ?: "Poll not found") }

            else -> Column(
                Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(ui.poll!!.title, style = MaterialTheme.typography.titleLarge)
                if (ui.poll!!.description.isNotBlank()) {
                    Text(ui.poll!!.description, style = MaterialTheme.typography.bodyMedium)
                }
                Text("Total votes: ${ui.total}", style = MaterialTheme.typography.labelLarge)

                // Export actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val csv = buildCsv()
                            val cm = context.getSystemService(ClipboardManager::class.java)
                            cm.setPrimaryClip(ClipData.newPlainText("poll_results.csv", csv))
                            Toast.makeText(context, "CSV copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Copy CSV") }

                    Button(
                        onClick = {
                            val csv = buildCsv()
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_SUBJECT, "Poll Results - ${ui.poll!!.title}")
                                putExtra(Intent.EXTRA_TEXT, csv)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share CSV"))
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Share CSV") }
                }

                // Bars
                ui.results.forEach { r ->
                    Column(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(r.label, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${r.count}  (${(r.pct * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        LinearProgressIndicator(
                            progress = { r.pct.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }
                }
            }
        }
    }
}
