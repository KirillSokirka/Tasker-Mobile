package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskermobile.model.auth.RegisterModel
import com.example.taskermobile.utils.ApiResponse
import com.example.taskermobile.viewmodels.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModel()

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var overlayView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        overlayView = findViewById(R.id.overlayView)

        val emailText: EditText = findViewById(R.id.email)
        val usernameText: EditText = findViewById(R.id.username)
        val passwordText: EditText = findViewById(R.id.passsword)

        val buttonRegister: Button = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val email = emailText.text.toString().trim()
            val username = usernameText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            viewModel.register(RegisterModel(email, username, password))
        }

        viewModel.registerResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> showLoading()

                is ApiResponse.Success -> {
                    hideLoading()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is ApiResponse.Failure -> {
                    hideLoading()
                    Toast.makeText(
                        this@RegisterActivity,
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
}

