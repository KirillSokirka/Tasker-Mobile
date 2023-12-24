package com.example.taskermobile.service

import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.LoginModel
import com.example.taskermobile.model.ProjectPreviewModel
import com.example.taskermobile.model.RegisterModel
import com.example.taskermobile.model.ReleasePreviewModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ReleaseApiService {
    @GET("api/releases")
    suspend fun getAll(): Response<List<ReleasePreviewModel>>
}