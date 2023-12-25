package com.example.taskermobile.service

import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.LoginModel
import com.example.taskermobile.model.ProjectModel
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
import retrofit2.http.Path

interface ProjectApiService {
    @GET("api/projects/available")
    suspend fun getAll(): Response<List<ProjectPreviewModel>>

    @PUT("api/projects")
    suspend fun update(@Body projectUpdateModel: ProjectUpdateModel): Response<ProjectUpdateModel>

    @GET("api/projects/{projectId}")
    suspend fun getById(@Path("projectId") projectId: String): Response<ProjectModel>
}