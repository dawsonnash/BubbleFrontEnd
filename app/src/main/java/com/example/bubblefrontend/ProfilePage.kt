package com.example.bubblefrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class ProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween  // Places children at the start and end
                    ){
                        SearchButton()
                        LogoutButton()
                    }

            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Will need to be updated upon working with mySQL database
fun SearchButton() {
    var searchQuery by remember { mutableStateOf("") }
    val allUsernames = UserManager.getAllUsernames() // Assume this returns a list of usernames
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
            modifier = Modifier.fillMaxWidth()
        )

        // Display results
        if (searchQuery.isNotEmpty()) { // Only show the list when there's a query
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)

            ) {
                items(filteredUsernames) { username ->
                    val user = UserManager.getUserDetails(username) // Assume this gets the User object for a username
                    if (user != null) {
                        Text("${user.firstName} ${user.lastName} ~ ${user.username}", style = TextStyle(fontSize = 20.sp))
                    }
                    Divider()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun WelcomeMessagePreview() {
    BubbleFrontEndTheme {
    }
}


