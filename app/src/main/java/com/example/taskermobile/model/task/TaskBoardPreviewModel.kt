package com.example.taskermobile.model.task

import com.example.taskermobile.model.user.UserModel


data class TaskBoardPreviewModel(
    val title: String,
    val description: String,
    val taskStatusName: String,
    val id: String,
    val assignee: String?,
    val priority: Int,
)

