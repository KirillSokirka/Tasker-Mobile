package com.example.taskermobile.service

import com.example.taskermobile.model.release.ReleaseModel
import com.example.taskermobile.model.release.ReleasePreviewModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReleaseApiService {
    @GET("api/releases/available/{projectId}")
    suspend fun getByProject(@Path("projectId") projectId: String):
            Response<List<ReleasePreviewModel>>

    @POST("api/releases")
    suspend fun create(@Body model: ReleaseModel) : Response<ReleaseModel>
}