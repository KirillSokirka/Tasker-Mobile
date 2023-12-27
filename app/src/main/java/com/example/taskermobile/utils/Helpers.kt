package com.example.taskermobile.utils

import android.util.Base64
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.taskermobile.model.ErrorResponse
import com.example.taskermobile.model.token.RefreshJwtResponse
import com.example.taskermobile.model.token.RefreshTokenModel
import com.example.taskermobile.service.AuthApiService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun<T> apiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T & Any>> = flow {
    emit(ApiResponse.Loading)

    try {
        val response = call()

        if (response.code() == 204 || (response.code() == 200 && response.body() == null)) {
            emit(ApiResponse.Success(null))
        } else if (response.isSuccessful) {
            response.body()?.let { data ->
                emit(ApiResponse.Success(data))
            } ?: emit(ApiResponse.Failure("No data to show", 204))
        } else {
            response.errorBody()?.let { error ->
                error.use { responseBody ->
                    val gson = GsonBuilder()
                        .registerTypeAdapter(ErrorResponse::class.java, ErrorResponseDeserializer())
                        .create()
                    val parsedError: ErrorResponse = gson.fromJson(responseBody.charStream(), ErrorResponse::class.java)
                    emit(ApiResponse.Failure(parsedError.message, 500))
                }
            }
        }
    } catch (e: Exception) {
        emit(ApiResponse.Failure(e.message ?: e.toString(), 400))
    }
}.flowOn(Dispatchers.IO)

fun getUsernameFromToken(token: String): String? {
    try {
        val split = token.split(".")
        if (split.size < 2) return null

        val payload = split[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val decodedString = String(decodedBytes, Charsets.UTF_8)

        val jsonObject = JSONObject(decodedString)
        return jsonObject.optString("unique_name")
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun getIdFromToken(token: String): String? {
    try {
        val split = token.split(".")
        if (split.size < 2) return null

        val payload = split[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val decodedString = String(decodedBytes, Charsets.UTF_8)

        val jsonObject = JSONObject(decodedString)
        return jsonObject.optString("jti")
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun getEmailFromToken(token: String): String? {
    try {
        val split = token.split(".")
        if (split.size < 2) return null

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

suspend fun getNewToken(refreshToken: RefreshTokenModel?): retrofit2.Response<RefreshJwtResponse> {
    val loggingInterceptor = HttpLoggingInterceptor()

    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://77.47.130.226:8188/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val service = retrofit.create(AuthApiService::class.java)

    return service.refreshToken(refreshToken!!)
}