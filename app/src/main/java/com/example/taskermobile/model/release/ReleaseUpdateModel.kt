package com.example.taskermobile.model.release

import com.example.taskermobile.model.task.TaskModel

data class ReleaseUpdateModel(
    val id: String,
    val projectId: String,
    val title: String,
    val isReleased: Boolean,
    val endDate: String,
    val tasks: List<String>
)