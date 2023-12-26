package com.example.taskermobile.utils

import com.example.taskermobile.model.token.RefreshTokenModel
import com.example.taskermobile.model.token.TokenValue
import com.example.taskermobile.utils.eventlisteners.AuthStateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TokenRefresher(
    private val tokenManager: TokenManager,
    private val authStateListener: AuthStateListener
) {
    private var refreshTokenJob: Job? = null

    fun startTokenRefresh() {
        refreshTokenJob?.cancel()

        refreshTokenJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    delay(20 * 60 * 1000)

                    val tokenSettings = tokenManager.getToken().firstOrNull()
                    if (tokenSettings != null) {
                        val refreshTokenModel = RefreshTokenModel(
                            getEmailFromToken(tokenSettings.token)!!,
                            tokenSettings.token,
                            tokenSettings.refreshToken
                        )

                        val refreshResponse = getNewToken(refreshTokenModel)
                        if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                            val newTokens = refreshResponse.body()!!
                            tokenManager.saveToken(TokenValue(newTokens.token, newTokens.refreshToken))
                        } else {
                            withContext(Dispatchers.Main) {
                                authStateListener.onRefreshTokenFailed()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            authStateListener.onTokenExpired()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        authStateListener.onRefreshTokenFailed()
                    }
                }
            }
        }
    }

    fun stopTokenRefresh() {
        refreshTokenJob?.cancel()
    }
}
