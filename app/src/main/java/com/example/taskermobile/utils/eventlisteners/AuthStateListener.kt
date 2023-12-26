package com.example.taskermobile.utils.eventlisteners

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.taskermobile.LoginActivity

interface AuthStateListener {
    fun onTokenExpired()
    fun onRefreshTokenFailed()
}

@SuppressLint("StaticFieldLeak")
object AuthEventListenerImplementation : AuthStateListener {

    // You need to set this context from an Activity or Application
    var context: Context? = null

    override fun onTokenExpired() {
        // This will be called when the token has expired.
        if (context != null && context is Activity) {
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
                navigateToLoginScreen(context!!)
            }
        }
    }

    override fun onRefreshTokenFailed() {
        // This will be called when the refresh token attempt fails.
        if (context != null && context is Activity) {
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Could not refresh session. Please log in again.", Toast.LENGTH_LONG).show()
                navigateToLoginScreen(context!!)
            }
        }
    }

    private fun navigateToLoginScreen(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}

