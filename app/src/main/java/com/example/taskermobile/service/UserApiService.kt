package com.example.taskermobile.service

import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.model.token.JwtResponse
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.model.user.UserUpdateModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
    @GET("user/{id}")
    suspend fun get(@Path("id") id: String): Response<UserModel>
    @GET("user")
    suspend fun getAll() : Response<List<UserModel>>
    @PUT("user")
    suspend fun update(@Body model: UserUpdateModel) : Response<UserModel>
    @DELETE("user/{id}")
    suspend fun delete(@Path("id") id: String): Response<String>
    @POST("auth/update-password")
    suspend fun changePassword(@Body changePasswordModel: ChangePasswordModel): Response<JwtResponse>
}