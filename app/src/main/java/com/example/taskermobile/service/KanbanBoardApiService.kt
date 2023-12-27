package com.example.taskermobile.service

import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.model.project.ProjectPreviewModel
import com.example.taskermobile.model.project.ProjectUpdateModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface KanbanBoardApiService {
    @GET("api/kanbanBoards/{id}")
    suspend fun getById(@Path("id") id: String): Response<KanbanBoardModel>
}