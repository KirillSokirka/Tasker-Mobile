package com.example.taskermobile.model.task

data class TaskCreateModel(
    val title: String? = null,
    val projectId: String? = null,
    val creatorId: String? = null,
    val description: String? = null,
    val taskStatusId: String? = null,
    val releaseId: String? = null,
    val priority: Int = 0,
    val assigneeId: String? = null
)
