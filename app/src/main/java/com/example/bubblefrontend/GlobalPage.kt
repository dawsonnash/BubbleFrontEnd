@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.api.FeedData
import com.example.bubblefrontend.api.NonUserModel
import com.example.bubblefrontend.api.PostModel
import com.example.bubblefrontend.api.UiFeedData
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import com.google.gson.Gson
import kotlin.math.sqrt

class GlobalPage : ComponentActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    // Post data model and nonUserModel added to Global page
    private lateinit var postModel: PostModel
    private lateinit var nonUserModel: NonUserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                // Handle the image URI
                imageUri.value = uri
            }
        // Instantiating post model
        postModel = ViewModelProvider(this)[PostModel::class.java]
        postModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        // Instantiating nonUserModel
        nonUserModel = ViewModelProvider(this)[NonUserModel::class.java]
        nonUserModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        // Default values for page and pageSize
        postModel.fetchPosts(page = 1, pageSize = 12)
        nonUserModel.fetchUsers()


        setContent {
            BubbleFrontEndTheme {
                GlobalScreen(postModel, nonUserModel) { imagePickerLauncher.launch("image/*") }
            }
        }
    }

    companion object {
        var imageUri = mutableStateOf<Uri?>(null)
    }
}


@Composable
fun FullScreenPostView(
    post: FeedData,
    uiFeedData: LiveData<List<UiFeedData>>,
    apiHandler: ApiHandler,
    nonUserModel: NonUserModel,
    context: Context,
    onBack: () -> Unit
) {

    // Observe the singleUser LiveData and react to changes
    val user by nonUserModel.singleUser.observeAsState()

    // This allows for the heart icon to change state in real-time
    val uiPosts by uiFeedData.observeAsState(initial = listOf())
    val uiPost = uiPosts.find { it.feedData.postID == post.postID }

    // Converting image urls to their full HTTP urls
    val postImageURL = post.photo_url
    val baseURL = "http://54.202.77.126:8080"
    val fullPostImageURL = baseURL + postImageURL
    val profileImageURL = post.profile_picture
    val fullProfileImageURL = baseURL + profileImageURL

    // To show the popup message for deleting posts
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Log.d("deleteButton", "posts UID: ${post.uid}")
    Log.d("deleteButon", "user's UID ${user?.uid}")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.8f))
    ) {
        Column {
            Button(onClick = onBack) {
                Text("Back")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    // This crashes the app, but will eventually go to user's page
                    .clickable {
                        val gson = Gson()
                        val userJson = gson.toJson(user)
                        val intent = Intent(context, NonUserPage::class.java).apply {
                            putExtra("NON_USER_JSON", userJson)
                        }
                        context.startActivity(intent)
                    }
            ) {
                // Profile picture
                Image(
                    painter = rememberImagePainter(
                        data = fullProfileImageURL,
                        builder = {
                            crossfade(true)     // For a smooth image loading transition
                        }
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp) // Set size of profile image
                        .clip(CircleShape)
                        .background(Color.Gray), // Placeholder background
                    contentScale = ContentScale.Crop


                )

                Spacer(Modifier.width(8.dp)) // Space between the image and the text

                // Username text
                Text(
                    text = post.username,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp) // Add padding between text and profile picture
                )

                Spacer(Modifier.weight(1f))
                Text(
                    text = post.timeAgo,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp) // Add padding between text and profile picture
                )
            }
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp)
            ) {

                Text(
                    text = post.caption,
                    fontSize = 20.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                // Display the photo if photo exists
                if (post.photo == "1") {
                    Image(
                        painter = rememberImagePainter(fullPostImageURL),
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // Adjust aspect ratio as needed
                    )
                }
            }
                // Delete Icon and Like Icon row
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    // Extract User's UID to compare to the post's UID in order to delete post
                    val profileSharedPreferences =
                        context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
                    val usersUID = profileSharedPreferences.getInt("uid", 0)
                    if (post.uid == usersUID) {
                        IconButton(onClick = {
                            showDeleteConfirmation = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete, // Trashcan icon
                                contentDescription = "Delete",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        if (showDeleteConfirmation) {
                            AlertDialog(
                                onDismissRequest = { showDeleteConfirmation = false },
                                title = { Text("Confirm Delete") },
                                text = { Text("Are you sure you want to delete?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            showDeleteConfirmation = false
                                            apiHandler.deletePost(post.postID, context)

                                            // Right now it just refreshes global page once you delete to refresh posts
                                            val intent = Intent(context, GlobalPage::class.java)
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showDeleteConfirmation = false }) {
                                        Text("No")
                                    }
                                },
                                properties = DialogProperties(
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (uiPost != null) {
                        HeartIcon(post, uiPost, apiHandler, context, modifier = Modifier)
                        Log.d("Debug", "Has Liked: ${post.hasLiked}")
                        Log.d("Debug", "UI has Liked: ${uiPost.hasLiked}")
                    } else {
                        // do something if uiPost is null
                    }

            }
        }
    }
}

@Composable
fun HeartIcon(
    post: FeedData,
    uiFeedData: UiFeedData,
    apiHandler: ApiHandler,
    context: Context,
    modifier: Modifier
) {

    val hasLiked by uiFeedData.hasLiked
    val likeCount by uiFeedData.likeCount


    val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    val uid = profileSharedPreferences.getInt("uid", 0)

    Icon(
        imageVector = if (hasLiked == 1) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = if (hasLiked == 1) "Liked" else "Not Liked",
        tint = if (hasLiked == 1) Color.Red else Color.Gray,
        modifier = modifier
            .clickable {
                // 8 is me (dawson nash)
                if (hasLiked == 0) {
                    apiHandler.likePost(uid, post.postID, uiFeedData, post, context)
                } else {
                    apiHandler.unlikePost(uid, post.postID, uiFeedData, post, context)
                }
            }
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = "$likeCount")
}


@Composable
fun GlobalScreen(postModel: PostModel, nonUserModel: NonUserModel, launchImagePicker: () -> Unit) {

    val context = LocalContext.current
    var showNewPostDialog by remember { mutableStateOf(false) }

    var showPostContent by remember { mutableStateOf(false) }
    var selectedPostData by remember { mutableStateOf<FeedData?>(null) }

    // Instance for all API calls
    val apiHandler = ApiHandler()

    apiHandler.getUID(context)
    // For API called posts
    var postList by remember { mutableStateOf(listOf<FeedData>()) }
    // For reactive UI elements
    val uiPostList by postModel.uiPostList.observeAsState(initial = listOf())

    LaunchedEffect(key1 = postModel) {
        // Apparently observeForever is a bad memory practice. try observeAsState
        postModel.postList.observeForever { newList ->
            postList = newList
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Render the grid of posts
                val configuration = LocalConfiguration.current
                val density = LocalDensity.current
                val bubbleSizePx = with(density) { 400.dp.toPx() }
                val bubbleRadiusPx = bubbleSizePx / 1.25f
                val verticalDistancePx = bubbleRadiusPx * sqrt(3f) * 3 / 4
                val horizontalDistancePx = bubbleRadiusPx * 2 * 0.75f

                val columns = 4
                // Need to come up with a way that determines number of bubbles
                // val columns = ceil(sqrt(postList.size.toDouble())).toInt()
                // val rows = ceil(postList.size.toDouble() / columns).toInt()
                // Log.d("GlobalScreen", "Number of columns: $columns")

                val totalGridWidthPx = columns * horizontalDistancePx
                val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

                val maxHorizontalScrollPx =
                    totalGridWidthPx - (1.2f * screenWidthPx) + (horizontalDistancePx / 2)
                val maxVerticalScrollPx =
                    (totalGridWidthPx - (2 * screenHeightPx) + (horizontalDistancePx / 2)) / 1.5f

                val initialOffsetX =
                    if (maxHorizontalScrollPx > 0) -maxHorizontalScrollPx / 2 else 0f
                val initialOffsetY =
                    if (maxVerticalScrollPx > 0) -maxVerticalScrollPx / 2 else 0f

                var offsetX by remember { mutableStateOf(initialOffsetX) }
                var offsetY by remember { mutableStateOf(initialOffsetY) }


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, _, _ ->
                                //  Log.d("GlobalScreen", "Pan detected: ${pan.x}, ${pan.y}")
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
                    // Loop through 'postList' and calculate offsets
                    postList.forEachIndexed { index, postData ->
                        val col = index % columns
                        val row = index / columns
                        val xOffset = col * horizontalDistancePx
                        val yOffset = row * verticalDistancePx

                        Bubble(
                            post = postData, apiHandler,
                            showPostContent = showPostContent,
                            modifier = Modifier
                                .offset(
                                    x = with(density) { xOffset.toDp() },
                                    y = with(density) { yOffset.toDp() }
                                )
                                .size(400.dp)
                                .clickable {
                                    selectedPostData = postData
                                    showPostContent = true // Show the full screen content
                                }

                        )
                        if (showPostContent && selectedPostData != null) {
                            Dialog(onDismissRequest = { showPostContent = false }) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    FullScreenPostView(
                                        post = selectedPostData!!, postModel.uiPostList, apiHandler,
                                        nonUserModel,
                                        context,
                                        onBack = { showPostContent = false }
                                    )
                                }
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
                context, apiHandler, postModel,
                onPostCreate = { caption, pickedImageUri ->
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
fun Bubble(post: FeedData, apiHandler: ApiHandler, showPostContent: Boolean, modifier: Modifier) {

    val postImageURL = post.photo_url
    val baseURL = "http://54.202.77.126:8080"
    val fullPostImageURL = baseURL + postImageURL

    val profileImageURL = post.profile_picture
    val fullProfileImageURL = baseURL + profileImageURL

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
                ),
                shape = CircleShape
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row() {
                Image(
                    painter = rememberImagePainter(
                        data = fullProfileImageURL,
                        builder = {
                            crossfade(true)     // For a smooth image loading transition
                        }
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp) // Set size of profile image
                        .clip(CircleShape)
                        .background(Color.Gray), // Placeholder background
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = post.username,
                    fontSize = 30.sp,
                    color = Color.White
                )
            }
            if (post.photo == "1") {
                Image(
                    painter = rememberImagePainter(
                        data = fullPostImageURL,
                        builder = {
                            crossfade(true)     // For a smooth image loading transition
                        }
                    ),
                    contentDescription = "Post Picture",
                    modifier = Modifier
                        .size(140.dp) // Set size of profile image
                        .clip(CircleShape)
                        .background(Color.Gray), // Placeholder background
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = post.caption,
                fontSize = 16.sp,
                color = Color.White
            )


        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    context: Context,
    apiHandler: ApiHandler,
    postModel: PostModel,
    onPostCreate: (String, Uri?) -> Unit,
    onDismiss: () -> Unit,
    launchImagePicker: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    val pickedImageUri = GlobalPage.imageUri.value

    val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    val username = profileSharedPreferences.getString("username", "")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Post") },
        text = {
            Column {
                TextField(
                    value = caption,
                    onValueChange = { newText -> caption = newText },
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
                    if (caption.isNotEmpty()) {
                        if (username != null) {
                            apiHandler.createNewPost(username, caption, pickedImageUri, context)

                            // Right now, once you create new post it just navigates refreshes global page
                            val intent = Intent(context, GlobalPage::class.java)
                            context.startActivity(intent)

                        }
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