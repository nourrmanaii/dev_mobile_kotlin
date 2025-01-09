package com.nour.todo.list

import Task
import androidx.recyclerview.widget.DiffUtil

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        // Comparer les IDs uniques pour déterminer si c'est le même élément
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        // Comparer le contenu entier des objets
        return oldItem == newItem
    }
}