package com.nour.todo.user

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class UserActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(0.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        // Action pour prendre une photo
                        // Utilisez CameraX ou un Intent pour capturer une image
                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        // Action pour choisir une photo
                        // Utilisez Intent ACTION_PICK pour sélectionner une image depuis le gallery
                    },
                    content = { Text("Pick photo") }
                )
            }
        }
    }
}
