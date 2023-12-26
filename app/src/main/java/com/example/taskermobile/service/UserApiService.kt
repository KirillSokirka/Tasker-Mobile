package com.example.taskermobile.service

import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.model.token.JwtResponse
import com.example.taskermobile.model.user.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @GET("users")
    suspend fun getAll() : Response<UserModel>
    @DELETE("user/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>
    @POST("auth/update-password")
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel): Response<JwtResponse>
}