package com.example.taskermobile.utils
import com.example.taskermobile.model.ErrorResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

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
