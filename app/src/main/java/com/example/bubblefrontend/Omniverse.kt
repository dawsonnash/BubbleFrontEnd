@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import androidx.compose.ui.geometry.Size
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.api.FeedData
import com.example.bubblefrontend.api.NonUserModel
import com.example.bubblefrontend.api.PostModel
import com.example.bubblefrontend.api.UiFeedData
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.VisibleRegion
import com.google.gson.Gson
import kotlinx.coroutines.delay
import android.graphics.Canvas
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan

class Omniverse : ComponentActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    // Post data model and nonUserModel added to Global page
    private lateinit var postModel: PostModel
    private lateinit var nonUserModel: NonUserModel
    private val postTileMap = mutableMapOf<Pair<Int, Int>, FeedData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Clear the tile map to refresh post positions
        postTileMap.clear()
        postIndex = 0

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                // Handle the image URI
                Omniverse.imageUri.value = uri
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
        postModel.fetchPosts(page = 1, pageSize = 20)
        nonUserModel.fetchUsers()


        setContent {
            BubbleFrontEndTheme {
                OmniverseScreen(postModel, nonUserModel, postTileMap){ imagePickerLauncher.launch("image/*") }
            }
        }
    }
    companion object {
        var imageUri = mutableStateOf<Uri?>(null)
    }
}

@Composable
fun OmniverseScreen(postModel: PostModel, nonUserModel: NonUserModel,postTileMap: MutableMap<Pair<Int, Int>, FeedData>,
                    launchImagePicker: () -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    var tileCoordinates by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedPost by remember { mutableStateOf<FeedData?>(null) }
    var showFullScreenPostView by remember { mutableStateOf(false) }
    var showNewPostDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val apiHandler = ApiHandler()

    val posts by postModel.postList.observeAsState(initial = listOf())

    val uiPostList by postModel.uiPostList.observeAsState(initial = listOf())

    Column(){
    Box(modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
        MapViewContainer(
            posts,
            postTileMap,
            mapView,
            tileCoordinates,
            context,
            updateTileCoordinates = { newTileCoordinates ->
                tileCoordinates = newTileCoordinates
            },
            onMarkerClicked = { post ->
                selectedPost = post // Update the selected post when a marker is clicked
                showFullScreenPostView = true
            }
        )
        if (showFullScreenPostView && selectedPost != null) {
            FullScreenPostView(
                selectedPost!!,
                postModel.uiPostList,
                apiHandler,
                nonUserModel,
                context,
                onBack = { showFullScreenPostView = false })
        }

        tileCoordinates?.let {
            Text(
                "Current Tile: X=${it.first}, Y=${it.second}",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        FloatingActionButton(
            onClick = { showNewPostDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Post")
        }
        if (showNewPostDialog) {
            CreatePostDialog(
                context, apiHandler, postModel,
                onPostCreate = { caption, pickedImageUri ->
                    Omniverse.imageUri.value = null // Reset the image URI
                    showNewPostDialog = false


                },
                onDismiss = {
                    Omniverse.imageUri.value = null // Reset the image URI
                    showNewPostDialog = false
                },
                launchImagePicker = launchImagePicker
            )
        }
    }
    BottomDashboard()
}
}

@Composable
fun MapViewContainer(
    posts: List<FeedData>,
    postTileMap: MutableMap<Pair<Int, Int>, FeedData>,
    mapView: MapView,
    tileCoordinates: Pair<Int, Int>?,
    context: Context,
    updateTileCoordinates: (Pair<Int, Int>) -> Unit,
    onMarkerClicked: (FeedData) -> Unit  // Add this parameter
) {
    var selectedPost by remember { mutableStateOf<FeedData?>(null) }

    AndroidView({ mapView }) { mapView ->
        mapView.getMapAsync { googleMap ->
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            //  googleMap.mapType = GoogleMap.MAP_TYPE_NONE
            googleMap.uiSettings.isZoomControlsEnabled = false  // Disable zoom controls
            googleMap.uiSettings.isZoomGesturesEnabled = false  // Disable zoom gestures
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(5.0f))

            // Initial population of the grid
            tileCoordinates?.let {
                populateGrid(googleMap, posts, postTileMap, it.first, it.second, context, onMarkerClicked)
            }

            googleMap.setOnCameraIdleListener {
                val centerLat = googleMap.cameraPosition.target.latitude
                val centerLon = googleMap.cameraPosition.target.longitude
                val zoomLevel = googleMap.cameraPosition.zoom.toInt()

                val (worldX, worldY) = latLongToWorldCoordinates(centerLat, centerLon, zoomLevel)
                val newTileCoordinates = worldToTileCoordinates(worldX, worldY)
                updateTileCoordinates(newTileCoordinates)

                // Repopulate the grid based on the new tile coordinates
                populateGrid(
                    googleMap,
                    posts,
                    postTileMap,
                    newTileCoordinates.first,
                    newTileCoordinates.second,
                    context,
                    onMarkerClicked
                )
            }
        }
    }
}
// The tile map is responsible for determining if a post is at a specific tile coordinate
var postIndex = 0
fun populateGrid(
    googleMap: GoogleMap,
    posts: List<FeedData>,
    postTileMap: MutableMap<Pair<Int, Int>, FeedData>,
    currentX: Int,
    currentY: Int,
    context: Context,
    onPostClicked: (FeedData) -> Unit
) {
    val zoomLevel = 5
    val circleSizeInPixels = 650 // Size of the circle in pixels


    for (i in -1..1) {
        for (j in -1..1) {
            val x = currentX + i + 1
            val y = currentY + j + 1
            val tileKey = Pair(x, y)

            val (lat, lon) = tileToLatLong(x, y, zoomLevel)
            val location = LatLng(lat, lon)

            val bubbleMarker: Bitmap = when {
                // Use testBubble for tiles that already have a post
                postTileMap.containsKey(tileKey) -> postBubble(
                    circleSizeInPixels,
                    postTileMap[tileKey]!!
                )

                // Assign a new post to this tile and use testBubble
                postIndex < posts.size -> {
                    val post = posts[postIndex++]
                    postTileMap[tileKey] = post
                    postBubble(circleSizeInPixels, post)
                }

                // Use blankBubble for tiles without a post
                else -> blankBubble(circleSizeInPixels, x, y)
            }
            val markerOptions = MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bubbleMarker))

            // Add the marker to the map and set its tag
            val marker = googleMap.addMarker(markerOptions)
            if (marker != null) {
                marker.tag = when {
                    postTileMap.containsKey(tileKey) -> postTileMap[tileKey]
                    postIndex < posts.size -> posts[postIndex - 1] // Because postIndex was incremented after assignment
                    else -> null
                }
            }

        }
    }
    googleMap.setOnMarkerClickListener { marker ->
        // Retrieve the post from the marker's tag
        val post = marker.tag as? FeedData

        // Display a toast message
        post?.let {
            //   Toast.makeText(context, "Clicked on post: ${it.caption}", Toast.LENGTH_SHORT).show()
            onPostClicked(it)
        }

        true // Return true to indicate that the click event is handled
    }

}

fun blankBubble(circleSizeInPixels: Int, tileX: Int, tileY: Int): Bitmap {
    val bitmap =
        Bitmap.createBitmap(circleSizeInPixels, circleSizeInPixels, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Paint for the circle
    val circlePaint = Paint().apply {
        color = android.graphics.Color.BLACK // Circle color
        style = Paint.Style.FILL
    }

    // Draw the circle
    canvas.drawCircle(
        circleSizeInPixels / 2f,
        circleSizeInPixels / 2f,
        circleSizeInPixels / 2f,
        circlePaint
    )

    // Text to be drawn on the circle
    val text = "$tileX, $tileY"

    // Paint for the text
    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE // Set the text color
        textAlign = Paint.Align.CENTER
        textSize = circleSizeInPixels / 12f // Set the text size relative to the circle size
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Make text bold
    }

    // Calculate the position for the text to be centered
    val xPos = circleSizeInPixels / 2f
    val yPos = (circleSizeInPixels / 2f - (textPaint.descent() + textPaint.ascent()) / 2)

    // Draw the text
    // canvas.drawText(text, xPos, yPos, textPaint)
    canvas.drawText("No more content :(", xPos, yPos, textPaint)

    return bitmap
}

fun postBubble(circleSizeInPixels: Int, post: FeedData): Bitmap {
    val bitmap =
        Bitmap.createBitmap(circleSizeInPixels, circleSizeInPixels, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Paint for the circle
    val circlePaint = Paint().apply {
        color = android.graphics.Color.BLACK // Circle color
        style = Paint.Style.FILL
    }

    // Draw the circle
    canvas.drawCircle(
        circleSizeInPixels / 2f,
        circleSizeInPixels / 2f,
        circleSizeInPixels / 2f,
        circlePaint
    )

    // Text to be drawn on the circle
    val text = post.caption.toString()

    // Paint for the text
    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE // Set the text color
        textAlign = Paint.Align.CENTER
        textSize = circleSizeInPixels / 20f // Set the text size relative to the circle size
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // Make text bold
    }

    // Calculate the position for the text to be centered
    val xPos = circleSizeInPixels / 2f
    val yPos = (circleSizeInPixels / 2f - (textPaint.descent() + textPaint.ascent()) / 2)

    // Draw the text
    canvas.drawText(text, xPos, yPos, textPaint)

    return bitmap
}


fun tileToLatLong(x: Int, y: Int, zoomLevel: Int): Pair<Double, Double> {
    val n = 2.0.pow(zoomLevel.toDouble())
    val lonDeg = x / n * 360.0 - 180.0
    val latRad = atan(sinh(PI * (1 - 2 * y / n)))
    val latDeg = Math.toDegrees(latRad)
    return Pair(latDeg, lonDeg)
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            // Additional map setup
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val lifecycleObserver = getMapLifecycleObserver(mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

private fun getMapLifecycleObserver(mapView: MapView) = LifecycleEventObserver { _, event ->
    when (event) {
        Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
        Lifecycle.Event.ON_START -> mapView.onStart()
        Lifecycle.Event.ON_RESUME -> mapView.onResume()
        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
        Lifecycle.Event.ON_STOP -> mapView.onStop()
        Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
        else -> throw IllegalStateException()
    }
}

fun latLongToWorldCoordinates(lat: Double, lon: Double, zoomLevel: Int): Pair<Double, Double> {
    val x = (lon + 180) / 360 * (256 * 2.0.pow(zoomLevel.toDouble()))
    val y =
        (1 - ln(tan(Math.toRadians(lat)) + 1 / cos(Math.toRadians(lat))) / Math.PI) / 2 * (256 * 2.0.pow(
            zoomLevel.toDouble()
        ))

    return Pair(x, y)
}

fun worldToTileCoordinates(worldX: Double, worldY: Double): Pair<Int, Int> {
    val tileX = floor(worldX / 256).toInt()
    val tileY = floor(worldY / 256).toInt()

    return Pair(tileX, tileY)
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
                                        val intent = Intent(context, Omniverse::class.java)
                                        context.startActivity(intent)
                                        //clearPostTileMap()
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
    val pickedImageUri = Omniverse.imageUri.value

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
                            Log.d("ImageURI", "is: $pickedImageUri")
                            apiHandler.createNewPost(username, caption, pickedImageUri, context)

                            // Right now, once you create new post it just navigates refreshes global page
                            val intent = Intent(context, Omniverse::class.java)
                            context.startActivity(intent)
                        //    clearPostTileMap()

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