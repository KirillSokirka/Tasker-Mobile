package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.token.JwtResponse
import com.example.taskermobile.model.auth.LoginModel
import com.example.taskermobile.model.auth.RegisterModel
import com.example.taskermobile.service.AuthApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.apiRequestFlow
import kotlinx.coroutines.launch


class AuthViewModel(
    private val authApiService: AuthApiService
) : BaseViewModel() {

    private val _loginResponse = MutableLiveData<ApiResponse<JwtResponse>>()
    val loginResponse: LiveData<ApiResponse<JwtResponse>> = _loginResponse

    private val _registerResponse = MutableLiveData<ApiResponse<Unit>>()
    val registerResponse: LiveData<ApiResponse<Unit>> = _registerResponse

    fun login(auth: LoginModel) {
        viewModelScope.launch {
            apiRequestFlow { authApiService.login(auth) }
                .collect { apiResponse ->
                    _loginResponse.value = apiResponse
                }
        }
    }

    fun register(auth: RegisterModel) {
        viewModelScope.launch {
            apiRequestFlow { authApiService.register(auth) }
                .collect { apiResponse ->
                    _registerResponse.value = apiResponse
                }
        }
    }
}
