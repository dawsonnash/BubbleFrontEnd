package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.bubblefrontend.api.NonUser
import com.example.bubblefrontend.api.NonUserModel
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import com.google.gson.Gson

class UserSearchPage : ComponentActivity() {
    private lateinit var nonUserModel: NonUserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nonUserModel = ViewModelProvider(this)[NonUserModel::class.java]

        nonUserModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        nonUserModel.fetchUsers()

        setContent {
            BubbleFrontEndTheme {
                UserSearchScreen(nonUserModel)
            }
        }
    }
}

@Composable
fun UserSearchScreen(userViewModel: NonUserModel){

    val userList = remember { mutableStateOf(listOf<NonUser>()) }
    LaunchedEffect(key1 = userViewModel) {
        userViewModel.userList.observeForever { newList ->
            userList.value = newList
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Set the image as the background
        Image(
            painter = painterResource(id = R.drawable.bubblebackground01),
            contentDescription = "Background",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        SearchBar(userList = userList.value)
        BottomDashboard()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(userList: List<NonUser>) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = userList.filter {
        it.username.contains(searchQuery, ignoreCase = true) ||
                it.name.contains(searchQuery, ignoreCase = true)
    }

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
                .background(
                    color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
                    shape = RoundedCornerShape(4.dp) // Rounded corners

                )
        )

        // Display results
        if (searchQuery.isNotEmpty()) { // Only show the list when the user is actively searching
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(filteredUsers) { user ->
                    // Style that user info is displayed
                    Text("${user.name} ~ ${user.username}",
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
                                shape = RoundedCornerShape(4.dp) // Rounded corners

                            )
                            .clickable {
                                val gson = Gson()
                                val userJson = gson.toJson(user)
                                val intent = Intent(context, NonUserPage::class.java).apply {
                                    putExtra("NON_USER_JSON", userJson)
                                }
                                context.startActivity(intent)

                            }
                    )
                    Divider()
                }
            }
        }
    }
}
