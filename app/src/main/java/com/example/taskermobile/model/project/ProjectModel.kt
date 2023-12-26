package com.example.taskermobile.model.project

import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.task.TaskModel

data class ProjectModel (
    val id: String?,
    val title: String?,
    val kanbanBoards: List<KanbanBoardModel>?,
    val tasks: List<TaskModel>?,
    val assignedUsers: List<String>?,
    val adminProjects: List<String>?
)