package com.example.taskermobile.service

import com.example.taskermobile.model.release.ReleaseCreateModel
import com.example.taskermobile.model.release.ReleaseModel
import com.example.taskermobile.model.release.ReleasePreviewModel
import com.example.taskermobile.model.release.ReleaseUpdateModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReleaseApiService {
    @GET("api/releases/available/{projectId}")
    suspend fun getByProject(@Path("projectId") projectId: String):
            Response<List<ReleasePreviewModel>>
    @GET("api/releases/{id}")
    suspend fun get(@Path("id") id: String) : Response<ReleaseModel>
    @POST("api/releases")
    suspend fun create(@Body model: ReleaseCreateModel) : Response<ReleaseModel>
    @PUT("api/releases")
    suspend fun update(@Body model: ReleaseUpdateModel) : Response<ReleaseModel>
    @DELETE("api/releases/{id}")
    suspend fun delete(@Path("id") id: String) : Response<String>
}