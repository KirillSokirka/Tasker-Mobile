package com.example.taskermobile

import SharedPreferencesService
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskermobile.model.ErrorResponse
import com.example.taskermobile.service.AuthApiService
import com.example.taskermobile.service.KanbanBoardApiService
import com.example.taskermobile.service.ProjectApiService
import com.example.taskermobile.service.ReleaseApiService
import com.example.taskermobile.service.TaskApiService
import com.example.taskermobile.service.TaskStatusService
import com.example.taskermobile.service.UserApiService
import com.example.taskermobile.utils.AuthAuthenticator
import com.example.taskermobile.utils.eventlisteners.AuthEventListenerImplementation
import com.example.taskermobile.utils.AuthInterceptor
import com.example.taskermobile.utils.eventlisteners.AuthStateListener
import com.example.taskermobile.utils.ErrorResponseDeserializer
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.viewmodels.AuthViewModel
import com.example.taskermobile.viewmodels.KanbanBoardViewModel
import com.example.taskermobile.viewmodels.ProjectsViewModel
import com.example.taskermobile.viewmodels.ReleasesPageViewModel
import com.example.taskermobile.viewmodels.TaskStatusViewModel
import com.example.taskermobile.viewmodels.TaskViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
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
            modules(listOf(myModule))
        }
    }
}

val myModule = module {
    single {
        GsonBuilder()
            .registerTypeAdapter(ErrorResponse::class.java, ErrorResponseDeserializer())
            .create()
    }
    single { provideTokenManager(androidContext()) }
    single { provideSharedPreferencesService(androidContext()) }
    single { AuthInterceptor(get()) }
    single { AuthAuthenticator(get(), get()) }
    single { provideOkHttpClient(get(), get()) }
    single { provideRetrofitBuilder(get()) }
    single { provideAuthAPIService(get()) }
    single { provideKanbanBoardAPIService(get()) }
    single { provideReleaseApiService(get()) }
    single { provideProjectApiService(get()) }
    single { provideTaskApiService(get()) }
    single { provideUserApiService(get())}
    single { provideTaskStatusApiService(get())}
    single<AuthStateListener> { AuthEventListenerImplementation }

    viewModel { AuthViewModel(get()) }
    viewModel { TokenViewModel(get()) }
    viewModel { ProjectsViewModel(get()) }
    viewModel { ReleasesPageViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { KanbanBoardViewModel(get()) }
    viewModel { TaskViewModel(get()) }
    viewModel { TaskStatusViewModel(get()) }
}

fun provideTokenManager(context: Context): TokenManager = TokenManager(context)
fun provideSharedPreferencesService(context: Context): SharedPreferencesService = SharedPreferencesService(context)

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
        //.baseUrl("http://77.47.130.226:8188/")
        .baseUrl("http://10.0.2.2:5185/")
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

fun provideReleaseApiService(retrofit: Retrofit.Builder): ReleaseApiService =
    retrofit
        .build()
        .create(ReleaseApiService::class.java)

fun provideTaskApiService(retrofit: Retrofit.Builder): TaskApiService =
    retrofit
        .build()
        .create(TaskApiService::class.java)

fun provideUserApiService(retrofit: Retrofit.Builder): UserApiService =
    retrofit
        .build()
        .create(UserApiService::class.java)

fun provideKanbanBoardAPIService(retrofit: Retrofit.Builder): KanbanBoardApiService =
    retrofit
        .build()
        .create(KanbanBoardApiService::class.java)

fun provideTaskStatusApiService(retrofit: Retrofit.Builder): TaskStatusService =
    retrofit
        .build()
        .create(TaskStatusService::class.java)