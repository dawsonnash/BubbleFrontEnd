@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bubblefrontend


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

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

data class Post(val text: String, var x: Float, var y: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalScreen() {
    var postText by remember { mutableStateOf("") }
    var gridPosition by remember { mutableStateOf("") }
    var isPostMenuVisible by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var posts by remember { mutableStateOf(mutableListOf<Post>()) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    translationX = offsetX,
                    translationY = offsetY
                )
        ) {
            posts.forEach { post ->
                Button(
                    onClick = { /* Do something when post is clicked */ },
                    modifier = Modifier
                        .offset(x = post.x.dp, y = post.y.dp)
                        .size(300.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                        .graphicsLayer(
                            shadowElevation = 10.dp.value,
                            shape = CircleShape,
                            clip = true
                        )
                ) {
                    Text(text = post.text, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            if (isPostMenuVisible) {
                OutlinedTextField(
                    value = postText,
                    onValueChange = { postText = it },
                    label = { Text("Write your post") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = gridPosition,
                    onValueChange = { gridPosition = it },
                    label = { Text("Grid Position (X,Y)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    try {
                        val (xStr, yStr) = gridPosition.split(",").map { it.trim() }
                        val x = xStr.toFloat() * 300  // Example multiplier for positioning
                        val y = yStr.toFloat() * 300
                        val newPost = Post(postText, x, y)
                        posts.add(newPost)
                        postText = ""
                        gridPosition = ""
                    } catch (e: Exception) {
                        // Handle invalid input
                    }
                    isPostMenuVisible = false
                }) {
                    Text("Post")
                }
            }

            Button(onClick = { isPostMenuVisible = !isPostMenuVisible }) {
                Text("+")
            }

            Spacer(modifier = Modifier.height(16.dp))
            BottomDashboard()
        }
    }
}

@Composable
fun BottomDashboard() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
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
                    // Update with the correct class name for UserSearchPage
                    val intent = Intent(context, UserSearchPage::class.java)
                    context.startActivity(intent)
                }
            )
            DashboardButton(
                title = "Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    // Update with the correct class name for ProfilePage
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
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = title)
        Text(text = title)
    }
}