package com.example.taskermobile.model

data class ChangePasswordModel (
    val email: String,
    val newPassword: String,
    val oldPassword: String
)