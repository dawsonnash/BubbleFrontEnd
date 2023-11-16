@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bubblefrontend

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.example.bubblefrontend.api.FeedData
import com.example.bubblefrontend.api.NonUser
import com.example.bubblefrontend.api.NonUserModel
import com.example.bubblefrontend.api.PostModel
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

class GlobalPage : ComponentActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    // Post data model instantiated and added to Global page
    private lateinit var postModel: PostModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the image URI
            imageUri.value = uri.toString()
        }
        postModel = ViewModelProvider(this)[PostModel::class.java]
        postModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // Default values for page and pageSize
        postModel.fetchPosts(page = 1, pageSize = 10)

        setContent {
            BubbleFrontEndTheme {
                GlobalScreen(postModel) { imagePickerLauncher.launch("image/*") }
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
fun GlobalScreen(postModel: PostModel, launchImagePicker: () -> Unit) {
    val posts = remember { mutableStateListOf(*initializePosts().toTypedArray()) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var showNewPostDialog by remember { mutableStateOf(false) }

    // For API called posts
    var postList by remember { mutableStateOf(listOf<FeedData>()) }
    LaunchedEffect(key1 = postModel) {
        // Apparently observeForever is a bad memory practice. try observeAsState
        postModel.postList.observeForever { newList ->
            postList = newList
        }
    }

    /*
    // Composable that displays the posts
    LazyColumn {
        items(postList) { post ->

            val imageURL = post.photo_url
            val baseURL = "http://54.202.77.126:8080"
            val fullImageURL = baseURL + imageURL
            Row() {
                Text(text = post.username)
                Image(
                    painter = rememberImagePainter(fullImageURL),  // Using Coil to load an image from a URL
                    contentDescription = "Post Photo",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
    */

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

                    // Need to calculate number of columns based on number of posts
                    // For some reason only works with 5
                    //val columns = 5

                    val columns = ceil(sqrt(postList.size.toDouble())).toInt()
                    val rows = ceil(postList.size.toDouble() / columns).toInt()
                    Log.d("GlobalScreen", "Number of columns: $columns")

                    val totalGridWidthPx = columns * horizontalDistancePx
                    val totalGridHeightPx = rows * verticalDistancePx
                    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

                    val maxHorizontalScrollPx = max(0f, totalGridWidthPx - screenWidthPx)
                    val maxVerticalScrollPx = max(0f, totalGridHeightPx - screenHeightPx)

                    val initialOffsetX = if (maxHorizontalScrollPx > 0) -maxHorizontalScrollPx / 2 else 0f
                    val initialOffsetY = if (maxVerticalScrollPx > 0) -maxVerticalScrollPx / 2 else 0f

                    var offsetX by remember { mutableStateOf(initialOffsetX) }
                    var offsetY by remember { mutableStateOf(initialOffsetY) }


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, _, _ ->
                                    Log.d("GlobalScreen", "Pan detected: ${pan.x}, ${pan.y}")
                                    val newOffsetX = (offsetX + pan.x).coerceIn(-maxHorizontalScrollPx, 0f)
                                    val newOffsetY = (offsetY + pan.y).coerceIn(-maxVerticalScrollPx, 0f)
                                    offsetX = newOffsetX
                                    offsetY = newOffsetY
                                }
                            }
                            .graphicsLayer(
                                translationX = offsetX,
                                translationY = offsetY
                            )
                    ) {
                        // Loop through 'postList' and calculate offsets
                        postList.forEachIndexed { index, postData ->
                            val col = index % columns
                            val row = index / columns
                            val xOffset = col * horizontalDistancePx
                            val yOffset = row * verticalDistancePx

                            Bubble(
                                post = postData,
                                modifier = Modifier
                                    .offset(
                                        x = with(density) { xOffset.toDp() },
                                        y = with(density) { yOffset.toDp() }
                                    )
                                    .size(400.dp)
                            )
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
fun Bubble(post: FeedData, modifier: Modifier) {
    // Define the Bubble UI with clickable behavior
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Magenta,
                        Color.Cyan,
                        Color.Yellow,
                        Color.Magenta
                    ),
                    center = Offset.Zero,
                    //BubbleRadiusPx
                    //radius = 4.3
                ),
                shape = CircleShape
            )
            .clickable {
                // Define what happens when a bubble is clicked
            },
        contentAlignment = Alignment.Center
    ) {
        Column() {
            Text(
                text = post.username ?: "filler info",
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = post.caption ?: "Default Caption",
                fontSize = 16.sp,
                color = Color.White
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