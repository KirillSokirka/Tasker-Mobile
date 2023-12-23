package com.example.taskermobile

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskermobile.model.ErrorResponse
import com.example.taskermobile.service.AuthApiService
import com.example.taskermobile.utils.AuthAuthenticator
import com.example.taskermobile.utils.AuthInterceptor
import com.example.taskermobile.utils.ErrorResponseDeserializer
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.viewmodels.AuthViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

class TaskerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TaskerApplication)
            modules(listOf(module {
                single {
                    GsonBuilder()
                        .registerTypeAdapter(ErrorResponse::class.java, ErrorResponseDeserializer())
                        .create()
                }

                single { provideTokenManager(androidContext()) }

                single { AuthInterceptor(get()) }

                // Provide a singleton instance of AuthAuthenticator
                single { AuthAuthenticator(get()) }

                // Provide a singleton instance of OkHttpClient
                single { provideOkHttpClient(get(), get()) }

                // Provide a singleton instance of Retrofit.Builder with a configured base URL
                single { provideRetrofitBuilder() }

                // Provide a singleton instance of AuthApiService
                single { provideAuthAPIService(get()) }

                viewModel { AuthViewModel(get()) }

                viewModel { TokenViewModel(get()) }
            }))
        }
    }
}

fun provideTokenManager(context: Context): TokenManager = TokenManager(context)

fun provideOkHttpClient(
    authInterceptor: AuthInterceptor,
    authAuthenticator: AuthAuthenticator
): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .authenticator(authAuthenticator)
        .build()
}

fun provideRetrofitBuilder(): Retrofit.Builder =
    Retrofit.Builder()
        .baseUrl("http://77.47.130.226:8188/")
        .addConverterFactory(GsonConverterFactory.create())

fun provideAuthAPIService(retrofit: Retrofit.Builder): AuthApiService =
    retrofit
        .build()
        .create(AuthApiService::class.java)
