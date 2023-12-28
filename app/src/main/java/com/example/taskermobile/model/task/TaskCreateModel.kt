package com.example.taskermobile.model.task

import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.user.UserModel

data class TaskCreateModel(
    val id: String? = null,
    val title: String? = null,
    val projectId: String? = null,
    val creatorId: String? = null,
    val description: String? = null,
    val taskStatusId: String? = null,
    val releasedId: String? = null,
    val priority: TaskPriority = TaskPriority.NONE,
    val assigneeId: String? = null
)
