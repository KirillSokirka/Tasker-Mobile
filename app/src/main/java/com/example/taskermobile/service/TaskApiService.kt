package com.example.taskermobile.service

import com.example.taskermobile.model.task.TaskPreviewModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TaskApiService {
    @GET("task/backlog/{projectId}")
    suspend fun getAll(@Path("projectId") projectId: String): Response<List<TaskPreviewModel>>
}