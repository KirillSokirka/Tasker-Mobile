package com.example.taskermobile.model.kanbanboard

import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.project.ProjectModel

data class KanbanBoardModel (
    val id: String?,
    val title: String? = null,
    val projectId: String? = null,
    val project: ProjectModel?,
    val columns: List<TaskStatusModel>?
)

data class TaskStatusModel(
    val id: String?,
    val name: String?,
    val kanbanBoardId: String?,
    val tasks: List<TaskPreviewModel>?
)

