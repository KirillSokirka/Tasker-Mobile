package com.example.taskermobile.service

import com.example.taskermobile.model.ChangePasswordModel
import com.example.taskermobile.model.JwtResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @DELETE("user/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>

    @POST("auth/update-password")
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel): Response<JwtResponse>
}