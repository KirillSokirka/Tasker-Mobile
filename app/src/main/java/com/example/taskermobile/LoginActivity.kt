package com.example.taskermobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.model.auth.LoginModel
import com.example.taskermobile.ui.theme.BackgroundColor
import com.example.taskermobile.ui.theme.HighLightColor
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.utils.eventlisteners.AuthEventListenerImplementation
import com.example.taskermobile.utils.eventlisteners.AuthStateListener
import com.example.taskermobile.viewmodels.AuthViewModel
import com.example.taskermobile.viewmodels.TokenViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModel()
    private val tokenViewModel: TokenViewModel by viewModel()
    private val authStateListener: AuthStateListener by inject()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var overlayView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthEventListenerImplementation.context = this

        setContentView(R.layout.activity_login)


        loadingIndicator = findViewById(R.id.loadingIndicator)
        overlayView = findViewById(R.id.overlayView)

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
                is ApiResponse.Loading -> showLoading()

                is ApiResponse.Success -> {
                    hideLoading()
                    tokenViewModel.saveToken(apiResponse.data!!.token)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        this@LoginActivity,
                        "Network error: ${apiResponse.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        overlayView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
        overlayView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (AuthEventListenerImplementation.context == this) {
            AuthEventListenerImplementation.context = applicationContext
        }
    }
}
