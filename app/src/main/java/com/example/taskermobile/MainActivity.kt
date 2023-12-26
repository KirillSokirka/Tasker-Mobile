package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.taskermobile.ui.theme.TaskerMobileTheme
import com.example.taskermobile.utils.AuthStateListener
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.utils.TokenRefresher
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val tokenManager: TokenManager by inject()
    private val authStateListener: AuthStateListener by inject()

    private lateinit var tokenRefresher: TokenRefresher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            TaskerMobileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Use a column to vertically align the button
                    Column(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Call your composable function to display text
                        Greeting("Android")

                        // Call a composable function to display a button
                        ProjectsButton()

                        MyReleasesButton()
                        
                        BacklogButton(projectId = "3bc90a0a-29bf-4d63-ac7b-3c061da50883")
                        ProjectUpdateButton(projectId = "3bc90a0a-29bf-4d63-ac7b-3c061da50883")
                        UserButton()
                    }
                }
            }

            tokenRefresher = TokenRefresher(tokenManager, authStateListener)
            tokenRefresher.startTokenRefresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenRefresher.stopTokenRefresh()
    }
}

@Composable
fun Greeting(name: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun ProjectsButton() {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, ProjectsPageActivity::class.java)
            context.startActivity(intent)
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        Text("Navigate to Project List Activity")
    }
}

@Composable
fun MyReleasesButton() {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, ReleasesPageActivity::class.java)
            context.startActivity(intent)
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        Text("Navigate to releases Activity")
    }
}

@Composable
fun BacklogButton(projectId: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, BacklogPageActivity::class.java)
            intent.putExtra("projectId", projectId)
            context.startActivity(intent)
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        Text("Navigate to backlog")
    }
}
@Composable
fun ProjectUpdateButton(projectId: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, ProjectUpdateActivity::class.java)
            intent.putExtra("projectId", projectId)
            context.startActivity(intent)
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        Text("Navigate to update project")
    }
}

@Composable
fun UserButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, UserActivity::class.java)
            context.startActivity(intent)
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        Text("Navigate to user")
    }
}