package com.example.taskermobile.utils.eventlisteners

import com.example.taskermobile.model.task.TaskBoardPreviewModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.taskstatus.TaskStatusBoardModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel

interface OnItemClickListener {
    fun onItemClick(id: String)
    fun onItemLongClick(task: TaskBoardPreviewModel, allStatuses: List<TaskStatusBoardModel>)
}
