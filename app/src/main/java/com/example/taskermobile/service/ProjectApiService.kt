package com.example.taskermobile.service

import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.model.project.ProjectPreviewModel
import com.example.taskermobile.model.project.ProjectUpdateModel
import com.example.taskermobile.model.user.MemberModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectApiService {
    @GET("api/projects/available")
    suspend fun getAll(): Response<List<ProjectPreviewModel>>
    @PUT("api/projects")
    suspend fun update(@Body projectUpdateModel: ProjectUpdateModel): Response<ProjectModel>
    @POST("api/projects")
    suspend fun create(@Body model: ProjectCreateModel): Response<ProjectModel>
    @GET("api/projects/{projectId}")
    suspend fun getById(@Path("projectId") projectId: String): Response<ProjectModel>
    @GET("api/projects/project-members/{id}")
    suspend fun getMembers(@Path("id") id: String): Response<List<MemberModel>>
    @DELETE("api/projects/{id}")
    suspend fun delete(@Path("id") id: String) : Response<String>
}