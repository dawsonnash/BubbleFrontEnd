@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bubblefrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import kotlin.math.sqrt

class GlobalPage : ComponentActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the image URI
            imageUri.value = uri.toString()
        }

        setContent {
            BubbleFrontEndTheme {
                GlobalScreen { imagePickerLauncher.launch("image/*") }
            }
        }
    }
    companion object {
        var imageUri = mutableStateOf<String?>(null)
    }
}


data class Post(
    val text: String?,
    val pictureUrl: String? // Assuming pictures are represented by URLs
    //Example: Post(text = "Some text", pictureUrl = "http://example.com/picture.jpg")
    //Example: Post(text = null, pictureUrl = "http://example.com/picture.jpg")
)

fun initializePosts(): List<Post> {
    val posts = mutableListOf<Post>()
    for (i in 1..625) {
        posts.add(Post(text = "Initialization Post $i", pictureUrl = null)) // Adding a number for differentiation
    }
    return posts
}

@Composable
fun FullScreenPostView(post: Post, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Button(onClick = onBack) {
                Text("Back")
            }

            Text(
                text = post.text ?: "",
                fontSize = 20.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Display the actual image if there is a picture URL
            post.pictureUrl?.let { imageUrl ->
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // Adjust aspect ratio as needed
                )
            }
        }
    }
}

@Composable
fun GlobalScreen(launchImagePicker: () -> Unit) {
    val posts = remember { mutableStateListOf(*initializePosts().toTypedArray()) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var showNewPostDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (selectedPost != null) {
                    FullScreenPostView(post = selectedPost!!, onBack = { selectedPost = null })
                } else {
                    // Render the grid of posts
                    val configuration = LocalConfiguration.current
                    val density = LocalDensity.current
                    val bubbleSizePx = with(density) { 400.dp.toPx() }
                    val bubbleRadiusPx = bubbleSizePx / 1.25f
                    val verticalDistancePx = bubbleRadiusPx * sqrt(3f) * 3 / 4
                    val horizontalDistancePx = bubbleRadiusPx * 2 * 0.75f
                    val columns = 25
                    val totalGridWidthPx = columns * horizontalDistancePx
                    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
                    val maxHorizontalScrollPx =
                        totalGridWidthPx - (1.2f * screenWidthPx) + (horizontalDistancePx / 2)
                    val maxVerticalScrollPx =
                        (totalGridWidthPx - (2 * screenHeightPx) + (horizontalDistancePx / 2)) / 1.5f
                    val initialOffsetX = -maxHorizontalScrollPx / 2
                    val initialOffsetY = -maxVerticalScrollPx / 2
                    var offsetX by remember { mutableStateOf(initialOffsetX) }
                    var offsetY by remember { mutableStateOf(initialOffsetY) }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, _, _ ->
                                    val newOffsetX =
                                        (offsetX + pan.x).coerceIn(-maxHorizontalScrollPx, 0f)
                                    val newOffsetY =
                                        (offsetY + pan.y).coerceIn(-maxVerticalScrollPx, 0f)
                                    offsetX = newOffsetX
                                    offsetY = newOffsetY
                                }
                            }
                            .graphicsLayer(
                                translationX = offsetX,
                                translationY = offsetY
                            )
                    ) {
                        posts.forEachIndexed { index, post ->
                            val col = index % columns
                            val row = index / columns
                            val xOffset =
                                if (row % 2 == 0) col * horizontalDistancePx else col * horizontalDistancePx + horizontalDistancePx / 2
                            val yOffset = row * verticalDistancePx * 3 / 4

                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = with(density) { xOffset.toDp() },
                                        y = with(density) { yOffset.toDp() })
                                    .size(400.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.Magenta,
                                                Color.Cyan,
                                                Color.Yellow,
                                                Color.Magenta
                                            ),
                                            center = Offset.Zero,
                                            radius = bubbleRadiusPx
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(16.dp)
                                    .clickable {
                                        selectedPost = post // Update the state to the clicked post
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.text ?: "",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            BottomDashboard()
        }

        // Floating action button
        FloatingActionButton(
            onClick = { showNewPostDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Post")
        }

        // Show dialog for new post creation
        if (showNewPostDialog) {
            CreatePostDialog(
                onPostCreate = { newText, newImageUri ->
                    posts.add(0, Post(text = newText, pictureUrl = newImageUri)) // Add new post at the top
                    posts.removeLast() // Remove the oldest post
                    GlobalPage.imageUri.value = null // Reset the image URI
                    showNewPostDialog = false
                },
                onDismiss = {
                    GlobalPage.imageUri.value = null // Reset the image URI
                    showNewPostDialog = false
                },
                launchImagePicker = launchImagePicker
            )
        }
    }
}

@Composable
fun CreatePostDialog(onPostCreate: (String, String?) -> Unit, onDismiss: () -> Unit, launchImagePicker: () -> Unit) {
    var text by remember { mutableStateOf("") }
    val pickedImageUri = GlobalPage.imageUri.value

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Post") },
        text = {
            Column {
                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    placeholder = { Text("Enter post content") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { launchImagePicker() }) {
                    Text("Add Picture")
                }
                if (pickedImageUri != null) {
                    Image(
                        painter = rememberImagePainter(pickedImageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp) // Adjust the size as needed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotEmpty()) {
                        onPostCreate(text, pickedImageUri)
                    }
                }
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}