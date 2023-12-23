package com.example.taskermobile.utils

import com.example.taskermobile.model.ErrorResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ErrorResponseDeserializer : JsonDeserializer<ErrorResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ErrorResponse {
        val jsonObject = json.asJsonObject

        val errorsJsonElement = jsonObject.get("errors") ?: jsonObject.get("")

        if (errorsJsonElement?.isJsonObject == true) {
            val errorsObject = errorsJsonElement.asJsonObject
            val messages = mutableListOf<String>()
            errorsObject.entrySet().forEach { entry ->
                val errorMessages = entry.value.asJsonArray
                errorMessages.forEach { messages.add(it.asString) }
            }
            return ErrorResponse(code = null, message = messages.joinToString(separator = "\n"))
        }

        else if (errorsJsonElement?.isJsonArray == true) {
            val messagesArray = errorsJsonElement.asJsonArray
            val messages = messagesArray.joinToString(separator = "\n") { it.asString }
            return ErrorResponse(code = null, message = messages)
        }

        return ErrorResponse(code = null, message = "An unknown error occurred")
    }
}