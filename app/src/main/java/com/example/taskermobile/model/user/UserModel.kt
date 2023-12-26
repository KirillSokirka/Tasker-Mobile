package com.example.taskermobile.model.user

data class UserModel(
    val id: String,
    val title: String?,
    val assignedProjects: List<String>?,
    val underControlProjects: List<String>?
)