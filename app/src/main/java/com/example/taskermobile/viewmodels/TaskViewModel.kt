package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.release.ReleaseCreateModel
import com.example.taskermobile.model.release.ReleaseModel
import com.example.taskermobile.model.release.ReleasePreviewModel
import com.example.taskermobile.model.task.TaskCreateModel
import com.example.taskermobile.model.task.TaskModel
import com.example.taskermobile.service.ReleaseApiService
import com.example.taskermobile.service.TaskApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskApiService: TaskApiService
) : BaseViewModel() {

    private val _taskCreateResponse = MutableLiveData<ApiResponse<TaskModel>>()
    val taskCreateResponse: LiveData<ApiResponse<TaskModel>> = _taskCreateResponse


    fun create(task: TaskCreateModel) {
        viewModelScope.launch {
            apiRequestFlow { taskApiService.create(task) }
                .collect { apiResponse -> _taskCreateResponse.value = apiResponse }
        }
    }

}