package com.example.taskermobile.model

data class RefreshTokenModel(
    val email: String,
    val token: String,
    val refreshToken: String
)
