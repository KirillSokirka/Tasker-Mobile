package com.example.taskermobile.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskermobile.dataStore
import com.example.taskermobile.model.TokenValue
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val gson = Gson()
    }

    fun getToken(): Flow<TokenValue?> {
        return context.dataStore.data
            .catch { exception ->
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[TOKEN_KEY]?.let {
                    gson.fromJson(it, TokenValue::class.java)
                }
            }
    }

    suspend fun saveToken(tokenValue: TokenValue) {
        val tokenString = gson.toJson(tokenValue)
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = tokenString
        }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}