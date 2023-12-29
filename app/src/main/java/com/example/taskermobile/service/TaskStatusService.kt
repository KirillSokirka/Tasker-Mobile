package com.example.taskermobile.service

import com.example.taskermobile.model.taskstatus.TaskStatusCreateModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.model.taskstatus.TaskStatusUpdateModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskStatusService {
    @PUT("api/taskStatus")
    suspend fun update(@Body projectUpdateModel: TaskStatusUpdateModel): Response<TaskStatusModel>
    @POST("api/taskStatus")
    suspend fun create(@Body model: TaskStatusCreateModel): Response<TaskStatusModel>
    @GET("api/taskStatus/{id}")
    suspend fun getById(@Path("id") id: String): Response<TaskStatusModel>
    @DELETE("api/taskStatus/{id}")
    suspend fun delete(@Path("id") id: String) : Response<String>
}