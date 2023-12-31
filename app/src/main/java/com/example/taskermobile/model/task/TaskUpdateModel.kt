package com.example.taskermobile.model.task

data class TaskUpdateModel(
    var id: String,
    val title: String? = null,
    val description: String? = null,
    val statusId: String? = null,
    val releaseId: String? = null,
    val priority: Int = 0,
    val assigneeId: String? = null
)

data class TaskUpdateStatusModel(
    val id: String,
    val statusId: String
)
