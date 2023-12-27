package com.example.taskermobile

import SharedPreferencesService
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.taskermobile.activities.backlogpage.BacklogPageFragment
import com.example.taskermobile.activities.project.ProjectsPageFragment
import com.example.taskermobile.activities.release.ReleasesPageFragment
import com.example.taskermobile.activities.users.UserFragment
import com.example.taskermobile.ui.theme.TextColor
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.utils.TokenRefresher
import com.example.taskermobile.utils.eventlisteners.AuthStateListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val tokenManager: TokenManager by inject()
    private val authStateListener: AuthStateListener by inject()

    private lateinit var tokenRefresher: TokenRefresher
    private lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        navigationController = navHostFragment.navController

        tokenRefresher = TokenRefresher(tokenManager, authStateListener)
        tokenRefresher.startTokenRefresh()

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        NavigationUI.setupWithNavController(bottomNavView, navigationController)
    }

     override fun onDestroy() {
        super.onDestroy()
        tokenRefresher.stopTokenRefresh()
    }
}