package com.example.taskermobile.service

import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.LoginModel
import com.example.taskermobile.model.RefreshTokenModel
import com.example.taskermobile.model.RegisterModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body auth: LoginModel,
    ): Response<JwtResponse>

    @POST("auth/register")
    suspend fun register(
        @Body auth: RegisterModel,
    ): Response<String>

    @POST("token/refresh-token")
    suspend fun refreshToken(
        @Body refreshTokenModel: RefreshTokenModel
    ): Response<JwtResponse>
}