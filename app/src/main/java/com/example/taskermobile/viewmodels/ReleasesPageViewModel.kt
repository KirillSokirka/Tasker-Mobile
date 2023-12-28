package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.release.ReleaseCreateModel
import com.example.taskermobile.model.release.ReleaseModel
import com.example.taskermobile.model.release.ReleasePreviewModel
import com.example.taskermobile.service.ReleaseApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class ReleasesPageViewModel(
    private val releaseApiService: ReleaseApiService
) : BaseViewModel() {

    private val _releasesResponse = MutableLiveData<ApiResponse<List<ReleasePreviewModel>>>()
    val releasesResponse: LiveData<ApiResponse<List<ReleasePreviewModel>>> = _releasesResponse

    private val _releaseResponse = MutableLiveData<ApiResponse<ReleaseModel>>()
    val releaseResponse: LiveData<ApiResponse<ReleaseModel>> = _releaseResponse

    private val _deleteReleaseResponse = MutableLiveData<ApiResponse<String>>()
    val deleteReleaseResponse: LiveData<ApiResponse<String>> = _deleteReleaseResponse

    fun getAll(projectId: String) {
        viewModelScope.launch {
            apiRequestFlow { releaseApiService.getByProject(projectId) }
                .collect { apiResponse -> _releasesResponse.value = apiResponse }
        }
    }

    fun create(model: ReleaseCreateModel) {
        viewModelScope.launch {
            apiRequestFlow { releaseApiService.create(model) }
                .collect { apiResponse -> _releaseResponse.value = apiResponse }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            apiRequestFlow { releaseApiService.delete(id) }
                .collect { apiResponse -> _deleteReleaseResponse.value = apiResponse }
        }
    }
}
