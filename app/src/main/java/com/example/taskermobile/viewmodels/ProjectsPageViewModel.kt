package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.ProjectPreviewModel
import com.example.taskermobile.service.ProjectApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch


class ProjectsPageViewModel(
    private val projectApiService: ProjectApiService
) : BaseViewModel() {

    private val _projectsResponse = MutableLiveData<ApiResponse<List<ProjectPreviewModel>>>()
    val projectsResponse: LiveData<ApiResponse<List<ProjectPreviewModel>>> = _projectsResponse

    fun getAll() {
        viewModelScope.launch {
            apiRequestFlow { projectApiService.GetAll().execute() }
                .collect { apiResponse ->
                    _projectsResponse.value = apiResponse
                }
        }
    }
}
