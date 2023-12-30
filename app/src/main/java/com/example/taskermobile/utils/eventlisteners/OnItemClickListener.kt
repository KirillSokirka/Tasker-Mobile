package com.example.taskermobile.utils.eventlisteners

import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel

interface OnItemClickListener {
    fun onItemClick(id: String)
    fun onItemLongClick(task: TaskPreviewModel, allStatuses: List<TaskStatusModel>)
}
