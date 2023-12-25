package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.service.UserApiService
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.TokenViewModel
import com.example.taskermobile.viewmodels.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserActivity() : AppCompatActivity() {
    private val tokenViewModel: TokenViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_activity)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        val logOutButton: Button = findViewById(R.id.logOut)

        logOutButton.setOnClickListener {
            tokenViewModel.deleteToken()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val deleteButton: Button = findViewById(R.id.deleteAccount)

        deleteButton.setOnClickListener {
            userViewModel.delete()
        }

        userViewModel.deleteResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE

                    tokenViewModel.deleteToken()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@UserActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


}