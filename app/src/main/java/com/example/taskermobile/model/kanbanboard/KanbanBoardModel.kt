package com.example.taskermobile.model.kanbanboard

import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel

data class KanbanBoardModel (
    val id: String?,
    val title: String? = null,
    val projectId: String? = null,
    val project: ProjectModel?,
    val columns: List<TaskStatusModel>?
)