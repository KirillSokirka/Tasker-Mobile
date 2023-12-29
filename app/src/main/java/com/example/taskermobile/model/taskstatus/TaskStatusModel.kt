package com.example.taskermobile.model.taskstatus

import android.os.Parcelable
import com.example.taskermobile.model.task.TaskPreviewModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class TaskStatusModel(
    val id: String?,
    val name: String?,
    val kanbanBoardId: String?,
    val tasks: @RawValue List<TaskPreviewModel>?
) : Parcelable