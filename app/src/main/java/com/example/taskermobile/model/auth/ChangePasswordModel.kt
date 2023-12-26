package com.example.taskermobile.model.auth

data class ChangePasswordModel (
    val email: String,
    val oldPassword: String,
    val newPassword: String
)