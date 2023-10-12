package com.example.bubblefrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.graphicsLayer


@OptIn(ExperimentalMaterial3Api::class)
class GlobalPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                GlobalScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalScreen() {
    var postText by remember { mutableStateOf("") }
    var isPostMenuVisible by remember { mutableStateOf(false) }
    var posts by remember { mutableStateOf(listOf<String>()) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        // For the posts that move with panning
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    translationX = offsetX,
                    translationY = offsetY
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Display all the posts
                posts.reversed().forEach { post ->
                    Text(text = post, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        // Fixed position UI elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))  // Pushes the items below to the bottom

            // Post creation menu (visible when isPostMenuVisible is true)
            if (isPostMenuVisible) {
                OutlinedTextField(
                    value = postText,
                    onValueChange = { postText = it },
                    label = { Text("Write your post") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    posts = posts + postText  // Add the new post to the list
                    postText = ""  // Clear the input field
                    isPostMenuVisible = false  // Hide the post creation menu
                }) {
                    Text("Post")
                }
            }

            // Plus button to toggle post creation menu
            Button(onClick = { isPostMenuVisible = !isPostMenuVisible }) {
                Text("+")
            }

            Spacer(modifier = Modifier.height(16.dp))  // Space between the plus button and dashboard
            BottomDashboard()
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
                    val intent = Intent(context, GlobalPage::class.java)
                    context.startActivity(intent)
                }
            )

            DashboardButton(
                title = "Search",
                icon = Icons.Default.Search,
                onClick = {
                    val intent = Intent(context, UserSearchPage::class.java)
                    context.startActivity(intent)
                }
            )

            DashboardButton(
                title = "Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    val intent = Intent(context, ProfilePage::class.java)
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
