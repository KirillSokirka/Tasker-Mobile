package com.example.taskermobile.model

data class ChangePasswordModel (
    val email: String,
    val oldPassword: String,
    val newPassword: String
)