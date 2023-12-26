package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.project.ProjectCreateModel
import com.example.taskermobile.model.project.ProjectModel
import com.example.taskermobile.model.project.ProjectPreviewModel
import com.example.taskermobile.model.project.ProjectUpdateModel
import com.example.taskermobile.service.ProjectApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val projectApiService: ProjectApiService
) : BaseViewModel() {

    private val _projectsResponse = MutableLiveData<ApiResponse<List<ProjectPreviewModel>>>()
    val projectsResponse: LiveData<ApiResponse<List<ProjectPreviewModel>>> = _projectsResponse

    private val _projectUpdateResponse = MutableLiveData<ApiResponse<ProjectModel>>()
    val projectUpdateResponse: LiveData<ApiResponse<ProjectModel>> = _projectUpdateResponse

    private val _projectCreateResponse = MutableLiveData<ApiResponse<ProjectModel>>()
    val projectCreateResponse: LiveData<ApiResponse<ProjectModel>> = _projectCreateResponse

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

    fun create(model: ProjectCreateModel) {
        viewModelScope.launch {
            apiRequestFlow { projectApiService.create(model) }
                .collect { apiResponse -> _projectCreateResponse.value = apiResponse }
        }
    }

    fun getById(projectId: String){
        viewModelScope.launch {
            apiRequestFlow { projectApiService.getById(projectId) }
                .collect { apiResponse -> _projectGetByIdResponse.value = apiResponse }
        }
    }

}
