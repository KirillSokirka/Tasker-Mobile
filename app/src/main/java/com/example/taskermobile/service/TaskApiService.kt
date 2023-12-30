package com.example.taskermobile.service

import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.model.project.ProjectUpdateModel
import com.example.taskermobile.model.task.TaskCreateModel
import com.example.taskermobile.model.task.TaskModel
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.model.task.TaskUpdateModel
import com.example.taskermobile.model.task.TaskUpdateStatusModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApiService {
    @GET("task/backlog/{projectId}")
    suspend fun getAll(@Path("projectId") projectId: String): Response<List<TaskPreviewModel>>
    @POST("Task/")
    suspend fun create(@Body task: TaskCreateModel) : Response<TaskModel>
    @GET("Task/{id}")
    suspend fun get(@Path("id") id: String) : Response<TaskModel>
    @DELETE("TaskStatus/{id}")
    suspend fun delete(@Path("id") id: String) : Response<String>
    @PUT("Task")
    suspend fun update(@Body model: TaskUpdateStatusModel): Response<TaskModel>
    @PUT("Task")
    suspend fun update(@Body model: TaskUpdateModel): Response<TaskModel>
}