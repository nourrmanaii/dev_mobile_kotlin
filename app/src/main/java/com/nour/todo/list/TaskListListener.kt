package com.nour.todo.list

import Task

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}