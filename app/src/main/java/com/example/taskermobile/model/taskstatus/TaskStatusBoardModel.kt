package com.example.taskermobile.model.taskstatus

import android.os.Parcelable
import com.example.taskermobile.model.task.TaskBoardPreviewModel
import com.example.taskermobile.model.task.TaskPreviewModel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class TaskStatusBoardModel(
    val id: String?,
    val name: String?,
    val kanbanBoardId: String?,
    val tasks: @RawValue List<TaskBoardPreviewModel>?
) : Parcelable