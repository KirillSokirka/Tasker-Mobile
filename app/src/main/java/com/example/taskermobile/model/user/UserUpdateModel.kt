package com.example.taskermobile.model.user

data class UserUpdateModel(
    val username: String,
    var assignedProjects: List<String>?,
    var underControlProjects: List<String>?
)