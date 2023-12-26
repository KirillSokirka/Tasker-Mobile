package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    fun getAll(projectId: String) {
        viewModelScope.launch {
            apiRequestFlow { releaseApiService.getByProject(projectId) }
                .collect { apiResponse -> _releasesResponse.value = apiResponse }
        }
    }
}
