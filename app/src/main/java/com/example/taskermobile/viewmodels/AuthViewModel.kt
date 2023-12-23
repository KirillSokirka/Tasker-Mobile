package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.LoginModel
import com.example.taskermobile.service.AuthApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch


class AuthViewModel(
    private val authApiService: AuthApiService
) : BaseViewModel() {

    // LiveData to post the results of the login operation
    private val _loginResponse = MutableLiveData<ApiResponse<JwtResponse>>()
    val loginResponse: LiveData<ApiResponse<JwtResponse>> = _loginResponse

    // Function to perform login using Flow
    fun login(auth: LoginModel) {
        viewModelScope.launch {
            apiRequestFlow { authApiService.login(auth) }
                .collect { apiResponse ->
                    _loginResponse.value = apiResponse
                }
        }
    }
}
