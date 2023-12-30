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
    val taskStatus: BaseDto?,
    val release: BaseDto?,
    val priority: Int,
    val creationDate: String,
    val assignee: UserModel?
)

data class BaseDto(val id: String, val title: String)
