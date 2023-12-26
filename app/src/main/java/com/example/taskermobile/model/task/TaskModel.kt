package com.example.taskermobile.model.task

import com.example.taskermobile.model.TaskPriority
import com.example.taskermobile.model.user.UserModel
import java.time.LocalDateTime
import java.util.UUID

data class TaskModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val projectId: String,
    val creator: UserModel?,
    val description: String?,
    val taskStatusId: String?,
    val releasedId: String?,
    val priority: TaskPriority = TaskPriority.NONE,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val assignee: UserModel?
)

