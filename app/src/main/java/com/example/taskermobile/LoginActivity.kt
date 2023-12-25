package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.model.LoginModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.AuthViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModel()
    private val tokenViewModel: TokenViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            viewModel.login(LoginModel(email, password))
        }

        viewModel.loginResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                }

                is ApiResponse.Success -> {
                    loadingIndicator.visibility = View.GONE
                    tokenViewModel.saveToken(apiResponse.data!!.token)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is ApiResponse.Failure -> {
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
