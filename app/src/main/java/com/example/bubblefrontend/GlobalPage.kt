package com.example.bubblefrontend

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
import androidx.compose.ui.unit.dp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import android.content.Intent

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

data class Post(val text: String, var x: Float, var y: Float, var isFull: Boolean = false)

fun positionNewPost(newPost: Post, existingPosts: MutableList<Post>) {
    val possibleDirections = listOf(
        Pair(0f, -150f), Pair(0f, 150f), Pair(-150f, 0f), Pair(150f, 0f)
    )

    var attempts = 0
    var placed = false
    var basePost: Post

    while (!placed) {
        val availablePosts = existingPosts.filter { !it.isFull }
        if (availablePosts.isNotEmpty()) {
            basePost = availablePosts.random()
            val direction = possibleDirections.random()
            newPost.x = basePost.x + direction.first
            newPost.y = basePost.y + direction.second

            if (existingPosts.none { isOverlapping(it, newPost) }) {
                placed = true
            } else {
                attempts++
                if (attempts >= 100) {
                    basePost.isFull = true
                    attempts = 0
                }
            }
        } else {
            newPost.x = 0f
            newPost.y = 0f
            placed = true
        }
    }
}

fun isOverlapping(post1: Post, post2: Post): Boolean {
    val horizontalOverlap = Math.abs(post1.x - post2.x) < 150
    val verticalOverlap = Math.abs(post1.y - post2.y) < 150
    return horizontalOverlap && verticalOverlap
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalScreen() {
    var postText by remember { mutableStateOf("") }
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(x = post.x.dp, y = post.y.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(text = post.text)
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
                Button(onClick = {
                    val newPost = Post(postText, 0f, 0f)
                    positionNewPost(newPost, posts)
                    posts.add(newPost)
                    postText = ""
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


