package com.example.taskermobile.service

import com.example.taskermobile.model.release.ReleasePreviewModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReleaseApiService {
    @GET("api/releases/available/{projectId}")
    suspend fun getByProject(@Path("projectId") projectId: String):
            Response<List<ReleasePreviewModel>>
}