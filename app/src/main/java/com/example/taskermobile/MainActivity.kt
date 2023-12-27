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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.taskermobile.activities.backlogpage.BacklogPageFragment
import com.example.taskermobile.activities.project.ProjectsPageFragment
import com.example.taskermobile.activities.release.ReleasesPageFragment
import com.example.taskermobile.activities.users.UserFragment
import com.example.taskermobile.ui.theme.TaskerMobileTheme
import com.example.taskermobile.ui.theme.TextColor
import com.example.taskermobile.utils.TokenManager
import com.example.taskermobile.utils.TokenRefresher
import com.example.taskermobile.utils.eventlisteners.AuthStateListener
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

        
    }

     override fun onDestroy() {
        super.onDestroy()
        tokenRefresher.stopTokenRefresh()
    }

    private fun navigateToTab(selectedTab: String) {
        when (selectedTab) {
            "projects" -> loadFragment(ProjectsPageFragment())
            "user" -> loadFragment(UserFragment())
            "backlog" -> loadFragment(BacklogPageFragment())
            "release" -> loadFragment(ReleasesPageFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
}

@Composable
fun BottomNavigationBar(onTabSelected: (String) -> Unit) {
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
            if (SharedPreferencesService(LocalContext.current).retrieveData("lastProjectActive") != null) {
                ReleasesButton(onTabSelected)
                BacklogButton(onTabSelected)
            }
            UserButton(onTabSelected)
        }
    }
}

@Composable
fun ProjectsButton(onTabSelected: (String) -> Unit) {
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
fun UserButton(onTabSelected: (String) -> Unit) {
    val userImage = painterResource(id = R.drawable.user)
    Box(
        modifier = Modifier
            .width(40.dp)
            .clickable { onTabSelected("user") },
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


@Composable
fun ReleasesButton(onTabSelected: (String) -> Unit) {
    val releaseImage = painterResource(id = R.drawable.release)
    Box(
        modifier = Modifier
            .width(65.dp)
            .clickable { onTabSelected("user") },
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
fun BacklogButton(onTabSelected: (String) -> Unit) {
    val backlogImage = painterResource(id = R.drawable.backlog)
    Box(
        modifier = Modifier
            .width(50.dp)
            .clickable { onTabSelected("backlog") },
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



