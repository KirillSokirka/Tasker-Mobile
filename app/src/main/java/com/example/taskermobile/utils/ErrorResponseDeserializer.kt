package com.example.taskermobile.utils

import com.example.taskermobile.model.ErrorResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ErrorResponseDeserializer : JsonDeserializer<ErrorResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ErrorResponse {
        val jsonObject = json.asJsonObject

        val messages = jsonObject.entrySet().first().value.asJsonArray
        val message = messages.joinToString(separator = "\n") { it.asString }

        return ErrorResponse(code = null, message = message)
    }
}