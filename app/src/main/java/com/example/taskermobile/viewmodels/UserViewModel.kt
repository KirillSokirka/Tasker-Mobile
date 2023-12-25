package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.ChangePasswordModel
import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.RegisterModel
import com.example.taskermobile.service.UserApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch

class UserViewModel( private val userApiService: UserApiService
) : BaseViewModel() {

    private val _deleteResponse = MutableLiveData<ApiResponse<String>>()
    val deleteResponse: LiveData<ApiResponse<String>> = _deleteResponse

    private val _changePasswordResponse = MutableLiveData<ApiResponse<JwtResponse>>()
    val changePasswordResponse: LiveData<ApiResponse<JwtResponse>> = _changePasswordResponse

    fun delete(id: String) {
        viewModelScope.launch {
            apiRequestFlow { userApiService.delete(id) }
                .collect { apiResponse ->
                    _deleteResponse.value = apiResponse
                }
        }
    }

    fun changePassword(changePasswordModel: ChangePasswordModel) {
        viewModelScope.launch {
            apiRequestFlow { userApiService.changePassword(changePasswordModel) }
                .collect { apiResponse ->
                    _changePasswordResponse.value = apiResponse
                }
        }
    }

}
