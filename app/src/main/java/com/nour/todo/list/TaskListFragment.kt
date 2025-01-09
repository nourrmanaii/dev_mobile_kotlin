package com.nour.todo.list

import Task
import TaskListViewModel
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nour.todo.R
import com.nour.todo.databinding.FragmentTaskListBinding
import com.nour.todo.detail.DetailActivity
import kotlinx.coroutines.launch
import coil.load
import com.nour.todo.data.Api

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    private val adapter = TaskListAdapter(object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.remove(task)
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.TASK_KEY, task)
            }
            editTaskLauncher.launch(intent)
        }
    })

    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newTask = result.data?.getSerializableExtra(DetailActivity.TASK_KEY) as? Task
            newTask?.let { viewModel.add(it) }
        }
    }

    private val editTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedTask = result.data?.getSerializableExtra(DetailActivity.TASK_KEY) as? Task
            updatedTask?.let { viewModel.update(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.taskListView.layoutManager = LinearLayoutManager(requireContext())
        binding.taskListView.adapter = adapter

        lifecycleScope.launch {
            viewModel.tasksStateFlow.collect { tasks ->
                adapter.submitList(tasks)
            }
        }

        binding.addTaskFab.setOnClickListener {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            addTaskLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()

        // Récupération des données utilisateur
        lifecycleScope.launch {
            try {
                val response = Api.userWebService.fetchUser()
                if (response.isSuccessful) {
                    val user = response.body()!!
                    binding.userTextView.text = "Utilisateur:\n${user.name} (${user.email})"
                } else {
                    binding.userTextView.text = "Erreur : ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.userTextView.text = "Erreur de récupération des informations utilisateur"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}