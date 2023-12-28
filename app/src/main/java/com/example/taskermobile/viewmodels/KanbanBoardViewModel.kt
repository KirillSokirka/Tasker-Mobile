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

    private val _kanbanCreateResponse = MutableLiveData<ApiResponse<KanbanBoardModel>>()
    val kanbanCreateResponse: LiveData<ApiResponse<KanbanBoardModel>> = _kanbanCreateResponse

    private val _kanbanUpdateResponse = MutableLiveData<ApiResponse<KanbanBoardModel>>()
    val kanbanUpdateResponse: LiveData<ApiResponse<KanbanBoardModel>> = _kanbanUpdateResponse

    fun getById(projectId: String){
        viewModelScope.launch {
            apiRequestFlow { kanbanBoardApiService.getById(projectId) }
                .collect { apiResponse -> _kanbanBoardResponse.value = apiResponse }
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
}
