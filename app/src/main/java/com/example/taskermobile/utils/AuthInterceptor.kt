package com.example.taskermobile.utils

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenSettings = runBlocking {
            tokenManager.getToken().firstOrNull()
        }

        val request = chain.request().newBuilder()

        if (tokenSettings != null) {
            Log.d("AuthInterceptor", "Token is: ${tokenSettings.token}")
            request.addHeader("Authorization", "Bearer ${tokenSettings.token}")
        } else {
            Log.d("AuthInterceptor", "Token is null")
        }

        return chain.proceed(request.build())
    }
}