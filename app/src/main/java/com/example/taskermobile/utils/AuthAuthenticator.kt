package com.example.taskermobile.utils

import com.example.taskermobile.model.RefreshTokenModel
import com.example.taskermobile.model.TokenValue
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AuthAuthenticator(private val tokenManager: TokenManager,
                        private val authStateListener: AuthStateListener) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val tokenSettings = runBlocking {
                    tokenManager.getToken().firstOrNull()
            }

            if (tokenSettings == null) {
                authStateListener.onTokenExpired()
                return null
            }

            val email = getEmailFromToken(tokenSettings.token)

            val refreshTokenModel =
                RefreshTokenModel(email!!, tokenSettings.token, tokenSettings.refreshToken)

            val refreshResponse = runBlocking {
                getNewToken(refreshTokenModel)
            }

            return if (refreshResponse.isSuccessful && refreshResponse.body() != null) {

                val refreshToken = refreshResponse.body()
                runBlocking { tokenManager.saveToken(TokenValue(refreshToken!!.token,
                    refreshToken.refreshToken)) }

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshToken!!.token}")
                    .build()
            } else {
                authStateListener.onRefreshTokenFailed()
                null
            }
        }

        return null
    }
}