package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.RegisterModel
import com.example.taskermobile.service.UserApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class UserViewModel( private val userApiService: UserApiService
) : BaseViewModel() {

    private val _deleteResponse = MutableLiveData<ApiResponse<String>>()
    val deleteResponse: LiveData<ApiResponse<String>> = _deleteResponse

    fun delete(id: String) {
        viewModelScope.launch {
            apiRequestFlow { userApiService.delete(id) }
                .collect { apiResponse ->
                    _deleteResponse.value = apiResponse
                }
        }
    }
}
