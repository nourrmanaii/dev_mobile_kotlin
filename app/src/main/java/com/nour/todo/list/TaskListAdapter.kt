package com.nour.todo.list

import Task
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nour.todo.R
import com.nour.todo.databinding.ItemTaskBinding
import coil.load


class TaskListAdapter(
    private val listener: TaskListListener
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description ?: "No description"

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
