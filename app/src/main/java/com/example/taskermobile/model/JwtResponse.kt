package com.example.taskermobile.model

data class JwtResponse(val token: TokenValue)

data class TokenValue(val token: String, val refreshToken: String) {
}