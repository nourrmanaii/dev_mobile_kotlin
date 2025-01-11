package com.nour.todo.user

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nour.todo.viewmodel.UserViewModel

class UserActivity : ComponentActivity() {

    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userViewModel: UserViewModel = viewModel() // Instancier le ViewModel
            val userAvatarUrl by userViewModel.userAvatarUrl.collectAsState()
            val error by userViewModel.error.collectAsState()

            var uri: Uri? by remember { mutableStateOf(null) }
            val userName by userViewModel.userName.collectAsState()
            var updatedName by remember { mutableStateOf(userName) }

            // Charger les informations utilisateur au démarrage
            LaunchedEffect(Unit) {
                userViewModel.fetchUser()
            }

            // Launcher pour capturer une photo en haute qualité
            val takePicture = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success) {
                    uri = captureUri
                    uri?.let { userViewModel.updateAvatar(this, it) }
                }
            }

            // Launcher pour sélectionner une photo depuis le stockage
            val pickPhoto = rememberLauncherForActivityResult(
                contract = PickVisualMedia()
            ) { selectedUri ->
                uri = selectedUri
                uri?.let { userViewModel.updateAvatar(this, it) }
            }

            // Launcher pour demander la permission READ_EXTERNAL_STORAGE
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    pickPhoto.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                }
            }

            // Charger les informations utilisateur au démarrage
            LaunchedEffect(Unit) {
                userViewModel.fetchUser()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Afficher l'erreur (s'il y en a)
                if (error != null) {
                    Text(text = error ?: "Erreur inconnue", color = MaterialTheme.colorScheme.error)
                }
                // Afficher l'avatar utilisateur
                AsyncImage(
                    model = userAvatarUrl ?: uri,
                    modifier = Modifier.fillMaxHeight(0.2f),
                    contentDescription = "User Avatar"
                )
                // Champ pour modifier le nom d'utilisateur
                TextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Nom d'utilisateur") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        userViewModel.updateUser(updatedName) // Appeler la méthode ViewModel
                    },
                    content = { Text("Mettre à jour") }
                )
                // Bouton pour capturer une photo
                Button(
                    onClick = {
                        captureUri?.let {
                            takePicture.launch(it)
                        }
                    },
                    content = { Text("Take picture") }
                )
                // Bouton pour choisir une photo
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= 29) {
                            pickPhoto.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                        } else {
                            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    content = { Text("Pick photo") }
                )
                // Bouton pour revenir en arrière
                Button(
                    onClick = { finish() },
                    content = { Text("Back") }
                )
            }
        }
    }
}
