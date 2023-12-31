package com.example.taskermobile.model.user

data class UserModel(
    val id: String,
    val title: String,
    val isAdmin: Boolean = false,
    var assignedProjects: List<String>?,
    var underControlProjects: List<String>?
)