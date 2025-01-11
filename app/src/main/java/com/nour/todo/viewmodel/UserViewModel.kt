package com.nour.todo.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nour.todo.data.Api
import com.nour.todo.data.UserUpdate
import com.nour.todo.data.UserUpdateArgs
import com.nour.todo.data.UserUpdateCommand
import com.nour.todo.user.toRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel : ViewModel() {
    // StateFlow pour gérer l'URL de l'avatar utilisateur
    private val _userAvatarUrl = MutableStateFlow<String?>(null)
    val userAvatarUrl: StateFlow<String?> get() = _userAvatarUrl

    // StateFlow pour gérer les erreurs
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // StateFlow pour le nom d'utilisateur
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> get() = _userName
    fun setUserName(name: String) {
        _userName.value = name
    }

    // Récupérer les informations utilisateur depuis l'API
    fun fetchUser() {
        viewModelScope.launch {
            try {
                val response = Api.userWebService.fetchUser()
                if (response.isSuccessful) {
                    val user = response.body()!!
                    _userName.value = user.name
                    _userAvatarUrl.value = user.avatar
                } else {
                    _error.value = "Erreur : ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Erreur de récupération des informations utilisateur"
            }
        }
    }

    // Mettre à jour l'avatar utilisateur via l'API
    fun updateAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val avatarPart = uri.toRequestBody(context)
                val response = Api.userWebService.updateAvatar(avatarPart)
                if (response.isSuccessful) {
                    val user = response.body()!!
                    _userAvatarUrl.value = user.avatar
                } else {
                    _error.value = "Erreur : ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Erreur lors de la mise à jour de l'avatar"
            }
        }
    }

    fun updateUser(name: String) {
        viewModelScope.launch {
            try {
                // Créez l'objet UserUpdate
                val userUpdate = UserUpdate(
                    commands = listOf(
                        UserUpdateCommand(
                            uuid = UUID.randomUUID().toString(),
                            args = UserUpdateArgs(name = name)
                        )
                    )
                )

                // Appeler l'API pour mettre à jour l'utilisateur
                val response = Api.userWebService.update(userUpdate)
                if (response.isSuccessful) {
                    // Recharger les informations utilisateur pour obtenir les données à jour
                    fetchUser()
                    _error.value = null // Effacer les erreurs
                    Log.d("UserViewModel", "Mise à jour réussie : $name")
                } else {
                    // Gérer les erreurs de réponse
                    _error.value = "Erreur API : ${response.code()} - ${response.message()}"
                    Log.e("UserViewModel", "Erreur API : ${response.message()}")
                }
            } catch (e: Exception) {
                // Gérer les exceptions réseau ou autres
                Log.e("UserViewModel", "Exception lors de la mise à jour", e)
                _error.value = "Erreur réseau : ${e.localizedMessage}"
            }
        }
    }

}
