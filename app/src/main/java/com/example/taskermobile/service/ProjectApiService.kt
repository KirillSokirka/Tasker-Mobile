package com.example.taskermobile.service

import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.LoginModel
import com.example.taskermobile.model.ProjectPreviewModel
import com.example.taskermobile.model.ProjectUpdateModel
import com.example.taskermobile.model.RegisterModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProjectApiService {
    @GET("api/projects/available")
    suspend fun getAll(): Response<List<ProjectPreviewModel>>

//    @PUT("api/projects")
//    suspend fun update(@Body projectUpdateModel: ProjectUpdateModel): Response<Unit>
}