import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nour.todo.data.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    private val webService = Api.tasksWebService

    val tasksStateFlow = MutableStateFlow<List<Task>>(emptyList())

    fun refresh() {
        viewModelScope.launch {
            val response = webService.fetchTasks()
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            tasksStateFlow.value = response.body()!!
        }
    }

    fun add(task: Task) {
        viewModelScope.launch {
            val response = webService.create(task)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }

            val createdTask = response.body()!!
            tasksStateFlow.value = tasksStateFlow.value + createdTask
        }
    }

    fun update(task: Task) {
        viewModelScope.launch {
            val response = webService.update(task)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            val updatedTask = response.body()!!
            tasksStateFlow.value = tasksStateFlow.value.map {
                if (it.id == updatedTask.id) updatedTask else it
            }
        }
    }

    fun remove(task: Task) {
        viewModelScope.launch {
            val response = webService.delete(task.id)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }

            tasksStateFlow.value = tasksStateFlow.value.filter { it.id != task.id }
        }
    }
}
