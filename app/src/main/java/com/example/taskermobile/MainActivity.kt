package com.example.taskermobile

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.taskermobile.activities.project.ProjectUpdateActivity
import com.example.taskermobile.activities.project.ProjectsPageFragment
import com.example.taskermobile.activities.release.ReleasesPageActivity
import com.example.taskermobile.activities.users.UserActivity
import com.example.taskermobile.ui.theme.TaskerMobileTheme
import com.example.taskermobile.ui.theme.TextColor
import com.example.taskermobile.utils.eventlisteners.AuthStateListener
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.utils.TokenRefresher
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val tokenManager: TokenManager by inject()
    private val authStateListener: AuthStateListener by inject()

    private lateinit var tokenRefresher: TokenRefresher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskerMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = { BottomNavigationBar { selectedTab -> navigateToTab(selectedTab) } }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AndroidView(
                                factory = { context ->
                                    FragmentContainerView(context).apply {
                                        id = R.id.fragment_container
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        tokenRefresher = TokenRefresher(tokenManager, authStateListener)
        tokenRefresher.startTokenRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenRefresher.stopTokenRefresh()
    }

    private fun navigateToTab(selectedTab: String) {
        when (selectedTab) {
            "projects" -> loadFragment(ProjectsPageFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

@Composable
fun BottomNavigationBar(onTabSelected: (String) -> Unit) {
    val context = LocalContext.current
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProjectsButton(onTabSelected)
            ReleasesButton(modifier = Modifier.weight(1f))
            BacklogButton(projectId = "3bc90a0a-29bf-4d63-ac7b-3c061da50883", modifier = Modifier.weight(1f))
            UserButton(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ProjectsButton(onTabSelected: (String) -> Unit) {
    val context = LocalContext.current
    val projectImage = painterResource(id = R.drawable.project)
    Box(
        modifier = Modifier
            .width(60.dp)
            .clickable { onTabSelected("projects") },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = projectImage,
                contentDescription = "Projects",
                modifier = Modifier
                    .height(35.dp)
            )
            Text(
                "Projects",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextColor
                )
            )
        }
    }
}


@Composable
fun ReleasesButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val releaseImage = painterResource(id = R.drawable.release)

    Box(
        modifier = Modifier
            .width(65.dp)
            .clickable {
                val intent = Intent(context, ReleasesPageActivity::class.java)
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = releaseImage,
                contentDescription = "Releases",
                modifier = Modifier
                    .height(35.dp)
            )
            Text(
                "Releases",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextColor
                )
            )
        }
    }
}


@Composable
fun BacklogButton(projectId: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val backlogImage = painterResource(id = R.drawable.backlog)
    Box(
        modifier = Modifier
            .width(50.dp)
            .clickable {
                val intent = Intent(context, BacklogPageActivity::class.java)
                intent.putExtra("projectId", projectId)
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = backlogImage,
                contentDescription = "Backlog",
                modifier = Modifier
                    .height(35.dp)
            )
            Text(
                "Backlog",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextColor
                )
            )
        }
    }
}

@Composable
fun ProjectUpdateButton(projectId: String, modifier: Modifier = Modifier) {
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
fun UserButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userImage = painterResource(id = R.drawable.user)
    Box(
        modifier = Modifier
            .width(40.dp)
            .clickable {
                val intent = Intent(context, UserActivity::class.java)
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = userImage,
                contentDescription = "User",
                modifier = Modifier.height(35.dp)
            )
            Text(
                "User",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextColor
                )
            )
        }
    }
}


