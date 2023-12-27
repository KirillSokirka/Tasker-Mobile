package com.example.taskermobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskermobile.model.token.TokenValue
import com.example.taskermobile.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TokenViewModel (
    private val tokenManager: TokenManager,
): ViewModel() {

    val token = MutableLiveData<TokenValue?>()

    val isLoading = MutableLiveData<Boolean>()

    init {
        isLoading.value = true // Start with loading state
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.getToken().collect { tokenValue ->
                withContext(Dispatchers.Main) {
                    token.value = tokenValue
                    isLoading.value = false // Loading complete
                }
            }
        }
    }

    fun saveToken(token: TokenValue) {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.saveToken(token)
        }
    }

    fun deleteToken() {
        viewModelScope.launch(Dispatchers.IO) {
            tokenManager.deleteToken()
        }
    }
}