package com.example.bubblefrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = "Global", style = TextStyle(fontSize = 50.sp))
        BottomDashboard()
        }
    }


@Composable
fun LogoutButton(){
    val context = LocalContext.current              // For transitioning to other activities
    Button(
        onClick = {

            val intent = Intent(context, WelcomePage::class.java)
            context.startActivity(intent)
        }
    ) {
        Text("Logout")
    }
}

@Composable
fun ProfileButton(){
    val context = LocalContext.current              // For transitioning to other activities
    IconButton(onClick = {
        val intent = Intent(context, ProfilePage::class.java)
        context.startActivity(intent)
    },
        modifier = Modifier.size(48.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_icon),  // Replace with your own drawable resource
            contentDescription = "Profile",
            modifier = Modifier.padding(8.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Will need to be updated upon working with mySQL database
fun SearchBar() {
    var searchQuery by remember { mutableStateOf("") }
    val allUsernames = UserManager.getAllUsernames()
    val filteredUsernames = allUsernames.filter { it.contains(searchQuery, ignoreCase = true) }
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Users") },
            modifier = Modifier
                .fillMaxWidth()
        )

        // Display results
        if (searchQuery.isNotEmpty()) { // Only show the list when the user is actively searching
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)

            ) {
                items(filteredUsernames) { username ->
                    val user = UserManager.getUserDetails(username)
                    if (user != null) {
                        // Style that user info is displayed
                        Text("${user.firstName} ${user.lastName} ~ ${user.username}", style = TextStyle(fontSize = 20.sp))
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun BottomDashboard() {
    val context = LocalContext.current  // Obtain the current context

    // Bottom bar shape and background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardButton(
                title = "Global",
                icon = Icons.Default.Home,
                onClick = {
                    val intent = Intent(context, HomePage::class.java)  // Replace with your Activity
                    context.startActivity(intent)
                }
            )

            DashboardButton(
                title = "Search",
                icon = Icons.Default.Search,
                onClick = {
                    val intent = Intent(context, UserSearchPage::class.java)  // Replace with your Activity
                    context.startActivity(intent)
                }
            )

            DashboardButton(
                title = "Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    val intent = Intent(context, ProfilePage::class.java)  // Replace with your Activity
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun DashboardButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = title)
        Text(text = title)
    }
}

