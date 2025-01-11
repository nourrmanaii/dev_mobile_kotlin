package com.nour.todo.user

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nour.todo.data.Api
import kotlinx.coroutines.launch

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }
            var userAvatarUrl: String? by remember { mutableStateOf(null) }
            val composeScope = rememberCoroutineScope()

            // Launcher pour capturer une photo avec la caméra
            val takePicture = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { capturedBitmap ->
                bitmap = capturedBitmap
                capturedBitmap?.let { bitmap ->
                    composeScope.launch {
                        try {
                            val avatarPart = bitmap.toRequestBody()
                            val response = Api.userWebService.updateAvatar(avatarPart)
                            if (response.isSuccessful) {
                                userAvatarUrl = response.body()?.avatar
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            // Launcher pour sélectionner une photo depuis le stockage
            val pickPhoto = rememberLauncherForActivityResult(
                contract = PickVisualMedia()
            ) { selectedUri ->
                uri = selectedUri
                selectedUri?.let { uri ->
                    composeScope.launch {
                        try {
                            val avatarPart = uri.toRequestBody(this@UserActivity)
                            val response = Api.userWebService.updateAvatar(avatarPart)
                            if (response.isSuccessful) {
                                userAvatarUrl = response.body()?.avatar
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            // Launcher pour demander la permission READ_EXTERNAL_STORAGE
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    // Permission accordée
                    pickPhoto.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                } else {
                    // Permission refusée
                    composeScope.launch {
                        println("Permission refusée")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Afficher l'image capturée ou sélectionnée
                AsyncImage(
                    model = userAvatarUrl ?: bitmap ?: uri,
                    modifier = Modifier.fillMaxHeight(0.2f),
                    contentDescription = "User Avatar"
                )
                // Bouton pour capturer une photo
                Button(
                    onClick = { takePicture.launch() },
                    content = { Text("Take picture") }
                )
                // Bouton pour choisir une photo
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= 29) {
                            // API >= 29 : lancer directement le sélecteur de fichiers
                            pickPhoto.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                        } else {
                            // API < 29 : demander la permission avant d'accéder aux fichiers
                            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    content = { Text("Pick photo") }
                )
                Button(
                    onClick = { finish() }, // Ferme l'activité actuelle et revient à la précédente
                    content = { Text("Back") }
                )
            }
        }
    }
}
