package com.example.taskermobile

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskermobile.model.ErrorResponse
import com.example.taskermobile.service.AuthApiService
import com.example.taskermobile.service.ProjectApiService
import com.example.taskermobile.utils.AuthAuthenticator
import com.example.taskermobile.utils.AuthInterceptor
import com.example.taskermobile.utils.ErrorResponseDeserializer
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.viewmodels.AuthViewModel
import com.example.taskermobile.viewmodels.ProjectsPageViewModel
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

                single { AuthAuthenticator(get()) }

                single { provideOkHttpClient(get(), get()) }

                single { provideRetrofitBuilder(get()) }

                single { provideAuthAPIService(get()) }

                single { provideProjectApiService(get()) }

                viewModel { AuthViewModel(get()) }

                viewModel { TokenViewModel(get()) }

                viewModel { ProjectsPageViewModel(get()) }
            }))
        }
    }
}

fun provideTokenManager(context: Context): TokenManager = TokenManager(context)

fun provideOkHttpClient(
    authInterceptor: AuthInterceptor,
    authAuthenticator: AuthAuthenticator
): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .authenticator(authAuthenticator)
        .addInterceptor(loggingInterceptor)
        .build()
}

fun provideRetrofitBuilder(httpClient: OkHttpClient): Retrofit.Builder =
    Retrofit.Builder()
        .baseUrl("http://77.47.130.226:8188/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())

fun provideAuthAPIService(retrofit: Retrofit.Builder): AuthApiService =
    retrofit
        .build()
        .create(AuthApiService::class.java)

fun provideProjectApiService(retrofit: Retrofit.Builder): ProjectApiService =
    retrofit
        .build()
        .create(ProjectApiService::class.java)
