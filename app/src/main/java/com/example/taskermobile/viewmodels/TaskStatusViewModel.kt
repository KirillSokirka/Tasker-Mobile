package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.model.project.ProjectUpdateModel
import com.example.taskermobile.model.taskstatus.TaskStatusCreateModel
import com.example.taskermobile.model.taskstatus.TaskStatusModel
import com.example.taskermobile.model.taskstatus.TaskStatusUpdateModel
import com.example.taskermobile.service.TaskStatusService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class TaskStatusViewModel(
    private val taskStatusService: TaskStatusService
) : BaseViewModel() {
    
    private val _taskStatusCreateResponse = MutableLiveData<ApiResponse<TaskStatusModel>>()
    val taskStatusCreateResponse: LiveData<ApiResponse<TaskStatusModel>> = _taskStatusCreateResponse

    private val _taskStatusDeleteResponse = MutableLiveData<ApiResponse<String>>()
    val taskStatusDeleteResponse: LiveData<ApiResponse<String>> = _taskStatusDeleteResponse

    private val _taskStatusUpdateResponse = MutableLiveData<ApiResponse<TaskStatusModel>>()
    val taskStatusUpdateResponse: LiveData<ApiResponse<TaskStatusModel>> = _taskStatusUpdateResponse

    private val _taskStatusGetByIdResponse = MutableLiveData<ApiResponse<TaskStatusModel>>()
    val taskStatusGetByIdResponse: LiveData<ApiResponse<TaskStatusModel>> = _taskStatusGetByIdResponse

    fun getById(id: String){
        viewModelScope.launch {
            apiRequestFlow { taskStatusService.getById(id) }
                .collect { apiResponse -> _taskStatusGetByIdResponse.value = apiResponse }
        }
    }

    fun update(model: TaskStatusUpdateModel) {
        viewModelScope.launch {
            apiRequestFlow { taskStatusService.update(model) }
                .collect { apiResponse -> _taskStatusUpdateResponse.value = apiResponse }
        }
    }

    fun create(model: TaskStatusCreateModel) {
        viewModelScope.launch {
            apiRequestFlow { taskStatusService.create(model) }
                .collect { apiResponse -> _taskStatusCreateResponse.value = apiResponse }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            apiRequestFlow { taskStatusService.delete(id) }
                .collect { apiResponse -> _taskStatusDeleteResponse.value = apiResponse }
        }
    }
}