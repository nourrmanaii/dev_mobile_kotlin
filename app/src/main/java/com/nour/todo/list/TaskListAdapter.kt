package com.nour.todo.list

import Task
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nour.todo.databinding.ItemTaskBinding

class TaskListAdapter(
    private val listener: TaskListListener
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description ?: "No description"

            binding.root.setOnLongClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Task: ${task.title}\nDescription: ${task.description ?: "No description"}"
                    )
                }
                binding.root.context.startActivity(Intent.createChooser(intent, "Share Task via"))
                true
            }

            binding.deleteTaskButton.setOnClickListener {
                listener.onClickDelete(task)
            }
            binding.editTaskButton.setOnClickListener {
                listener.onClickEdit(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}