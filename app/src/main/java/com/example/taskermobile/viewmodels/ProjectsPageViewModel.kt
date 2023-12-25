package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.ProjectModel
import com.example.taskermobile.model.ProjectPreviewModel
import com.example.taskermobile.model.ProjectUpdateModel
import com.example.taskermobile.service.ProjectApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch


class ProjectsPageViewModel(
    private val projectApiService: ProjectApiService
) : BaseViewModel() {

    private val _projectsResponse = MutableLiveData<ApiResponse<List<ProjectPreviewModel>>>()
    val projectsResponse: LiveData<ApiResponse<List<ProjectPreviewModel>>> = _projectsResponse

    private val _projectUpdateResponse = MutableLiveData<ApiResponse<ProjectUpdateModel>>()
    val projectUpdateResponse: LiveData<ApiResponse<ProjectUpdateModel>> = _projectUpdateResponse

    private val _projectGetByIdResponse = MutableLiveData<ApiResponse<ProjectModel>>()
    val projectGetByIdResponse: LiveData<ApiResponse<ProjectModel>> = _projectGetByIdResponse

    fun getAll() {
        viewModelScope.launch {
            apiRequestFlow { projectApiService.getAll() }
                .collect { apiResponse -> _projectsResponse.value = apiResponse }
        }
    }

    fun update(projectUpdateModel: ProjectUpdateModel) {
        viewModelScope.launch {
            apiRequestFlow { projectApiService.update(projectUpdateModel) }
                .collect { apiResponse -> _projectUpdateResponse.value = apiResponse }
        }
    }

    fun getById(projectId: String){
        viewModelScope.launch {
            apiRequestFlow { projectApiService.getById(projectId) }
                .collect { apiResponse -> _projectGetByIdResponse.value = apiResponse }
        }
    }

}
