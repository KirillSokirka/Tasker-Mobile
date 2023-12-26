package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.model.token.JwtResponse
import com.example.taskermobile.model.user.UserModel
import com.example.taskermobile.model.user.UserUpdateModel
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

    private val _usersResponse = MutableLiveData<ApiResponse<List<UserModel>>>()
    val userResponse: LiveData<ApiResponse<List<UserModel>>> = _usersResponse

    private val _usersUpdateResponse = MutableLiveData<ApiResponse<UserModel>>()
    val userUpdateResponse: LiveData<ApiResponse<UserModel>> = _usersUpdateResponse

    private val _usersGetResponse = MutableLiveData<ApiResponse<UserModel>>()
    val userGetResponse: LiveData<ApiResponse<UserModel>> = _usersGetResponse

    fun getAll() {
        viewModelScope.launch {
            apiRequestFlow { userApiService.getAll() }
                .collect { apiResponse ->
                    _usersResponse.value = apiResponse
                }
        }
    }

    fun get(id: String) {
        viewModelScope.launch {
            apiRequestFlow { userApiService.get(id) }
                .collect { apiResponse ->
                    _usersGetResponse.value = apiResponse
                }
        }
    }

    fun updateUserProjects(user: UserUpdateModel) {
        viewModelScope.launch {
            apiRequestFlow { userApiService.update(user) }
            .collect { apiResponse ->
                _usersUpdateResponse.value = apiResponse
            }
        }
    }

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
