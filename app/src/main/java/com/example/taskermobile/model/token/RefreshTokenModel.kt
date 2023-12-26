package com.example.taskermobile.model.token

data class RefreshTokenModel(
    val email: String,
    val token: String,
    val refreshToken: String
)
