package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.kanbanboard.KanbanBoardCreateModel
import com.example.taskermobile.model.kanbanboard.KanbanBoardModel
import com.example.taskermobile.model.kanbanboard.KanbanBoardUpdateModel
import com.example.taskermobile.service.KanbanBoardApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class KanbanBoardViewModel(
    private val kanbanBoardApiService: KanbanBoardApiService
) : BaseViewModel() {

    private val _kanbanBoardResponse = MutableLiveData<ApiResponse<KanbanBoardModel>>()
    val kanbanBoardResponse: LiveData<ApiResponse<KanbanBoardModel>> = _kanbanBoardResponse

    private val _kanbanBoardByProjectResponse = MutableLiveData<ApiResponse<List<KanbanBoardModel>>>()
    val kanbanBoardByProjectResponse: LiveData<ApiResponse<List<KanbanBoardModel>>> = _kanbanBoardByProjectResponse

    private val _kanbanCreateResponse = MutableLiveData<ApiResponse<KanbanBoardModel>>()
    val kanbanCreateResponse: LiveData<ApiResponse<KanbanBoardModel>> = _kanbanCreateResponse

    private val _kanbanUpdateResponse = MutableLiveData<ApiResponse<KanbanBoardModel>>()
    val kanbanUpdateResponse: LiveData<ApiResponse<KanbanBoardModel>> = _kanbanUpdateResponse

    private val _kanbanDeleteResponse = MutableLiveData<ApiResponse<String>>()
    val kanbanDeleteResponse: LiveData<ApiResponse<String>> = _kanbanDeleteResponse

    fun getById(id: String){
        viewModelScope.launch {
            apiRequestFlow { kanbanBoardApiService.getById(id) }
                .collect { apiResponse -> _kanbanBoardResponse.value = apiResponse }
        }
    }

    fun getByProject(projectId: String){
        viewModelScope.launch {
            apiRequestFlow { kanbanBoardApiService.getByProjectId(projectId) }
                .collect { apiResponse -> _kanbanBoardByProjectResponse.value = apiResponse }
        }
    }

    fun create(model: KanbanBoardCreateModel){
        viewModelScope.launch {
            apiRequestFlow { kanbanBoardApiService.create(model) }
                .collect { apiResponse -> _kanbanCreateResponse.value = apiResponse }
        }
    }

    fun update(model: KanbanBoardUpdateModel){
        viewModelScope.launch {
            apiRequestFlow { kanbanBoardApiService.update(model) }
                .collect { apiResponse -> _kanbanUpdateResponse.value = apiResponse }
        }
    }

    fun delete(boardId: String){
        viewModelScope.launch {
            apiRequestFlow { kanbanBoardApiService.delete(boardId) }
                .collect { apiResponse -> _kanbanDeleteResponse.value = apiResponse }
        }
    }
}
