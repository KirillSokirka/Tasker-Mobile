package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.taskermobile.model.auth.ChangePasswordModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.getEmailFromToken
import com.example.taskermobile.utils.getIdFromToken
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
        val changePasswordButton: Button = findViewById(R.id.changePassword)
//        val userNameLable: TextView = findViewById(R.id.userName)

        tokenViewModel.isLoading.observe(this, Observer { isLoading ->
            deleteButton.isEnabled = !isLoading
            changePasswordButton.isEnabled = !isLoading
        })


        deleteButton.setOnClickListener {
            val idFromJwt = getIdFromToken(tokenViewModel.token.value?.token.toString())!!
            userViewModel.delete(
                idFromJwt
            )
        }

        changePasswordButton.setOnClickListener {
            val emailFromJwt = getEmailFromToken(tokenViewModel.token.value?.token.toString())!!
            userViewModel.changePassword(
                ChangePasswordModel(
                    emailFromJwt,
                    findViewById<EditText>(R.id.oldPassword).text.toString(),
                    findViewById<EditText>(R.id.newPassword).text.toString()
                )
            )
        }

        userViewModel.changePasswordResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    tokenViewModel.deleteToken()
                    val intent = Intent(this, LoginActivity::class.java)
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

        userViewModel.deleteResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    tokenViewModel.deleteToken()
                    val intent = Intent(this, LoginActivity::class.java)
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