package com.example.taskermobile.utils

import android.util.Base64
import com.example.taskermobile.model.JwtResponse
import com.example.taskermobile.model.RefreshTokenModel
import com.example.taskermobile.service.AuthApiService
import com.example.taskermobile.viewmodels.SharedViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuthAuthenticator(private val tokenManager: TokenManager,
                        private val sharedViewModel: SharedViewModel) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val tokenSettings = runBlocking {
                    tokenManager.getToken().firstOrNull()
            }

            if (tokenSettings == null) {
                sharedViewModel.requireLogin()
                return null
            }

            val email = getEmailFromToken(tokenSettings.token)

            val refreshTokenModel =
                RefreshTokenModel(email!!, tokenSettings.token, tokenSettings.refreshToken)

            val refreshResponse = runBlocking {
                getNewToken(refreshTokenModel)
            }

            return if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newToken = refreshResponse.body()!!.token
                runBlocking { tokenManager.saveToken(newToken) }

                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                sharedViewModel.requireLogin()
                null
            }
        }

        return null
    }

    private suspend fun getNewToken(refreshToken: RefreshTokenModel?): retrofit2.Response<JwtResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://77.47.130.226:8188/token/refresh-token")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(AuthApiService::class.java)

        return service.refreshToken(refreshToken!!)
    }

    private fun getEmailFromToken(token: String): String? {
        try {
            val split = token.split(".")
            if (split.size < 2) return null // Not a valid JWT

            val payload = split[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            val jsonObject = JSONObject(decodedString)
            return jsonObject.optString("email")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}