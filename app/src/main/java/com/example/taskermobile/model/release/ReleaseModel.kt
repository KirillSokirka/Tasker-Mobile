package com.example.taskermobile.model.release

import com.example.taskermobile.model.task.TaskPreviewModel
import java.time.LocalDateTime

data class ReleaseModel(
    val id: String?,
    val title: String?,
    val isReleased: Boolean,
    val creationDate: String,
    val endDate: String,
    val projectId: String?,
    val tasks: List<TaskPreviewModel>
)
