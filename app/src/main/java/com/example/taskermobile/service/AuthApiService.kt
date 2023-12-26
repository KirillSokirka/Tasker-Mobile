package com.example.taskermobile.service

import com.example.taskermobile.model.token.JwtResponse
import com.example.taskermobile.model.auth.LoginModel
import com.example.taskermobile.model.token.RefreshJwtResponse
import com.example.taskermobile.model.token.RefreshTokenModel
import com.example.taskermobile.model.auth.RegisterModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body auth: LoginModel,
    ): Response<JwtResponse>

    @POST("auth/register")
    suspend fun register(
        @Body auth: RegisterModel,
    ): Response<Unit>

    @POST("token/refresh-token")
    suspend fun refreshToken(
        @Body refreshTokenModel: RefreshTokenModel
    ): Response<RefreshJwtResponse>
}