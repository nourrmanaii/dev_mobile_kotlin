package com.nour.todo.detail

import Task
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.UUID

class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération de la tâche passée en argument (pour l'édition)
        val initialTask = intent.getSerializableExtra(TASK_KEY) as? Task

        setContent {
            DetailScreen(
                initialTask = initialTask,
                onSave = { task ->
                    val intent = Intent().apply {
                        putExtra(TASK_KEY, task)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                },
                onCancel = {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            )
        }
    }

    companion object {
        const val TASK_KEY = "task"
    }
}

@Composable
fun DetailScreen(
    initialTask: Task?,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current // Récupère le contexte

    var task by remember {
        mutableStateOf(
            initialTask ?: Task(
                id = UUID.randomUUID().toString(),
                title = "",
                description = ""
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialTask == null) "Ajouter une nouvelle tâche" else "Modifier la tâche",
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedTextField(
            value = task.title,
            onValueChange = { task = task.copy(title = it) },
            label = { Text("Titre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = task.description,
            onValueChange = { task = task.copy(description = it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onSave(task) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            OutlinedButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Task: ${task.title}\nDescription: ${task.description}"
                        )
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share Task via")
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Share")
            }
        }
    }
}


@Preview
@Composable
fun PreviewDetailScreen() {
    DetailScreen(
        initialTask = null,
        onSave = {},
        onCancel = {}
    )
}