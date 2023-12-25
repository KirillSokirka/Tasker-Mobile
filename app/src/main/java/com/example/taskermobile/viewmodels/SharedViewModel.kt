package com.example.taskermobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel : BaseViewModel() {

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun requireLogin() {
        _navigateToLogin.postValue(true)
    }

    fun resetNavigationFlag() {
        _navigateToLogin.postValue(false)
    }
}