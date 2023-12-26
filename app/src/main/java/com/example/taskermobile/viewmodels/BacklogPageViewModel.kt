package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.task.TaskPreviewModel
import com.example.taskermobile.service.TaskApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class BacklogPageViewModel(
    private val taskApiService: TaskApiService
) : BaseViewModel() {

    private val _tasksResponse = MutableLiveData<ApiResponse<List<TaskPreviewModel>>>()
    val tasksResponse: LiveData<ApiResponse<List<TaskPreviewModel>>> = _tasksResponse

    fun getAll(projectId: String) {
        viewModelScope.launch {
            apiRequestFlow { taskApiService.getAll(projectId) }
                .collect { apiResponse -> _tasksResponse.value = apiResponse }
        }
    }
}
