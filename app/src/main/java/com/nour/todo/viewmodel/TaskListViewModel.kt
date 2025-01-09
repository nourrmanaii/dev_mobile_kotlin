package com.nour.todo.viewmodel

import Task
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nour.todo.data.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    private val webService = Api.tasksWebService

    // État observable contenant les tâches
    private val _tasksStateFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksStateFlow: StateFlow<List<Task>> get() = _tasksStateFlow

    // Rafraîchir la liste des tâches depuis l'API
    fun refresh() {
        viewModelScope.launch {
            try {
                val response = webService.fetchTasks()
                if (!response.isSuccessful) {
                    Log.e("Network", "Error: ${response.raw()}")
                    return@launch
                }
                _tasksStateFlow.value = response.body()!!
            } catch (e: Exception) {
                Log.e("Network", "Exception: ${e.message}")
            }
        }
    }

    // Ajouter une tâche
    fun add(task: Task) {
        viewModelScope.launch {
            try {
                val response = webService.create(task)
                if (!response.isSuccessful) {
                    Log.e("Network", "Error: ${response.raw()}")
                    return@launch
                }
                val newTask = response.body()!!
                _tasksStateFlow.value = _tasksStateFlow.value + newTask
            } catch (e: Exception) {
                Log.e("Network", "Exception: ${e.message}")
            }
        }
    }

    // Mettre à jour une tâche
    fun update(task: Task) {
        viewModelScope.launch {
            try {
                val response = webService.update(task)
                if (!response.isSuccessful) {
                    Log.e("Network", "Error: ${response.raw()}")
                    return@launch
                }
                val updatedTask = response.body()!!
                _tasksStateFlow.value = _tasksStateFlow.value.map {
                    if (it.id == updatedTask.id) updatedTask else it
                }
            } catch (e: Exception) {
                Log.e("Network", "Exception: ${e.message}")
            }
        }
    }

    // Supprimer une tâche
    fun remove(task: Task) {
        viewModelScope.launch {
            try {
                val response = webService.delete(task.id)
                if (!response.isSuccessful) {
                    Log.e("Network", "Error: ${response.raw()}")
                    return@launch
                }
                _tasksStateFlow.value = _tasksStateFlow.value.filter { it.id != task.id }
            } catch (e: Exception) {
                Log.e("Network", "Exception: ${e.message}")
            }
        }
    }
}