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
import com.example.taskermobile.viewmodels.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val sharedModel: SharedViewModel by viewModel()

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
                        MyButton()

                        MyReleasesButton()
                        
                        BacklogButton(projectId = "c9efa3be-730e-4fac-a6ef-34fb97b9c972")
                    }
                }
            }
        }
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
fun MyButton() {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, ProjectsPageActivity::class.java)
            context.startActivity(intent)
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ) {
        Text("Navigate to Second Activity")
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